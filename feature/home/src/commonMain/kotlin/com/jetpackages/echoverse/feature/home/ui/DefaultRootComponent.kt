package com.jetpackages.echoverse.feature.home.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.jetpackages.echoverse.feature.home.chat.DefaultChatComponent
import com.jetpackages.echoverse.feature.home.chat.ui.ChatComponent
import com.jetpackages.echoverse.feature.home.create_echo.DefaultCreateEchoComponent
import com.jetpackages.echoverse.feature.home.create_echo.ui.CreateEchoComponent
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class DefaultRootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {

    private val navigation = StackNavigation<Configuration>()

    val childStack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Configuration.serializer(),
            initialConfiguration = Configuration.Home,
            handleBackButton = true,
            childFactory = ::createChild
        )

    private fun createChild(config: Configuration, context: ComponentContext): Child {
        return when (config) {
            is Configuration.Home -> Child.Home(createHomeComponent(context))
            is Configuration.CreateEcho -> Child.CreateEcho(
                createCreateEchoComponent(context, config)
            )
            is Configuration.Chat -> Child.Chat(createChatComponent(context, config))
        }
    }

    @OptIn(DelicateDecomposeApi::class)
    private fun createHomeComponent(context: ComponentContext): HomeComponent =
        DefaultHomeComponent(
            componentContext = context,
            getEchosUseCase = get(),
            deleteEchoUseCase = get(),
            onNavigateToCreateEcho = { fileName, content ->
                navigation.push(Configuration.CreateEcho(fileName, content))
            },
            onEchoClick = { echoId ->
                navigation.push(Configuration.Chat(echoId))
            }
        )

    private fun createCreateEchoComponent(
        context: ComponentContext,
        config: Configuration.CreateEcho
    ): CreateEchoComponent =
        DefaultCreateEchoComponent(
            componentContext = context,
            fileName = config.fileName,
            chatContent = config.chatContent,
            extractParticipantsUseCase = get(),
            createEchoUseCase = get(),
            onFinished = { navigation.pop() }
        )

    private fun createChatComponent(
        context: ComponentContext,
        config: Configuration.Chat
    ): ChatComponent =
        DefaultChatComponent(
            componentContext = context,
            echoId = config.echoId,
            getEchoWithProfileUseCase = get(),
            generateReplyUseCase = get(),
            getMessageHistoryUseCase = get(),
            saveMessageUseCase = get(),
            saveMemoryUseCase = get(),
            onBack = { navigation.pop() }
        )

    sealed class Child {
        data class Home(val component: HomeComponent) : Child()
        data class CreateEcho(val component: CreateEchoComponent) : Child()
        data class Chat(val component: ChatComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object Home : Configuration()

        @Serializable
        data class CreateEcho(val fileName: String, val chatContent: String) : Configuration()

        @Serializable
        data class Chat(val echoId: String) : Configuration()
    }
}