package com.jetpackages.echoverse.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jetpackages.echoverse.core.data.mappers.toEcho
import com.jetpackages.echoverse.core.data.mappers.toPersonalityProfile
import model.PersonalityProfile
import com.jetpackages.echoverse.db.EchoVerseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import model.Echo
import repository.EchoRepository

class EchoRepositoryImpl(
    private val db: EchoVerseDatabase
) : EchoRepository {

    private val echoQueries = db.echoEntityQueries
    private val profileQueries = db.personalityProfileEntityQueries

    override fun getAllEchos(): Flow<List<Echo>> {
        return echoQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { echoEntityList ->
                echoEntityList.map { it.toEcho() }
            }
    }

    override suspend fun createEcho(
        name: String,
        profilePictureUri: String?,
        personalityProfile: PersonalityProfile
    ): Echo {
        val newEcho = Echo(
            id = personalityProfile.echoId,
            name = name,
            profilePictureUri = profilePictureUri,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )

        db.transaction {
            echoQueries.insert(
                id = newEcho.id,
                name = newEcho.name,
                profilePictureUri = newEcho.profilePictureUri,
                createdAt = newEcho.createdAt
            )
            profileQueries.insert(
                id = personalityProfile.id,
                echoId = personalityProfile.echoId,
                sourceChatFileName = personalityProfile.sourceChatFileName,
                identityName = personalityProfile.coreIdentity.name,
                relationshipToUser = personalityProfile.coreIdentity.relationshipToUser,
                dailyLifeSummary = personalityProfile.coreIdentity.dailyLifeSummary,
                avgMessageLength = personalityProfile.communicationRules.averageMessageLength,
                tone = personalityProfile.communicationRules.tone,
                capitalizationStyle = personalityProfile.communicationRules.capitalizationStyle,
                punctuationStyle = personalityProfile.communicationRules.punctuationStyle,
                emojiFrequency = personalityProfile.communicationRules.emojiFrequency,
                signaturePhrases = personalityProfile.lexicon.signaturePhrases.joinToString(";"),
                termsOfEndearment = personalityProfile.lexicon.termsOfEndearment.joinToString(";"),
                topEmojis = personalityProfile.lexicon.topEmojis.joinToString(";")
            )
        }

        return newEcho
    }

    override suspend fun getEchoWithProfile(id: String): Pair<Echo, PersonalityProfile>? {
        return withContext(Dispatchers.IO) {
            // 1. Execute the query and fetch the single result.
            //    The `executeAsOneOrNull()` function is the correct one for this.
            val echoEntity = echoQueries.getById(id).executeAsOneOrNull() ?: return@withContext null

            // 2. Do the same for the profile.
            val profileEntity = profileQueries.getProfileForEcho(id).executeAsOneOrNull() ?: return@withContext null

            // 3. If both are found, map them and return the Pair.
            echoEntity.toEcho() to profileEntity.toPersonalityProfile()
        }
    }

    override suspend fun deleteEcho(id: String) {
        db.transaction {
            echoQueries.deleteById(id)
            profileQueries.deleteByEchoId(id)
            db.messageEntityQueries.deleteHistoryForEcho(id)
            db.memoryFragmentEntityQueries.deleteForEcho(id)
        }
    }
}