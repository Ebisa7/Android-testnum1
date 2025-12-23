# LTQuiz Test - Interactive Quiz Application

LTQuiz Test is a modern Android quiz application built with Kotlin that provides an engaging platform for users to test their knowledge across various subjects through interactive quizzes.

## App Overview

The app is designed as a comprehensive quiz platform with a clean, Material Design 3 interface featuring a white primary theme with teal accent colors (#00A37F). It supports multiple screen sizes and orientations (locked to portrait mode for consistency).

## Core Features & Pages

### 1. Splash Screen
- Initial loading screen that serves as the app's entry point
- Features custom splash theme and branding
- Automatically transitions to the main app experience

### 2. Home Page
- **Welcome Section**: Greets users with "Welcome to LTQuiz!" and describes the app's purpose
- **Quick Actions**: Two primary buttons for immediate engagement:
  - "Start Quiz" - Launches a quiz session
  - "Browse" - Navigates to quiz discovery
- **Recent Quizzes**: Displays user's quiz history (shows placeholder when no quizzes completed)
- **Navigation**: Bottom navigation bar for easy access to all main sections

### 3. Discover Page
- **Search Functionality**: Prominent search bar for finding specific quizzes
- **Category Filters**: Organized quiz categories including:
  - Science (with info icon)
  - History (with history icon)
  - Math (with edit icon)
- **Popular Quizzes**: Featured quiz cards showing:
  - "Basic Science Quiz" (10 questions, 5 minutes)
  - "World History Quiz" (15 questions, 8 minutes)
- Each quiz card displays title, description, and duration/question count

### 4. Profile Page
- **User Information**:
  - Circular profile avatar
  - User name (Test User)
  - Email address (test@ltquiz.com)
  - Quiz statistics (completed quizzes count, best score percentage)
- **Settings Menu**:
  - Edit Profile option
  - Notifications settings
  - About section
- All settings items are clickable with navigation arrows

### 5. Quiz Activity
- Dedicated quiz-taking interface
- Supports deep linking from web (ltquiz.vercel.app/quiz/)
- Handles quiz sessions and scoring

## Technical Capabilities

- **Internet Access**: Required for quiz content and updates
- **Storage Permissions**: Can read/write external storage (with SDK version restrictions)
- **Vibration**: Provides haptic feedback during interactions
- **Deep Linking**: Supports web-to-app navigation for specific quizzes
- **Multi-screen Support**: Optimized for phones, tablets, and various screen densities

## Navigation Structure

The app uses a bottom navigation system with three main tabs:
- **Home** (house icon): Main dashboard and quick actions
- **Discover** (search icon): Quiz browsing and discovery
- **Profile** (location icon): User account and settings

## Architecture

This app follows modern Android development practices:
- **Jetpack Compose** for UI
- **Navigation Compose** for single-activity architecture
- **Hilt** for dependency injection
- **Material Design 3** theming
- **MVVM** architecture pattern
- **Kotlin Coroutines** and **Flow** for reactive programming

## App Identity

- **Package**: com.ltquiz.test
- **Name**: LTQuiz Test
- **Theme**: Clean, modern Material Design with white backgrounds and teal accents
- **Orientation**: Portrait-only for consistent user experience
- **Target**: Educational quiz platform for knowledge testing and learning

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 (API level 24) or higher
- Kotlin 1.8.0 or later

### Building the App

1. Clone the repository:
```bash
git clone <repository-url>
cd ltquiz-test
```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Run the app on an emulator or physical device

### Project Structure

```
app/
├── src/main/java/com/ltquiz/test/
│   ├── data/
│   │   ├── model/          # Data models (Quiz, Question, etc.)
│   │   └── repository/     # Data repositories
│   ├── di/                 # Dependency injection modules
│   ├── ui/
│   │   ├── components/     # Reusable UI components
│   │   ├── discover/       # Discover screen
│   │   ├── home/          # Home screen
│   │   ├── profile/       # Profile screen
│   │   ├── quiz/          # Quiz screen
│   │   └── theme/         # App theming
│   ├── LTQuizApplication.kt
│   ├── MainActivity.kt
│   └── QuizActivity.kt
└── src/main/res/
    ├── values/
    │   ├── colors.xml      # App colors
    │   ├── strings.xml     # String resources
    │   └── styles.xml      # App themes and styles
    └── ...
```

## Features in Development

- Quiz question randomization
- Score tracking and analytics
- User authentication
- Online quiz synchronization
- Achievement system
- Social sharing capabilities

## Contributing

This project serves as a comprehensive quiz platform where users can discover, take, and track their performance on various educational quizzes across multiple subjects, with a focus on user-friendly navigation and engaging visual design.

## License

```
Copyright 2024 LTQuiz Test

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
