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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ltquiz.test.data.model.Todo
import com.ltquiz.test.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TodoFilter {
    ALL,
    ACTIVE,
    COMPLETED
}

data class TodoUiState(
    val items: List<Todo> = emptyList(),
    val filter: TodoFilter = TodoFilter.ALL,
    val searchQuery: String = ""
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    private val filter = MutableStateFlow(TodoFilter.ALL)
    private val searchQuery = MutableStateFlow("")
    private var lastHandledActionKey: String? = null
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    val uiState: StateFlow<TodoUiState> = combine(
        repository.todos,
        filter,
        searchQuery
    ) { todos, activeFilter, query ->
        val trimmedQuery = query.trim()
        val filtered = todos
            .filter { todo ->
                when (activeFilter) {
                    TodoFilter.ALL -> true
                    TodoFilter.ACTIVE -> !todo.isCompleted
                    TodoFilter.COMPLETED -> todo.isCompleted
                }
            }
            .filter { todo ->
                if (trimmedQuery.isEmpty()) {
                    true
                } else {
                    todo.title.contains(trimmedQuery, ignoreCase = true)
                }
            }

        TodoUiState(
            items = filtered,
            filter = activeFilter,
            searchQuery = trimmedQuery
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        TodoUiState()
    )

    fun setFilter(newFilter: TodoFilter) {
        filter.value = newFilter
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun addTodo(title: String) {
        viewModelScope.launch {
            repository.addTodo(title)
        }
    }

    fun toggleCompleted(todo: Todo, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleCompleted(todo, completed)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }

    fun updateTodo(todo: Todo, newTitle: String) {
        viewModelScope.launch {
            repository.updateTodo(todo, newTitle)
        }
    }

    fun handleAppAction(
        action: String?,
        title: String?,
        newTitle: String?,
        id: String?,
        query: String?,
        filterName: String?
    ) {
        val key = listOf(
            action.orEmpty(),
            title.orEmpty(),
            newTitle.orEmpty(),
            id.orEmpty(),
            query.orEmpty(),
            filterName.orEmpty()
        ).joinToString("|")

        if (key == lastHandledActionKey) {
            return
        }
        lastHandledActionKey = key

        viewModelScope.launch {
            when (action?.lowercase()) {
                "add" -> {
                    if (!title.isNullOrBlank()) {
                        repository.addTodo(title)
                    }
                }
                "edit" -> {
                    val newValue = newTitle?.trim()
                    if (!newValue.isNullOrBlank()) {
                        when (val result = resolveTodo(id, title)) {
                            is ResolveTodoResult.Found -> repository.updateTodo(result.todo, newValue)
                            ResolveTodoResult.Ambiguous ->
                                _messages.tryEmit("Multiple matching todos found. Be more specific.")
                            ResolveTodoResult.NotFound ->
                                _messages.tryEmit("No matching todo found.")
                        }
                    }
                }
                "delete" -> {
                    when (val result = resolveTodo(id, title)) {
                        is ResolveTodoResult.Found -> repository.deleteTodo(result.todo)
                        ResolveTodoResult.Ambiguous ->
                            _messages.tryEmit("Multiple matching todos found. Be more specific.")
                        ResolveTodoResult.NotFound ->
                            _messages.tryEmit("No matching todo found.")
                    }
                }
                "complete" -> {
                    when (val result = resolveTodo(id, title)) {
                        is ResolveTodoResult.Found -> repository.toggleCompleted(result.todo, true)
                        ResolveTodoResult.Ambiguous ->
                            _messages.tryEmit("Multiple matching todos found. Be more specific.")
                        ResolveTodoResult.NotFound ->
                            _messages.tryEmit("No matching todo found.")
                    }
                }
                "list" -> {
                    val filterValue = parseFilter(filterName)
                    if (filterValue != null) {
                        filter.value = filterValue
                    }
                    if (!query.isNullOrBlank()) {
                        searchQuery.value = query
                    }
                }
                "search" -> {
                    if (!query.isNullOrBlank()) {
                        searchQuery.value = query
                    }
                }
            }
        }
    }

    private suspend fun resolveTodo(id: String?, title: String?): ResolveTodoResult {
        val parsedId = id?.toLongOrNull()
        if (parsedId != null) {
            val found = repository.getById(parsedId)
            return if (found != null) ResolveTodoResult.Found(found) else ResolveTodoResult.NotFound
        }

        val query = title?.trim().orEmpty()
        if (query.isEmpty()) {
            return ResolveTodoResult.NotFound
        }

        val exactMatches = repository.findExactByTitle(query)
        if (exactMatches.size == 1) {
            return ResolveTodoResult.Found(exactMatches.first())
        }
        if (exactMatches.size > 1) {
            return ResolveTodoResult.Ambiguous
        }

        val containsMatches = repository.findByTitle(query)
        return when (containsMatches.size) {
            1 -> ResolveTodoResult.Found(containsMatches.first())
            0 -> ResolveTodoResult.NotFound
            else -> ResolveTodoResult.Ambiguous
        }
    }

    private fun parseFilter(filterName: String?): TodoFilter? {
        if (filterName.isNullOrBlank()) {
            return TodoFilter.ALL
        }
        val normalized = filterName.lowercase()
        return when {
            normalized.contains("complete") || normalized.contains("done") -> TodoFilter.COMPLETED
            normalized.contains("active") || normalized.contains("incomplete") -> TodoFilter.ACTIVE
            normalized.contains("all") -> TodoFilter.ALL
            else -> null
        }
    }
}

private sealed interface ResolveTodoResult {
    data class Found(val todo: Todo) : ResolveTodoResult
    data object NotFound : ResolveTodoResult
    data object Ambiguous : ResolveTodoResult
}
