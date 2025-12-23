/*
 * Copyright 2024 LTQuiz Test
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltquiz.test.data.repository

import com.ltquiz.test.data.model.Quiz
import com.ltquiz.test.data.model.Question
import com.ltquiz.test.data.model.QuizResult
import com.ltquiz.test.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing quiz data
 * In a real app, this would interface with Room database and network APIs
 */
@Singleton
class QuizRepository @Inject constructor() {

    private val _quizzes = MutableStateFlow(sampleQuizzes)
    val quizzes: Flow<List<Quiz>> = _quizzes.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: Flow<UserProfile> = _userProfile.asStateFlow()

    private val _quizResults = MutableStateFlow<List<QuizResult>>(emptyList())
    val quizResults: Flow<List<QuizResult>> = _quizResults.asStateFlow()

    suspend fun getQuizById(id: String): Quiz? {
        return _quizzes.value.find { it.id == id }
    }

    suspend fun getPopularQuizzes(): List<Quiz> {
        return _quizzes.value.filter { it.isPopular }
    }

    suspend fun getQuizzesByCategory(category: String): List<Quiz> {
        return _quizzes.value.filter { it.category.equals(category, ignoreCase = true) }
    }

    suspend fun searchQuizzes(query: String): List<Quiz> {
        return _quizzes.value.filter { 
            it.title.contains(query, ignoreCase = true) || 
            it.description.contains(query, ignoreCase = true) 
        }
    }

    suspend fun submitQuizResult(result: QuizResult) {
        val currentResults = _quizResults.value.toMutableList()
        currentResults.add(result)
        _quizResults.value = currentResults

        // Update user profile
        val currentProfile = _userProfile.value
        val newProfile = currentProfile.copy(
            completedQuizzes = currentProfile.completedQuizzes + 1,
            bestScore = maxOf(currentProfile.bestScore, result.percentage),
            totalScore = currentProfile.totalScore + result.score
        )
        _userProfile.value = newProfile
    }

    suspend fun getRecentQuizResults(limit: Int = 5): List<QuizResult> {
        return _quizResults.value
            .sortedByDescending { it.completedAt }
            .take(limit)
    }
}

// Sample data for demonstration
private val sampleQuizzes = listOf(
    Quiz(
        id = "basic-science",
        title = "Basic Science Quiz",
        description = "Test your fundamental science knowledge",
        category = "Science",
        questionCount = 10,
        duration = "5 minutes",
        isPopular = true,
        questions = listOf(
            Question(
                id = "q1",
                text = "What is the chemical symbol for water?",
                options = listOf("H2O", "CO2", "NaCl", "O2"),
                correctAnswerIndex = 0,
                explanation = "Water is composed of two hydrogen atoms and one oxygen atom, hence H2O."
            ),
            Question(
                id = "q2",
                text = "Which planet is closest to the Sun?",
                options = listOf("Venus", "Mercury", "Earth", "Mars"),
                correctAnswerIndex = 1,
                explanation = "Mercury is the innermost planet in our solar system."
            )
        )
    ),
    Quiz(
        id = "world-history",
        title = "World History Quiz",
        description = "Explore major historical events and figures",
        category = "History",
        questionCount = 15,
        duration = "8 minutes",
        isPopular = true,
        questions = listOf(
            Question(
                id = "h1",
                text = "In which year did World War II end?",
                options = listOf("1944", "1945", "1946", "1947"),
                correctAnswerIndex = 1,
                explanation = "World War II ended in 1945 with the surrender of Japan."
            ),
            Question(
                id = "h2",
                text = "Who was the first President of the United States?",
                options = listOf("Thomas Jefferson", "John Adams", "George Washington", "Benjamin Franklin"),
                correctAnswerIndex = 2,
                explanation = "George Washington was the first President of the United States, serving from 1789 to 1797."
            )
        )
    ),
    Quiz(
        id = "basic-math",
        title = "Basic Mathematics",
        description = "Test your mathematical skills",
        category = "Math",
        questionCount = 12,
        duration = "6 minutes",
        isPopular = false,
        questions = listOf(
            Question(
                id = "m1",
                text = "What is 15 + 27?",
                options = listOf("42", "41", "43", "40"),
                correctAnswerIndex = 0,
                explanation = "15 + 27 = 42"
            )
        )
    )
)