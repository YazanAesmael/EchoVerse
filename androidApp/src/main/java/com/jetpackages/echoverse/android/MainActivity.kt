package com.jetpackages.echoverse.android

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import com.jetpackages.echoverse.core.ui.theme.EchoVerseTheme
import com.jetpackages.echoverse.feature.home.ui.DefaultRootComponent
import com.jetpackages.echoverse.feature.home.ui.RootScreen // <-- NEW IMPORT
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.ZipInputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = DefaultRootComponent(
            componentContext = defaultComponentContext()
        )

        // The share intent now needs to be handled by the root component
        handleShareIntent(intent, root)

        setContent {
            EchoVerseTheme {
                // We now call the RootScreen, which handles all navigation
                RootScreen(component = root)
            }
        }
    }

    private fun unzipAndReadChatTxt(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val zipInputStream = ZipInputStream(inputStream)
            var zipEntry = zipInputStream.nextEntry

            Log.d("MainActivityTag", "Unzipping and reading chat file...")

            while (zipEntry != null) {
                // --- CRITICAL FIX 1: Log the CURRENT entry's name ---
                Log.d("MainActivityTag", "Found entry in zip: ${zipEntry.name}")

                // --- CRITICAL FIX 2: Look for ANY .txt file, not a specific name ---
                if (!zipEntry.isDirectory && zipEntry.name.endsWith(".txt")) {
                    Log.d("MainActivityTag", "Found chat file: ${zipEntry.name}. Reading content...")
                    // We found the chat file! Read its content.
                    return BufferedReader(InputStreamReader(zipInputStream)).readText()
                }

                // --- CRITICAL FIX 3: Advance to the next entry AFTER processing ---
                zipEntry = zipInputStream.nextEntry
            }
            // If we get here, no .txt file was found in the zip
            Log.w("MainActivityTag", "No .txt file found in the zip archive.")
            null
        } catch (e: Exception) {
            Log.e("MainActivityTag", "Error while unzipping and reading chat file", e)
            null
        }
    }

    // ... (The rest of the MainActivity, including handleShareIntent, readTextFromUri, and getFileName, is correct and does not need to be changed)
    private fun handleShareIntent(intent: Intent?, root: DefaultRootComponent) {
        if (intent?.action != Intent.ACTION_SEND) return

        val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
        }

        if (uri == null) return

        val fileName = getFileName(uri)
        var content: String? = null

        when (intent.type) {
            "application/zip", "application/x-zip-compressed" -> { // Handle both common zip MIME types
                Log.d("MainActivityTag", "Processing zip file: $fileName")
                content = unzipAndReadChatTxt(uri)
            }
            "text/plain" -> {
                Log.d("MainActivityTag", "Processing text file: $fileName")
                content = readTextFromUri(uri)
            }
        }

        if (content != null) {
            (root.childStack.value.active.instance as? DefaultRootComponent.Child.Home)
                ?.component?.onChatFileReceived(fileName, content)
        } else {
            Log.e("MainActivityTag", "Failed to get content from shared file.")
        }
    }

    private fun readTextFromUri(uri: Uri): String? {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "Unknown File"
    }
}