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

package com.ltquiz.test.data.model

/**
 * Data model for a quiz
 */
data class Quiz(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val questionCount: Int,
    val duration: String,
    val questions: List<Question> = emptyList(),
    val isPopular: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Data model for a quiz question
 */
data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String? = null
)

/**
 * Data model for quiz result
 */
data class QuizResult(
    val quizId: String,
    val score: Int,
    val totalQuestions: Int,
    val timeSpent: Long,
    val completedAt: Long = System.currentTimeMillis()
) {
    val percentage: Int
        get() = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
}

/**
 * Data model for user profile
 */
data class UserProfile(
    val id: String = "test_user",
    val name: String = "Test User",
    val email: String = "test@ltquiz.com",
    val completedQuizzes: Int = 0,
    val bestScore: Int = 0,
    val totalScore: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)