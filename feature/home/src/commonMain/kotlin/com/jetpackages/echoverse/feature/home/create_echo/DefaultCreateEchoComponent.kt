package com.jetpackages.echoverse.feature.home.create_echo

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.jetpackages.echoverse.feature.home.create_echo.ui.CreateEchoComponent
import com.jetpackages.echoverse.feature.home.create_echo.ui.CreateEchoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.ChatParticipant
import usecase.CreateEchoUseCase
import usecase.ExtractParticipantsUseCase

class DefaultCreateEchoComponent(
    private val componentContext: ComponentContext,
    private val fileName: String,
    private val chatContent: String,
    private val extractParticipantsUseCase: ExtractParticipantsUseCase,
    private val createEchoUseCase: CreateEchoUseCase,
    private val onFinished: () -> Unit
) : CreateEchoComponent, ComponentContext by componentContext {

    private val _state = MutableStateFlow(CreateEchoState(fileName = fileName))
    override val state = _state.asStateFlow()

    private val componentScope = CoroutineScope(Dispatchers.Main)

    init {
        lifecycle.doOnCreate {
            componentScope.launch {
                val participants = extractParticipantsUseCase(chatContent)
                _state.update { it.copy(isLoadingParticipants = false, participants = participants) }
            }
        }
    }

    override fun onParticipantSelected(participant: ChatParticipant) {
        _state.update { it.copy(selectedParticipant = participant, echoName = participant.name) }
    }

    override fun onEchoNameChanged(name: String) {
        _state.update { it.copy(echoName = name) }
    }

    override fun onCreateEchoClicked() {
        val currentState = state.value
        if (currentState.selectedParticipant == null || currentState.echoName.isBlank()) return

        _state.update { it.copy(isCreating = true) }
        componentScope.launch {
            createEchoUseCase(
                chatContent = chatContent,
                participantToEcho = currentState.selectedParticipant.name,
                echoName = currentState.echoName,
                profilePictureUri = null, // TODO: Add picture selection
                sourceChatFileName = fileName
            )
            onFinished() // Navigate back after creation
        }
    }

    override fun onBackClicked() {
        onFinished()
    }
}