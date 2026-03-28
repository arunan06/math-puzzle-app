package com.example.mathpuzzlegame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenuScreen(
    onNewGameClick: () -> Unit,
    onAdvancedClick: () -> Unit,
    onAboutClick: () -> Unit,
    useTimer: Boolean,
    onTimerToggle: (Boolean) -> Unit
) {
    var showTutorial by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC5))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CROSS MATH\nPUZZLE",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 45.sp,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            MenuButton(
                text = "BEGINNER",
                color = Color(0xFFFF9800),
                onClick = onNewGameClick
            )

            MenuButton(
                text = "ADVANCED",
                color = Color(0xFFE91E63),
                onClick = onAdvancedClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Timer Mode (60s)", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = useTimer,
                        onCheckedChange = onTimerToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF03DAC5)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row {
                IconButton(onClick = { showTutorial = true }) {
                    Icon(Icons.Default.Info, contentDescription = "Tutorial", tint = Color.White)
                }
                TextButton(onClick = onAboutClick) {
                    Text("ABOUT", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showTutorial) {
        TutorialDialog(onDismiss = { showTutorial = false })
    }
}

@Composable
fun MenuButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 4.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TutorialDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How to Play", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("1. Fill in the empty cells to complete equations.")
                Text("2. Equations run horizontally and vertically.")
                Text("3. Tap a cell to pick a number.")
                Text("4. Green = Correct, Red = Wrong.")
                Text("5. You have 3 Hints per game!")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Got it!") }
        }
    )
}
