package com.jetpackages.echoverse.feature.home.create_echo.ui

import kotlinx.coroutines.flow.StateFlow
import model.ChatParticipant

data class CreateEchoState(
    val fileName: String,
    val isLoadingParticipants: Boolean = true,
    val participants: List<ChatParticipant> = emptyList(),
    val selectedParticipant: ChatParticipant? = null,
    val echoName: String = "",
    val isCreating: Boolean = false
)

interface CreateEchoComponent {
    val state: StateFlow<CreateEchoState>
    fun onParticipantSelected(participant: ChatParticipant)
    fun onEchoNameChanged(name: String)
    fun onCreateEchoClicked()
    fun onBackClicked()
}