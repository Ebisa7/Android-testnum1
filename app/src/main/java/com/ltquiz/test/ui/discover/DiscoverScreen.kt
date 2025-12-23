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

package com.ltquiz.test.ui.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ltquiz.test.ui.components.QuizCard

/**
 * Discover screen with search, categories, and popular quizzes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onQuizSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Search Bar
        item {
            SearchSection(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { /* Handle search */ }
            )
        }
        
        // Category Filters
        item {
            CategorySection()
        }
        
        // Popular Quizzes
        item {
            PopularQuizzesSection(
                onQuizClick = onQuizSelected
            )
        }
    }
}

@Composable
private fun SearchSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Find Your Quiz",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search for quizzes...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            singleLine = true
        )
    }
}

@Composable
private fun CategorySection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    onClick = { /* Handle category selection */ }
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: QuizCategory,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(category.name) },
        selected = false,
        leadingIcon = {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                modifier = Modifier.size(18.dp)
            )
        }
    )
}

@Composable
private fun PopularQuizzesSection(
    onQuizClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Popular Quizzes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        
        popularQuizzes.forEach { quiz ->
            QuizCard(
                title = quiz.title,
                description = quiz.description,
                questionCount = quiz.questionCount,
                duration = quiz.duration,
                onClick = { onQuizClick(quiz.id) }
            )
        }
    }
}

data class QuizCategory(
    val id: String,
    val name: String,
    val icon: ImageVector
)

data class PopularQuiz(
    val id: String,
    val title: String,
    val description: String,
    val questionCount: Int,
    val duration: String
)

private val categories = listOf(
    QuizCategory("science", "Science", Icons.Default.Info),
    QuizCategory("history", "History", Icons.Default.Search),
    QuizCategory("math", "Math", Icons.Default.Edit)
)

private val popularQuizzes = listOf(
    PopularQuiz(
        id = "basic-science",
        title = "Basic Science Quiz",
        description = "Test your fundamental science knowledge",
        questionCount = 10,
        duration = "5 minutes"
    ),
    PopularQuiz(
        id = "world-history",
        title = "World History Quiz",
        description = "Explore major historical events and figures",
        questionCount = 15,
        duration = "8 minutes"
    )
)