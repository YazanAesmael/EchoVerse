package com.jetpackages.echoverse.feature.home.ui

import kotlinx.coroutines.flow.StateFlow

/**
 * The public interface for our Home screen's ViewModel/Component.
 * This defines the contract that the UI will interact with.
 */
interface HomeComponent {
    /**
     * The reactive state of the screen. The UI will observe this to redraw itself.
     */
    val state: StateFlow<HomeState>

    /**
     * A function that the UI can call to signal a user's intent to delete an Echo.
     */
    fun onDeleteEcho(id: String)

    /**
     * A function that the UI can call to signal a user's intent to share a chat file.
     */
    fun onChatFileReceived(fileName: String, content: String)

    /**
     * A function that the UI can call to signal a user's intent to create a new Echo chat.
     */
    fun onEchoClicked(id: String)
}