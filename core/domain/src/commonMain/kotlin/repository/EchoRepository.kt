package repository

import model.PersonalityProfile
import kotlinx.coroutines.flow.Flow
import model.Echo

/**
 * A contract for managing the storage and retrieval of Echo entities.
 */
interface EchoRepository {

    /**
     * Retrieves a continuous stream of all Echos, ordered by creation date.
     * The list will automatically update when an Echo is added or deleted.
     */
    fun getAllEchos(): Flow<List<Echo>>

    /**
     * Creates and saves a new Echo and its associated personality profile.
     *
     * @return The newly created Echo instance.
     */
    suspend fun createEcho(
        name: String,
        profilePictureUri: String?,
        personalityProfile: PersonalityProfile
    ): Echo

    suspend fun getEchoWithProfile(id: String): Pair<Echo, PersonalityProfile>?

    /**
     * Deletes an Echo and its associated data by its unique ID.
     */
    suspend fun deleteEcho(id: String)
}