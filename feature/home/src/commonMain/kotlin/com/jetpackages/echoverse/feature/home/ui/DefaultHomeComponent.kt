package com.jetpackages.echoverse.feature.home.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import usecase.DeleteEchoUseCase
import usecase.GetEchosUseCase

/**
 * The default implementation of HomeComponent. This class contains the business logic
 * for the Home screen and manages its state.
 *
 * @param componentContext The Decompose context that provides lifecycle management.
 * @param getEchosUseCase The use case for retrieving the list of Echos.
 * @param deleteEchoUseCase The use case for deleting an Echo.
 */
class DefaultHomeComponent(
    private val componentContext: ComponentContext,
    private val getEchosUseCase: GetEchosUseCase,
    private val deleteEchoUseCase: DeleteEchoUseCase,
    private val onNavigateToCreateEcho: (fileName: String, content: String) -> Unit,
    private val onEchoClick: (id: String) -> Unit
) : HomeComponent, ComponentContext by componentContext {

    private val _state = MutableStateFlow(HomeState())
    override val state: StateFlow<HomeState> = _state.asStateFlow()

    private val componentScope = CoroutineScope(Dispatchers.Main)

    init {
        lifecycle.doOnCreate {
            getEchosUseCase()
                .onEach { echos ->
                    _state.value = HomeState(isLoading = false, echos = echos)
                }
                .launchIn(componentScope)
        }
    }

    override fun onDeleteEcho(id: String) {
        componentScope.launch {
            deleteEchoUseCase(id)
        }
    }

    override fun onChatFileReceived(fileName: String, content: String) {
        println("Shared logic received chat file! Name: $fileName")
        println("Shared logic content length: ${content.length} characters")
        onNavigateToCreateEcho(fileName, content)
    }

    override fun onEchoClicked(id: String) {
        this.onEchoClick(id)
    }
}