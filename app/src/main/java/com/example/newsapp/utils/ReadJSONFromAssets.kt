package com.example.newsapp.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Reads JSON data from a file located in the app's assets folder.
 *
 * @param context The context of the application.
 * @param path The path to the JSON file within the assets folder.
 * @return A string containing the JSON data, or an empty string if an error occurs.
 */
fun readJSONFromAssets(context: Context, path: String): String {
    return try {
        // Open the JSON file from the assets folder
        val file = context.assets.open(path)

        // Create a buffered reader to efficiently read the file
        val bufferedReader = BufferedReader(InputStreamReader(file))

        // Read the JSON content from the buffered reader
        val jsonString = bufferedReader.use { it.readText() }

        // Return the JSON string
        jsonString
    } catch (e: Exception) {
        // Print the stack trace if an exception occurs (for debugging purposes)
        e.printStackTrace()

        // Return an empty string in case of an error
        ""
    }
}
