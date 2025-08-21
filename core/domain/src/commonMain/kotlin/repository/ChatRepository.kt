package repository

import com.jetpackages.echoverse.core.domain.model.PersonalityProfile
import model.Message

interface ChatRepository {
    /**
     * Generates a reply from the AI based on the provided context.
     * @return The AI's generated Message.
     */
    suspend fun getReply(
        profile: PersonalityProfile,
        history: List<Message>,
        userMessage: String
    ): Message
}