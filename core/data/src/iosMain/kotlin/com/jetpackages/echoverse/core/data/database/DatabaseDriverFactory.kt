// In core/data/src/iosMain/kotlin/com/jetpackages/echoverse/core/data/database/DatabaseDriverFactory.kt

package com.jetpackages.echoverse.core.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.jetpackages.echoverse.db.EchoVerseDatabase

/**
 * The actual implementation of the DatabaseDriverFactory for the iOS platform.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = EchoVerseDatabase.Schema,
            name = DB_NAME
        )
    }
}