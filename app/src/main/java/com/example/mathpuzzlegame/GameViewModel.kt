package com.example.mathpuzzlegame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class CellType {
    NUMBER, OPERATOR, EQUALS, EMPTY, BLACK
}

data class GridCell(
    val type: CellType,
    val value: String = "",
    val isEditable: Boolean = false,
    val expectedValue: String = ""
)

data class Equation(
    val cells: List<Pair<Int, Int>>,
    val isHorizontal: Boolean
)

class GameViewModel : ViewModel() {
    var gridSize by mutableStateOf(5)
    var grid = mutableStateMapOf<Pair<Int, Int>, GridCell>()
    var equations = mutableListOf<Equation>()
    
    var score by mutableStateOf(0)
    var timeLeft by mutableStateOf(120) 
    var isGameOver by mutableStateOf(false)
    var useTimer by mutableStateOf(true)
    var isAdvancedMode by mutableStateOf(false)
    var hintsLeft by mutableStateOf(3)
    
    private var timerJob: Job? = null
    var equationStatuses = mutableStateMapOf<Int, Boolean?>()

    fun startNewGame(advanced: Boolean) {
        isAdvancedMode = advanced
        gridSize = 5 // Keeping it 5x5 for the classic cross-math look
        generatePuzzle()
        score = 0
        hintsLeft = 3
        timeLeft = 120
        isGameOver = false
        if (useTimer) startTimer() else timerJob?.cancel()
    }

    private fun generatePuzzle() {
        grid.clear()
        equations.clear()
        equationStatuses.clear()
        
        val tempValues = mutableMapOf<Pair<Int, Int>, String>()
        val ops = if (isAdvancedMode) listOf("+", "-", "×", "÷") else listOf("+", "-")

        // 1. Generate Horizontal Results (All Positive 1-100)
        for (r in listOf(0, 2, 4)) {
            var n1: Int
            var n2: Int
            var op: String
            var res: Int
            
            do {
                n1 = Random.nextInt(1, 51)
                op = ops.random()
                n2 = Random.nextInt(1, 51)
                
                // Ensure positive results and valid division
                if (op == "-") {
                    if (n1 < n2) { val t = n1; n1 = n2; n2 = t }
                } else if (op == "÷") {
                    n1 = n2 * Random.nextInt(1, 10) // Ensure divisibility
                }
                
                res = calculate(n1, op, n2)
            } while (res <= 0 || res > 100)
            
            tempValues[r to 0] = n1.toString()
            tempValues[r to 1] = op
            tempValues[r to 2] = n2.toString()
            tempValues[r to 3] = "="
            tempValues[r to 4] = res.toString()
            
            equations.add(Equation(listOf(r to 0, r to 1, r to 2, r to 3, r to 4), true))
        }

        // 2. Generate Vertical Results ensuring intersections
        for (c in listOf(0, 2, 4)) {
            var n1 = tempValues[0 to c]?.toIntOrNull() ?: Random.nextInt(1, 50)
            var op: String
            var n2 = tempValues[2 to c]?.toIntOrNull() ?: Random.nextInt(1, 50)
            var res: Int

            // Logic to adjust operands for existing intersections is tricky, 
            // for simplicity in this generated puzzle, we'll try to find an op that works or regenerate n2
            val possibleOps = ops.shuffled()
            var found = false
            for (testOp in possibleOps) {
                if (testOp == "÷" && (n2 == 0 || n1 % n2 != 0)) continue
                val testRes = calculate(n1, testOp, n2)
                if (testRes in 1..100) {
                    op = testOp
                    res = testRes
                    found = true
                    break
                }
            }
            
            if (!found) {
                // If no op works with existing n1, n2, we force Addition as fallback and adjust n2
                op = "+"
                res = calculate(n1, op, n2)
                if (res > 100) { n2 = 1; res = n1 + 1 }
            } else {
                op = possibleOps.first { calculate(n1, it, n2) in 1..100 } // Assigned above but for safety
                res = calculate(n1, op, n2)
            }
            
            tempValues[0 to c] = n1.toString()
            tempValues[1 to c] = op
            tempValues[2 to c] = n2.toString()
            tempValues[3 to c] = "="
            tempValues[4 to c] = res.toString()
            
            equations.add(Equation(listOf(0 to c, 1 to c, 2 to c, 3 to c, 4 to c), false))
        }

        // Fill Grid
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val value = tempValues[r to c] ?: ""
                val type = when {
                    value == "=" -> CellType.EQUALS
                    value in listOf("+", "-", "×", "÷") -> CellType.OPERATOR
                    value.isNotEmpty() -> CellType.NUMBER
                    (r == 1 && c == 1) || (r == 1 && c == 3) || (r == 3 && c == 1) || (r == 3 && c == 3) -> CellType.BLACK
                    else -> CellType.EMPTY
                }
                
                grid[r to c] = GridCell(type, value)
            }
        }

        // Hide cells to create puzzle
        val editablePositions = mutableListOf<Pair<Int, Int>>()
        grid.forEach { (pos, cell) ->
            if (cell.type == CellType.NUMBER) editablePositions.add(pos)
        }
        
        val hideCount = if (isAdvancedMode) 7 else 4
        editablePositions.shuffled().take(hideCount).forEach { pos ->
            val cell = grid[pos]!!
            grid[pos] = cell.copy(isEditable = true, value = "", expectedValue = cell.value)
        }
        
        validateEquations()
    }

    fun calculate(n1: Int, op: String, n2: Int): Int {
        return when (op) {
            "+" -> n1 + n2
            "-" -> n1 - n2
            "×" -> n1 * n2
            "÷" -> if (n2 != 0) n1 / n2 else 0
            else -> n1 + n2
        }
    }

    fun onCellInput(pos: Pair<Int, Int>, input: String) {
        if (isGameOver) return
        val cell = grid[pos] ?: return
        if (cell.isEditable) {
            if (input.isEmpty() || input.toIntOrNull() != null) {
                grid[pos] = cell.copy(value = input)
                validateEquations()
            }
        }
    }

    fun validateEquations() {
        var newScore = 0
        equations.forEachIndexed { index, eq ->
            val vals = eq.cells.map { grid[it]?.value ?: "" }
            val isFull = vals.all { it.isNotEmpty() }
            
            if (isFull) {
                val n1 = vals[0].toIntOrNull()
                val op = vals[1]
                val n2 = vals[2].toIntOrNull()
                val res = vals[4].toIntOrNull()
                
                if (n1 != null && n2 != null && res != null) {
                    val correct = calculate(n1, op, n2) == res
                    equationStatuses[index] = correct
                    if (correct) newScore += 20 // Base points per correct equation
                } else {
                    equationStatuses[index] = false
                }
            } else {
                equationStatuses[index] = null
            }
        }
        score = newScore
    }

    fun useHint() {
        if (hintsLeft > 0 && !isGameOver) {
            val blankCells = grid.entries.filter { it.value.isEditable && it.value.value != it.value.expectedValue }
            if (blankCells.isNotEmpty()) {
                val randomCell = blankCells.random()
                onCellInput(randomCell.key, randomCell.value.expectedValue)
                hintsLeft--
                score = (score - 10).coerceAtLeast(0) // Deduct points for hint
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isGameOver = true
        }
    }
}
