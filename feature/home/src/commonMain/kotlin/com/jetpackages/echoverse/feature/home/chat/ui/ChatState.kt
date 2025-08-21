package com.jetpackages.echoverse.feature.home.chat.ui

import kotlinx.coroutines.flow.StateFlow
import model.Message

data class ChatState(
    val echoName: String = "Loading...",
    val messages: List<Message> = emptyList(),
    val currentInput: String = "",
    val isEchoTyping: Boolean = false
)

interface ChatComponent {
    val state: StateFlow<ChatState>
    fun onInputChanged(text: String)
    fun onSendMessage()
    fun onBackClicked()
}