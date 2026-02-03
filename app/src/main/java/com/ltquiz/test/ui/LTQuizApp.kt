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

package com.ltquiz.test.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.navDeepLink
import com.ltquiz.test.ui.discover.DiscoverScreen
import com.ltquiz.test.ui.home.HomeScreen
import com.ltquiz.test.ui.profile.ProfileScreen
import com.ltquiz.test.ui.chat.ChatScreen
import com.ltquiz.test.ui.todo.TodoScreen

/**
 * Main app composable with bottom navigation
 * Provides navigation between Home, Discover, and Profile screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LTQuizApp() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToQuiz = { quizId ->
                        // Handle quiz navigation
                    },
                    onNavigateToDiscover = {
                        navController.navigate("discover")
                    }
                )
            }
            composable("discover") {
                DiscoverScreen(
                    onQuizSelected = { quizId ->
                        // Handle quiz selection
                    }
                )
            }
            composable("profile") {
                ProfileScreen()
            }
            composable("chat") {
                ChatScreen()
            }
            composable(
                route = "todo?filter={filter}&query={query}&action={action}&id={id}&title={title}&newTitle={newTitle}",
                arguments = listOf(
                    navArgument("filter") { type = NavType.StringType; defaultValue = "" },
                    navArgument("query") { type = NavType.StringType; defaultValue = "" },
                    navArgument("action") { type = NavType.StringType; defaultValue = "" },
                    navArgument("id") { type = NavType.StringType; defaultValue = "" },
                    navArgument("title") { type = NavType.StringType; defaultValue = "" },
                    navArgument("newTitle") { type = NavType.StringType; defaultValue = "" }
                ),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "ltquiz://todo" },
                    navDeepLink { uriPattern = "ltquiz://todo?action=add&title={title}" },
                    navDeepLink { uriPattern = "ltquiz://todo?action=search&query={query}" },
                    navDeepLink { uriPattern = "ltquiz://todo?action=list&filter={filter}" },
                    navDeepLink { uriPattern = "ltquiz://todo?action=complete&title={title}" },
                    navDeepLink { uriPattern = "ltquiz://todo?action=delete&title={title}" },
                    navDeepLink { uriPattern = "ltquiz://todo?action=edit&title={title}&newTitle={newTitle}" }
                )
            ) { entry ->
                TodoScreen(
                    action = entry.arguments?.getString("action").orEmpty(),
                    title = entry.arguments?.getString("title").orEmpty(),
                    newTitle = entry.arguments?.getString("newTitle").orEmpty(),
                    id = entry.arguments?.getString("id").orEmpty(),
                    query = entry.arguments?.getString("query").orEmpty(),
                    filter = entry.arguments?.getString("filter").orEmpty()
                )
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

private val bottomNavItems = listOf(
    BottomNavItem("home", Icons.Default.Home, "Home"),
    BottomNavItem("chat", Icons.Default.Chat, "Chat"),
    BottomNavItem("todo", Icons.Default.List, "Todo"),
    BottomNavItem("profile", Icons.Default.LocationOn, "Profile")
)
