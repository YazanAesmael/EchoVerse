package com.jetpackages.echoverse.core.data.mappers

import com.jetpackages.echoverse.core.domain.model.CommunicationRules
import com.jetpackages.echoverse.core.domain.model.CoreIdentity
import com.jetpackages.echoverse.core.domain.model.Lexicon
import com.jetpackages.echoverse.core.domain.model.PersonalityProfile
import com.jetpackages.echoverse.db.PersonalityProfileEntity

// A helper to safely split comma-separated strings into lists
private fun String.toList() = if (this.isBlank()) emptyList() else this.split(";")
private fun List<String>.fromList() = this.joinToString(";")

/**
 * Maps a PersonalityProfileEntity (flat database model) to a PersonalityProfile (nested domain model).
 */
fun PersonalityProfileEntity.toPersonalityProfile(): PersonalityProfile {
    return PersonalityProfile(
        id = this.id,
        echoId = this.echoId,
        sourceChatFileName = this.sourceChatFileName,
        coreIdentity = CoreIdentity(
            name = this.identityName,
            relationshipToUser = this.relationshipToUser,
            dailyLifeSummary = this.dailyLifeSummary
        ),
        communicationRules = CommunicationRules(
            averageMessageLength = this.avgMessageLength,
            tone = this.tone,
            capitalizationStyle = this.capitalizationStyle,
            punctuationStyle = this.punctuationStyle,
            emojiFrequency = this.emojiFrequency
        ),
        lexicon = Lexicon(
            signaturePhrases = this.signaturePhrases.toList(),
            termsOfEndearment = this.termsOfEndearment.toList(),
            topEmojis = this.topEmojis.toList()
        )
    )
}

/**
 * Maps a PersonalityProfile (nested domain model) to the parameters needed for the database insert.
 */
fun PersonalityProfile.toDbParams(): (String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> Unit {
    return { id, echoId, sourceChatFileName, identityName, relationshipToUser, dailyLifeSummary,
             avgMessageLength, tone, capitalizationStyle, punctuationStyle, emojiFrequency,
             signaturePhrases, termsOfEndearment, topEmojis ->
        // This is a placeholder for a function that would be passed to the insert query
        // For our use case, we'll call the insert directly.
    }
}