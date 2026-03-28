package com.example.mathpuzzlegame.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mathpuzzlegame.CellType
import com.example.mathpuzzlegame.GameViewModel
import com.example.mathpuzzlegame.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    val gridSize = viewModel.gridSize

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cross Math Puzzle") },
                navigationIcon = {
                    IconButton(onClick = { 
                        SoundManager.playClick(context)
                        onBack() 
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        SoundManager.playClick(context)
                        viewModel.startNewGame(viewModel.isAdvancedMode) 
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Score: ${viewModel.score}", style = MaterialTheme.typography.titleLarge)
                if (viewModel.useTimer) {
                    Text(
                        "Time: ${viewModel.timeLeft}",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (viewModel.timeLeft < 10) Color.Red else Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Grid
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.2f))
                    .padding(4.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridSize),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(gridSize * gridSize) { index ->
                        val r = index / gridSize
                        val c = index % gridSize
                        val cell = viewModel.grid[r to c]

                        if (cell != null) {
                            val isCorrect = isCellPartOfCorrectEquation(r, c, viewModel)
                            val isWrong = isCellPartOfWrongEquation(r, c, viewModel)
                            
                            GridCellView(
                                cell = cell,
                                isCorrect = isCorrect,
                                isWrong = isWrong,
                                onClick = {
                                    if (cell.isEditable && !viewModel.isGameOver) {
                                        SoundManager.playClick(context)
                                        selectedCell = r to c
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (selectedCell != null) {
        val currentVal = viewModel.grid[selectedCell!!]?.value ?: ""
        if (viewModel.isAdvancedMode) {
            AdvancedNumberInputDialog(
                initialValue = currentVal,
                onDismiss = { selectedCell = null },
                onConfirm = { newValue ->
                    handleInput(newValue, selectedCell!!, viewModel, context)
                    selectedCell = null 
                }
            )
        } else {
            BeginnerNumberPickerDialog(
                initialValue = currentVal,
                onDismiss = { selectedCell = null },
                onConfirm = { newValue ->
                    handleInput(newValue, selectedCell!!, viewModel, context)
                    selectedCell = null 
                }
            )
        }
    }

    if (viewModel.isGameOver && viewModel.useTimer) {
        AlertDialog(
            onDismissRequest = onBack,
            title = { Text("GAME OVER!") },
            text = { Text("Final Score: ${viewModel.score}") },
            confirmButton = {
                Button(onClick = onBack) { Text("OK") }
            }
        )
    }
}

@Composable
fun BeginnerNumberPickerDialog(
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf(initialValue) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Pick a Number (1-99)",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (text.isEmpty()) "Select..." else text,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(keys) { key ->
                        Button(
                            onClick = {
                                SoundManager.playClick(context)
                                when (key) {
                                    "C" -> text = ""
                                    "OK" -> if (text.isNotEmpty() && text.toInt() in 1..99) onConfirm(text)
                                    else -> {
                                        if (text.length < 2) {
                                            val newVal = text + key
                                            if (newVal.toInt() in 1..99) text = newVal
                                        }
                                    }
                                }
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = if (key == "OK") ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                     else if (key == "C") ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                                     else ButtonDefaults.buttonColors()
                        ) {
                            Text(key, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                TextButton(onClick = { 
                    SoundManager.playClick(context)
                    onDismiss() 
                }, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun AdvancedNumberInputDialog(
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf(initialValue) }
    var error by remember { mutableStateOf<String?>(null) }
    val numericRegex = Regex("^[0-9]+$")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Advanced Input") },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        if (it.isEmpty() || numericRegex.matches(it)) {
                            text = it
                            error = null
                        }
                    },
                    label = { Text("Enter multi-digit number") },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (text.isEmpty() || !numericRegex.matches(text)) {
                    SoundManager.playClick(context)
                    error = "Invalid positive integer"
                } else {
                    onConfirm(text)
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { 
                SoundManager.playClick(context)
                onDismiss() 
            }) {
                Text("Cancel")
            }
        }
    )
}

fun handleInput(input: String, pos: Pair<Int, Int>, viewModel: GameViewModel, context: android.content.Context) {
    val oldScore = viewModel.score
    viewModel.onCellInput(pos, input)
    if (viewModel.score > oldScore) {
        SoundManager.playSuccess(context)
    }
}

fun isCellPartOfCorrectEquation(r: Int, c: Int, viewModel: GameViewModel): Boolean {
    return viewModel.equations.indices.any { index ->
        viewModel.equationStatuses[index] == true && viewModel.equations[index].cells.contains(r to c)
    }
}

fun isCellPartOfWrongEquation(r: Int, c: Int, viewModel: GameViewModel): Boolean {
    return viewModel.equations.indices.any { index ->
        viewModel.equationStatuses[index] == false && viewModel.equations[index].cells.contains(r to c)
    }
}

@Composable
fun GridCellView(
    cell: com.example.mathpuzzlegame.GridCell,
    isCorrect: Boolean,
    isWrong: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            cell.type == CellType.BLACK -> Color.Black
            isCorrect -> Color(0xFFC8E6C9)
            isWrong -> Color(0xFFFFCDD2)
            else -> Color.White
        }, label = "color"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(bgColor)
            .clickable(enabled = cell.type != CellType.BLACK, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = cell.value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (cell.type == CellType.BLACK) Color.White else Color.Black
        )
    }
}
