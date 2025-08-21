// In core/data/src/androidMain/kotlin/com/jetpackages/echoverse/core/data/database/DatabaseDriverFactory.kt

package com.jetpackages.echoverse.core.data.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jetpackages.echoverse.db.EchoVerseDatabase

/**
 * The actual implementation of the DatabaseDriverFactory for the Android platform.
 */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = EchoVerseDatabase.Schema,
            context = context,
            name = DB_NAME
        )
    }
}