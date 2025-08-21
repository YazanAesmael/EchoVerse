package com.jetpackages.echoverse.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetpackages.echoverse.feature.home.ui.components.EchoCard

/**
 * The main screen of the application, displaying the Echo Library.
 * This is a "smart" Composable that is aware of its component/ViewModel.
 *
 * @param component The HomeComponent instance that provides state and handles logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(component: HomeComponent) {
    // Collect the state from the component as a Compose State object.
    // The UI will automatically recompose whenever this state changes.
    val state by component.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("EchoVerse") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Trigger chat import flow */ }) {
                Icon(Icons.Default.Add, contentDescription = "Create new Echo")
            }
        }
    ) { paddingValues ->
        when {
            // Loading State
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Empty State
            state.echos.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No Echos yet.")
                        Text("Tap the '+' to create your first one!")
                    }
                }
            }
            // Content State
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.echos, key = { it.id }) { echo ->
                        EchoCard(
                            echo = echo,
                            onClick = { component.onEchoClicked(echo.id) },
                            onDeleteClick = { component.onDeleteEcho(echo.id) }
                        )
                    }
                }
            }
        }
    }
}