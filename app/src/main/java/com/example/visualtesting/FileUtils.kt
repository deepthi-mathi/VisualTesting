package com.example.visualtesting

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {

    private var errorFileName: String? = null

    fun getOrCreateFileName(fileName: String,ext: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "${fileName}_$timestamp.$ext"
    }

    fun appendJsonToFile(jsonObject: JsonObject, fileName: String, context: Context) {
        // Set the results directory to the current project path
        val externalResultsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val resultsDir = File(externalResultsDir, "AIResults")

        // Create the directory if it doesn't exist
        if (!resultsDir.exists()) {
            resultsDir.mkdirs()
        }

        // Create the file where the JSON will be appended
        // Generate the error file name only once
        if (errorFileName == null) {
            errorFileName = getOrCreateFileName("error_report","json") // Call your method to generate the file name
        }
        val file = File(resultsDir, errorFileName)

        try {
            // Create a new JsonArray or read the existing array from the file
            val jsonArray = if (file.exists()) {
                // Read existing JSON array from the file
                val existingContent = file.readText()
                Gson().fromJson(existingContent, JsonArray::class.java)
            } else {
                // Create a new JsonArray if file does not exist
                JsonArray()
            }

            // Append the new error JSON object to the array
            jsonArray.add(jsonObject)

            // Write the updated array back to the file
            FileWriter(file).use { writer ->
                writer.write(jsonArray.toString())
            }
            println("Error report appended to: $resultsDir")
        } catch (e: IOException) {
            Log.e("Error", e.message!!)
            println("Failed to append error report: ${e.message}")
        }
    }
}
