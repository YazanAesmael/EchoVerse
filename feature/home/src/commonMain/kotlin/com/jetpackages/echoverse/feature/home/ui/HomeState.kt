package com.jetpackages.echoverse.feature.home.ui

import model.Echo

/**
 * Represents the immutable state of the Home screen.
 *
 * @property isLoading True when the list of Echos is being fetched for the first time.
 * @property echos The current list of Echos to display.
 */
data class HomeState(
    val isLoading: Boolean = true,
    val echos: List<Echo> = emptyList()
)