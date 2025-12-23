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

package com.ltquiz.test

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ltquiz.test.ui.quiz.QuizScreen
import com.ltquiz.test.ui.theme.LTQuizTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Dedicated Quiz Activity for deep linking support
 * Handles quiz sessions from web links (ltquiz.vercel.app/quiz/)
 */
@AndroidEntryPoint
class QuizActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Handle deep link data
        val quizId = extractQuizIdFromIntent(intent)
        Timber.d("QuizActivity started with quizId: $quizId")
        
        setContent {
            LTQuizTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuizScreen(
                        quizId = quizId,
                        onNavigateBack = { finish() }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        val quizId = extractQuizIdFromIntent(intent)
        Timber.d("QuizActivity new intent with quizId: $quizId")
        // Handle new quiz if needed
    }

    private fun extractQuizIdFromIntent(intent: Intent): String? {
        return when (intent.action) {
            Intent.ACTION_VIEW -> {
                intent.data?.lastPathSegment
            }
            else -> {
                intent.getStringExtra("quiz_id")
            }
        }
    }
}