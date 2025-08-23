package com.jetpackages.echoverse.feature.home.chat

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.jetpackages.echoverse.feature.home.chat.ui.ChatComponent
import com.jetpackages.echoverse.feature.home.chat.ui.ChatState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import model.Message
import model.PersonalityProfile
import usecase.GenerateReplyUseCase
import usecase.GetEchoWithProfileUseCase
import usecase.GetMessageHistoryUseCase
import usecase.SaveMessageUseCase

class DefaultChatComponent(
    componentContext: ComponentContext,
    private val echoId: String,
    private val getEchoWithProfileUseCase: GetEchoWithProfileUseCase,
    private val generateReplyUseCase: GenerateReplyUseCase,
    private val getMessageHistoryUseCase: GetMessageHistoryUseCase,
    private val saveMessageUseCase: SaveMessageUseCase,
    private val onBack: () -> Unit
) : ChatComponent, ComponentContext by componentContext {

    private val _state = MutableStateFlow(ChatState())
    override val state = _state.asStateFlow()

    private var personalityProfile: PersonalityProfile? = null
    private val componentScope = CoroutineScope(Dispatchers.Main)

    init {
        lifecycle.doOnCreate {
            // Load the Echo's profile and name
            componentScope.launch {
                getEchoWithProfileUseCase(echoId)?.let { (echo, profile) ->
                    personalityProfile = profile
                    _state.update { it.copy(echoName = echo.name) }
                }
            }

            // Start observing the message history from the database.
            getMessageHistoryUseCase(echoId)
                .onEach { history ->
                    _state.update { it.copy(messages = history) }
                }
                .launchIn(componentScope)
        }
    }

    override fun onInputChanged(text: String) {
        _state.update { it.copy(currentInput = text) }
    }

    override fun onSendMessage() {
        val userInput = state.value.currentInput
        val profile = personalityProfile
        if (userInput.isBlank() || profile == null) return

        val userMessage = Message(userInput, true, Clock.System.now().toEpochMilliseconds())

        _state.update { it.copy(currentInput = "") }

        componentScope.launch {
            // 1. Save the user's message.
            saveMessageUseCase(echoId, userMessage)

            // 2. Show the "typing" indicator.
            _state.update { it.copy(isEchoTyping = true) }

            // 3. Generate a reply using the new "Exemplar Engine".
            val reply = generateReplyUseCase(
                profile = profile,
                history = state.value.messages,
                userMessage = userInput
            )

            // 4. Save the AI's reply.
            saveMessageUseCase(echoId, reply)

            // 5. Hide the "typing" indicator.
            _state.update { it.copy(isEchoTyping = false) }
        }
    }

    override fun onBackClicked() {
        onBack()
    }
}