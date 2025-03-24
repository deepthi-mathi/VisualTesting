package com.example.visualtesting.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.visualtesting.Constants.API_KEY
import com.example.visualtesting.Constants.BASE_URL
import com.example.visualtesting.Constants.DEPLOYMENT_ID
import com.example.visualtesting.Constants.DEPLOYMENT_VERSION
import com.example.visualtesting.FileUtils.getOrCreateFileName
import com.example.visualtesting.ImageConversions
import com.example.visualtesting.JsonObjectCreation.createJsonForChatGpt
import java.io.File

class TestUtilities {

    val imageConversions = ImageConversions()
    private val apiKey = API_KEY // AZURE_API_KEY
    private val endpoint = BASE_URL
    private val deploymentId = DEPLOYMENT_ID
    private val version = DEPLOYMENT_VERSION

    fun takeTheScreenshot(context: Context, fileName: String): String {
        // Get the UiDevice instance for UI testing
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Create a timestamp for unique file names
        /*val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val screenshotFileName = "${fileName}_$timestamp.png"*/
        val screenshotFileName = getOrCreateFileName(fileName, "png")

        // Define the folder where the screenshot will be saved
        val screenshotDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val resultsDir = File(screenshotDir, "AIActualScreenShots")
        // Check if the directory is valid
        if (resultsDir == null) {
            return handleScreenshotError("Failed to get directory")
        }

        // Create directory if it doesn't exist
        if (!resultsDir.exists() && !resultsDir.mkdirs()) {
            return handleScreenshotError("Failed to create directory: ${screenshotDir.absolutePath}")
        }

        // File object where the screenshot will be saved
        val screenshotFile = File(resultsDir, screenshotFileName)

        // Try to take the screenshot and handle success or failure
        return try {
            val success = device.takeScreenshot(screenshotFile)
            if (!success) {
                handleScreenshotError("Screenshot capture failed.")
            } else {
                println("Screenshot saved to: ${screenshotFile.absolutePath}")
                screenshotFile.absolutePath // Return the full file path if successful
            }
        } catch (e: Exception) {
            handleScreenshotError("Error taking screenshot: ${e.message}")
        }
    }

    private fun handleScreenshotError(message: String): String {
        throw Exception(message)
    }

    fun doesFileExist(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists() && file.isFile
    }

    fun compareImagesWithAI(
        actualimagePath: String,
        exceptedResId: Int,
        context: Context,
        callback: (String?) -> Unit
    ) {
        // creating a queue for request queue.
        val queue: RequestQueue = Volley.newRequestQueue(context)

        val image64ActualBase64 = imageConversions.getBase64FromFilePath(actualimagePath)
        val image64ExpectedBase64 = imageConversions.getBase64FromDrawable(exceptedResId, context)

        // creating a json object on below line.
        val jsonObject =
            createJsonForChatGpt(image64ActualBase64, image64ExpectedBase64)
        println("jsonObject========$jsonObject\n")

        // Construct the full API URL
        val fullUrl =
            "$endpoint/openai/deployments/$deploymentId/chat/completions?api-version=$version"

        // on below line making json object request.
        val postRequest = object : JsonObjectRequest(
            Method.POST,
            fullUrl,
            jsonObject,
            Response.Listener { response ->
                // on below line getting response message.
                val responseMsg: String? = response.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                // Return the response message via the callback
                callback(responseMsg)
            },
            Response.ErrorListener { error ->
                Log.e("TAGAPI", "Error: ${error.message}\n$error")
                callback(null) // Return null in case of error
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                // adding headers
                params["Content-Type"] = "application/json"
                params["api-key"] = apiKey
                return params
            }
        }

        // Adding retry policy for the request.
        postRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            override fun retry(error: VolleyError) {
                // Handle retry logic here
            }
        })

        // Adding the request to the queue.
        queue.add(postRequest)
    }
}
