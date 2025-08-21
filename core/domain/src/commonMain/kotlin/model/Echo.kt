package model

/**
 * Represents a fully created and interactive AI companion.
 * This is the primary user-facing entity.
 *
 * @property id A unique identifier for the Echo.
 * @property name The user-given name for the Echo (e.g., "Mom", "Best Friend").
 * @property profilePictureUri A local URI string pointing to the Echo's profile picture.
 * @property createdAt The timestamp of when the Echo was created.
 */
data class Echo(
    val id: String,
    val name: String,
    val profilePictureUri: String?,
    val createdAt: Long
)