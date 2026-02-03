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

package com.ltquiz.test.ui.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ltquiz.test.data.model.Todo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    action: String?,
    title: String?,
    newTitle: String?,
    id: String?,
    query: String?,
    filter: String?,
    modifier: Modifier = Modifier,
    viewModel: TodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var newTodoTitle by rememberSaveable { mutableStateOf("") }
    var editingTodo by remember { mutableStateOf<Todo?>(null) }
    var editingTitle by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(action, title, newTitle, id, query, filter) {
        viewModel.handleAppAction(
            action = action,
            title = title,
            newTitle = newTitle,
            id = id,
            query = query,
            filterName = filter
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todos") }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = newTodoTitle,
                    onValueChange = { newTodoTitle = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Add a task") },
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        viewModel.addTodo(newTodoTitle)
                        newTodoTitle = ""
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::setSearchQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search todos") },
                singleLine = true
            )

            FilterRow(
                current = uiState.filter,
                onFilterSelected = viewModel::setFilter
            )

            if (uiState.items.isEmpty()) {
                Text(
                    text = "No todos yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.items, key = { it.id }) { todo ->
                        TodoRow(
                            todo = todo,
                            onToggle = { completed ->
                                viewModel.toggleCompleted(todo, completed)
                            },
                            onEdit = {
                                editingTodo = todo
                                editingTitle = todo.title
                            },
                            onDelete = { viewModel.deleteTodo(todo) }
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.messages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (editingTodo != null) {
        AlertDialog(
            onDismissRequest = { editingTodo = null },
            title = { Text("Edit task") },
            text = {
                OutlinedTextField(
                    value = editingTitle,
                    onValueChange = { editingTitle = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = editingTodo
                        if (target != null) {
                            viewModel.updateTodo(target, editingTitle)
                        }
                        editingTodo = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTodo = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun FilterRow(
    current: TodoFilter,
    onFilterSelected: (TodoFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterChip(
            selected = current == TodoFilter.ALL,
            onClick = { onFilterSelected(TodoFilter.ALL) },
            label = { Text("All") }
        )
        FilterChip(
            selected = current == TodoFilter.ACTIVE,
            onClick = { onFilterSelected(TodoFilter.ACTIVE) },
            label = { Text("Active") }
        )
        FilterChip(
            selected = current == TodoFilter.COMPLETED,
            onClick = { onFilterSelected(TodoFilter.COMPLETED) },
            label = { Text("Completed") }
        )
    }
}

@Composable
private fun TodoRow(
    todo: Todo,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = todo.isCompleted,
            onCheckedChange = onToggle
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = todo.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (todo.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (todo.isCompleted) "Completed" else "Active",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}
