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

import com.ltquiz.test.data.local.TodoDao
import com.ltquiz.test.data.model.Todo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {

    val todos: Flow<List<Todo>> = todoDao.observeAll()

    suspend fun addTodo(title: String): Long {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) {
            return 0
        }
        return todoDao.insert(
            Todo(
                title = trimmed
            )
        )
    }

    suspend fun updateTodo(todo: Todo, newTitle: String) {
        val trimmed = newTitle.trim()
        if (trimmed.isEmpty()) {
            return
        }
        todoDao.update(
            todo.copy(
                title = trimmed,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleCompleted(todo: Todo, completed: Boolean) {
        todoDao.update(
            todo.copy(
                isCompleted = completed,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteTodo(todo: Todo) {
        todoDao.delete(todo)
    }

    suspend fun getById(id: Long): Todo? {
        return todoDao.getById(id)
    }

    suspend fun findByTitle(query: String): List<Todo> {
        return if (query.isBlank()) {
            emptyList()
        } else {
            todoDao.findByTitle(query.trim())
        }
    }
}
