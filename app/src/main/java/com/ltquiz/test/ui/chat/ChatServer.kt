/*
 * Copyright 2026 LTQuiz Test
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

package com.ltquiz.test.ui.chat

import org.nanohttpd.protocols.http.NanoHTTPD
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ChatMessage(
    val id: Int,
    val name: String,
    val text: String,
    val timestampMs: Long
)

object ChatRoom {
    private val lock = Any()
    private val nextId = AtomicInteger(0)
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())

    val messages: StateFlow<List<ChatMessage>> = _messages

    fun addMessage(name: String, text: String): ChatMessage {
        val message = ChatMessage(
            id = nextId.incrementAndGet(),
            name = name.trim(),
            text = text.trim(),
            timestampMs = System.currentTimeMillis()
        )
        synchronized(lock) {
            _messages.value = _messages.value + message
        }
        return message
    }

    fun messagesSince(id: Int): List<ChatMessage> {
        return _messages.value.filter { it.id > id }
    }
}

class ChatServer(
    port: Int
) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {
        return when (session.uri) {
            "/" -> newFixedLengthResponse(
                Response.Status.OK,
                "text/html",
                ChatWebPage.html
            )
            "/messages" -> handleMessages(session)
            "/send" -> handleSend(session)
            else -> newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found")
        }
    }

    private fun handleMessages(session: IHTTPSession): Response {
        val since = session.parameters["since"]?.firstOrNull()?.toIntOrNull() ?: 0
        val payload = ChatRoom.messagesSince(since)
        val json = buildString {
            append("{\"messages\":[")
            payload.forEachIndexed { index, message ->
                if (index > 0) append(',')
                append("{\"id\":")
                append(message.id)
                append(",\"name\":\"")
                append(message.name.escapeJson())
                append("\",\"text\":\"")
                append(message.text.escapeJson())
                append("\",\"timestampMs\":")
                append(message.timestampMs)
                append('}')
            }
            append("]}")
        }
        return newFixedLengthResponse(Response.Status.OK, "application/json", json)
    }

    private fun handleSend(session: IHTTPSession): Response {
        if (session.method != Method.POST) {
            return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, "text/plain", "POST only")
        }
        val body = HashMap<String, String>()
        try {
            session.parseBody(body)
            val name = session.parameters["name"]?.firstOrNull().orEmpty().trim()
            val text = session.parameters["text"]?.firstOrNull().orEmpty().trim()
            if (name.isNotEmpty() && text.isNotEmpty()) {
                ChatRoom.addMessage(name, text)
            }
        } catch (ex: Exception) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Bad Request")
        }
        return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"ok\":true}")
    }
}

object ChatServerManager {
    private const val PORT = 8080
    private var server: ChatServer? = null

    val port: Int = PORT

    fun startIfNeeded() {
        if (server != null) return
        server = ChatServer(PORT).apply {
            try {
                start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
            } catch (ex: Exception) {
                server = null
            }
        }
    }

    fun stop() {
        server?.stop()
        server = null
    }
}

private object ChatWebPage {
    val html = """
        <!doctype html>
        <html lang="en">
          <head>
            <meta charset="utf-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1"/>
            <title>LTQuiz Chat</title>
            <style>
              :root {
                color-scheme: light;
                --bg: #0f1b2b;
                --card: #13243a;
                --accent: #2dd4bf;
                --text: #e6edf6;
                --muted: #b5c4d8;
              }
              body {
                margin: 0;
                font-family: "Segoe UI", system-ui, sans-serif;
                background: radial-gradient(circle at 20% 20%, #223457, var(--bg));
                color: var(--text);
              }
              .wrap {
                max-width: 720px;
                margin: 0 auto;
                padding: 20px;
              }
              h1 { font-size: 22px; margin-bottom: 6px; }
              .card {
                background: var(--card);
                border-radius: 16px;
                padding: 16px;
                box-shadow: 0 18px 40px rgba(0,0,0,0.25);
              }
              #messages {
                max-height: 60vh;
                overflow-y: auto;
                display: grid;
                gap: 10px;
                margin-bottom: 12px;
              }
              .msg {
                padding: 10px 12px;
                border-radius: 12px;
                background: rgba(255,255,255,0.06);
              }
              .name { font-weight: 600; color: var(--accent); }
              .meta { font-size: 12px; color: var(--muted); }
              form {
                display: grid;
                grid-template-columns: 1fr auto;
                gap: 10px;
              }
              input {
                padding: 10px 12px;
                border-radius: 10px;
                border: 1px solid rgba(255,255,255,0.18);
                background: rgba(255,255,255,0.08);
                color: var(--text);
              }
              button {
                padding: 10px 16px;
                border-radius: 10px;
                border: none;
                background: var(--accent);
                color: #083344;
                font-weight: 700;
              }
            </style>
          </head>
          <body>
            <div class="wrap">
              <h1>LTQuiz Room</h1>
              <p class="meta">You are connected to the host on this network.</p>
              <div class="card">
                <div id="messages"></div>
                <form id="chatForm">
                  <input id="text" type="text" placeholder="Type a message" autocomplete="off" />
                  <button type="submit">Send</button>
                </form>
              </div>
            </div>
            <script>
              const name = prompt("Enter your name") || "Guest";
              let lastId = 0;
              const messagesEl = document.getElementById("messages");
              const form = document.getElementById("chatForm");
              const textInput = document.getElementById("text");

              function appendMessage(msg) {
                const wrap = document.createElement("div");
                wrap.className = "msg";
                wrap.innerHTML = `<div class="name">${msg.name}</div><div>${msg.text}</div>`;
                messagesEl.appendChild(wrap);
                messagesEl.scrollTop = messagesEl.scrollHeight;
              }

              async function poll() {
                try {
                  const res = await fetch(`/messages?since=${lastId}`);
                  const data = await res.json();
                  for (const msg of data.messages) {
                    appendMessage(msg);
                    lastId = Math.max(lastId, msg.id);
                  }
                } catch (err) {}
              }

              form.addEventListener("submit", async (e) => {
                e.preventDefault();
                const text = textInput.value.trim();
                if (!text) return;
                textInput.value = "";
                await fetch("/send", {
                  method: "POST",
                  headers: { "Content-Type": "application/x-www-form-urlencoded" },
                  body: new URLSearchParams({ name, text })
                });
                poll();
              });

              poll();
              setInterval(poll, 1000);
            </script>
          </body>
        </html>
    """.trimIndent()
}

private fun String.escapeJson(): String {
    return buildString {
        for (ch in this@escapeJson) {
            when (ch) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(ch)
            }
        }
    }
}
