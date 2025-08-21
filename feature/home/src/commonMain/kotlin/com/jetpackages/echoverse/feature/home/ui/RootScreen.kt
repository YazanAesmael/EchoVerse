package com.jetpackages.echoverse.feature.home.ui

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.jetpackages.echoverse.feature.home.chat.ui.ChatScreen

@Composable
fun RootScreen(component: DefaultRootComponent) {
    Children(
        stack = component.childStack,
        animation = stackAnimation(slide())
    ) { child ->
        when (val instance = child.instance) {
            is DefaultRootComponent.Child.Home -> HomeScreen(instance.component)
            is DefaultRootComponent.Child.CreateEcho -> CreateEchoScreen(instance.component)
            is DefaultRootComponent.Child.Chat -> ChatScreen(instance.component)
        }
    }
}