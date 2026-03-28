package com.example.mathpuzzlegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mathpuzzlegame.ui.*
import com.example.mathpuzzlegame.ui.theme.MathPuzzleGameTheme

enum class Screen {
    MENU, GAME
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize SoundManager
        SoundManager.init(this)

        setContent {
            MathPuzzleGameTheme {
                val viewModel: GameViewModel = viewModel()
                var currentScreen by remember { mutableStateOf(Screen.MENU) }
                var showAbout by remember { mutableStateOf(false) }

                when (currentScreen) {
                    Screen.MENU -> {
                        MainMenuScreen(
                            onNewGameClick = {
                                SoundManager.playClick(this)
                                viewModel.startNewGame(advanced = false)
                                currentScreen = Screen.GAME
                            },
                            onAdvancedClick = {
                                SoundManager.playClick(this)
                                viewModel.startNewGame(advanced = true)
                                currentScreen = Screen.GAME
                            },
                            onAboutClick = { 
                                SoundManager.playClick(this)
                                showAbout = true 
                            },
                            useTimer = viewModel.useTimer,
                            onTimerToggle = { viewModel.useTimer = it }
                        )
                    }
                    Screen.GAME -> {
                        GameScreen(
                            viewModel = viewModel,
                            onBack = { 
                                SoundManager.playClick(this)
                                currentScreen = Screen.MENU 
                            }
                        )
                    }
                }

                if (showAbout) {
                    AboutDialog(onDismiss = { showAbout = false })
                }
            }
        }
    }
}
