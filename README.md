Cross-Math Puzzle Game
A modern math puzzle game built with Kotlin & Jetpack Compose
✨ I’m vibe coding this project! ✨

📌 Project Overview
This is an Android puzzle game where users solve math equations arranged in a crossword-style grid. The goal is to fill in the missing numbers so that all horizontal and vertical equations are correct.

The game is designed with Beginner and Advanced modes, a modern UI, sound effects, and real-time validation for an engaging user experience.

🎯 Features
✔ Beginner & Advanced Modes

Beginner: Smaller grid, fewer blanks.
Advanced: Larger grid, more blanks, more challenging.
✔ Hint System

Get up to 3 hints per game to reveal correct answers.
✔ Positive Numbers Only

All numbers are between 1 and 100 (no negative numbers).
✔ Sound Effects

Click sound when selecting a cell.
Success sound for correct answers.
Error sound for wrong answers.
✔ Modern UI

Clean, colorful design with animations.
Highlight correct equations in green, incorrect in red.
✔ Game Controls

New Game, Advanced Level, About, Hint buttons.
Score and Timer displayed on screen.
✔ State Management

Preserves game state on orientation change using ViewModel and rememberSaveable.
🛠 Tech Stack
Language: Kotlin
UI Framework: Jetpack Compose
Architecture: MVVM
Sound: MediaPlayer / ExoPlayer
State Management: ViewModel + Compose State
📂 Project Structure

app/
 ├── ui/           # Jetpack Compose UI components
 ├── viewmodel/    # Game logic and state management
 ├── model/        # Data models for grid, equations
 ├── sound/        # Sound effect handling
 └── MainActivity.kt
🚀 How to Run
Clone the repository:

git clone https://github.com/your-username/cross-math-puzzle.git
Open in Android Studio.
Build and run on an emulator or physical device.
📸 Screenshots![WhatsApp Image 2026-03-28 at 9 32 21 PM](https://github.com/user-attachments/assets/d5b09f74-5a85-4d3c-8a56-b3b927f75bbc)  ![WhatsApp Image 2026-03-28 at 9 32 22 PM](https://github.com/user-attachments/assets/ac043cfb-4160-4667-8c2e-471004897600)


(Add screenshots of Beginner and Advanced modes here)

✅ Future Enhancements
Add Leaderboard and Achievements.
Add Dark Mode.
Add Custom Grid Size option.
💡 Why This Project?
Because math puzzles are fun, and I’m vibe coding this project to make learning math exciting! 🎧🔥
