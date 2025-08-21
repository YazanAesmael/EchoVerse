package com.jetpackages.echoverse.core.data.database

import app.cash.sqldelight.db.SqlDriver
import com.jetpackages.echoverse.db.EchoVerseDatabase

/**
 * Executes the create statements for the entire database schema.
 * This is used during the initial database creation.
 */
fun createDatabaseSchema(driver: SqlDriver) {
    EchoVerseDatabase.Schema.create(driver)
}