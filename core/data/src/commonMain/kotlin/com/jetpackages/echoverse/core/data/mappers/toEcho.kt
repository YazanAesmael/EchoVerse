package com.jetpackages.echoverse.core.data.mappers

import com.jetpackages.echoverse.db.EchoEntity
import model.Echo

/**
 * Maps an EchoEntity (database model) to an Echo (domain model).
 */
fun EchoEntity.toEcho(): Echo {
    return Echo(
        id = this.id,
        name = this.name,
        profilePictureUri = this.profilePictureUri,
        createdAt = this.createdAt
    )
}