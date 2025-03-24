package com.example.visualtesting.utils

import android.content.Context
import android.util.Log
import com.example.visualtesting.FileUtils
import com.example.visualtesting.JsonObjectCreation.createJsonErrorResponse
import com.example.visualtesting.TestListener
import junit.framework.TestCase.assertNotNull
import org.junit.Assert
import org.junit.Rule
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object VisualAssert {

    val uiTestsUtils = TestUtilities()

    @get:Rule
    val watcher = TestListener()

    /**
     * Separate assertion function for visual validation based on response message.
     * This function asserts whether the comparison was successful or not.
     * Throws an AssertionError if the validation fails.
     */

    fun assertCompareImages(
        actualScreenshotFilePath: String,
        exceptedResId: Int,
        context: Context
    ) {
        val latch = CountDownLatch(1)
        var testPassed = true // Track if the test passed
        var failureMessage: String? = null // Store the failure message

        if (uiTestsUtils.doesFileExist(actualScreenshotFilePath)) {
            uiTestsUtils.compareImagesWithAI(
                actualScreenshotFilePath,
                exceptedResId,
                context
            ) { responseMsg ->
                try {
                    assertVisualValidation(responseMsg, context)
                } catch (e: AssertionError) {
                    Log.e("TestError", "Assertion failed: ${e.message}")
                    failureMessage =
                        "Images do not match based on AI response, Please check the response in the report on path /storage/emulated/0/Documents/AIResults"
                    testPassed = false
                } catch (e: Exception) {
                    // Log unexpected exceptions
                    failureMessage = "Unexpected error: ${e.message}"
                    testPassed = false
                } finally {
                    latch.countDown() // Ensure latch is decremented in all cases
                }
            }
        } else {
            failureMessage = "The file at $actualScreenshotFilePath does not exist."
            testPassed = false
            latch.countDown()
        }
        // Add a timeout for waiting for the latch
        if (!latch.await(120, TimeUnit.SECONDS)) {
            failureMessage = "Timeout occurred while waiting for AI response."
            testPassed = false
        }

        if (!testPassed) {
            Assert.fail(failureMessage) // Fail the test and output the failure message
        }
    }

    /*fun assertVisualValidation(responseMsg: String?) {
        // Check if response message is null or empty and assert failure
        Assert.assertNotNull(responseMsg, "Response message is null or empty")
        println("Result===" + responseMsg)

        // Check if the response contains any of the matchers from the constants class
        val containsMatch = TestDataConstants.IMAGE_COMPARISON_MATCHERS.any { matcher ->
            responseMsg!!.contains(matcher, ignoreCase = true)
        }
        Assert.assertTrue(containsMatch, "Images do not match based on AI response")
    }*/

    fun assertVisualValidation(responseMsg: String?, context: Context) {
        // Check if response message is null or empty and assert failure
        assertNotNull("Response message is null or empty", responseMsg)

        // Check if the response contains any of the matchers
        val containsMatch = TestDataConstants.IMAGE_COMPARISON_MATCHERS.any { matcher ->
            responseMsg!!.contains(matcher, ignoreCase = true)
        }

        if (!containsMatch) {
            // Create a JSON object with the error message
            val errorJson = createJsonErrorResponse(responseMsg!!)

            // Write the JSON object to a file in the results folder
            FileUtils.appendJsonToFile(errorJson, "error_report", context)

            // Assert the failure
            Assert.fail("Images do not match based on AI response, Please check the response in ")
            // Assert.assertTrue(false, "Images do not match based on AI response")
        } else {
            Log.e("Visual Testing - Success", "Images match successfully based on AI response.")
        }
    }
}

