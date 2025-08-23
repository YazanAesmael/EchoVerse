// In core/data/src/commonMain/kotlin/com/jetpackages/echoverse/core/data/database/DatabaseDriverFactory.kt

package com.jetpackages.echoverse.core.data.database

import app.cash.sqldelight.db.SqlDriver
import com.jetpackages.echoverse.db.EchoVerseDatabase

const val DB_NAME = "echoverse_v2.db"

/**
 * An expected factory for creating a platform-specific SQLDriver.
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

/**
 * A common helper function to create the database instance using a given driver factory.
 */
fun createDatabase(driverFactory: DatabaseDriverFactory): EchoVerseDatabase {
    val driver = driverFactory.createDriver()

    // We instantiate the Adapter that is nested inside the generated MessageEntity data class.
    return EchoVerseDatabase(
        driver = driver
    )
}