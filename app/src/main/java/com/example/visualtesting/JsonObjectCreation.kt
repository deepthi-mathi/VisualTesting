package com.example.visualtesting

import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Rule
object JsonObjectCreation {

    @get:Rule
    val watcher = TestListener()

    fun createJsonForChatGpt(
        actualimagePath: String,
        expectedImagePath: String
    ): JSONObject {
        val jsonObject = JSONObject()
        val messagesArray = JSONArray()

        val messageObject = JSONObject()
        val contentArray = JSONArray()

        val DEFAULT_PROMPT =
            "Compare elements on two images for visual differences. Text on images, Layout and position of elements, colors of elements, texts and background should be the same. Ignore small noises,battery,system time display on top left, dark/light color differences and blur. First image named +\"Actual\"+, second image \"Expected\". If you find any differences provide answer as numbered list and add to the end of the answer svg image to show differences on top of Expected image. Mark elements that disappear in red, new elements in green and elements that are on both images but changed in blue. In case of no differences answer \"no differences\"."

        // Text part
        val textObject = JSONObject()
        textObject.put("type", "text")
        textObject.put("text", DEFAULT_PROMPT)
        contentArray.put(textObject)

        // First image URL
        val firstImageObject = JSONObject()
        firstImageObject.put("type", "image_url")
        firstImageObject.put(
            "image_url",
            JSONObject().put("url", "data:image/png;base64,$actualimagePath")
        )
        contentArray.put(firstImageObject)

        // Second image URL
        val secondImageObject = JSONObject()
        secondImageObject.put("type", "image_url")
        secondImageObject.put(
            "image_url",
            JSONObject().put("url", "data:image/png;base64,$expectedImagePath")
        )
        contentArray.put(secondImageObject)

        // Add content array to the message
        messageObject.put("role", "user")
        messageObject.put("content", contentArray)

        // Add message to messages array
        messagesArray.put(messageObject)

        // Add messages array to final object
        jsonObject.put("messages", messagesArray)

        // Add other parameters (temperature, top_p, max_tokens)
        jsonObject.put("temperature", 0.7)
        jsonObject.put("top_p", 0.95)
        jsonObject.put("max_tokens", 800)
        jsonObject.put("top_p", 1)
        jsonObject.put("frequency_penalty", 0.0)
        jsonObject.put("presence_penalty", 0.0)

        return jsonObject
    }

    fun createJsonErrorResponse(errorMessage: String): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("status", "Fail")
        jsonObject.addProperty("Test Name", TestMethodHolder.currentTestMethodName)
        jsonObject.addProperty("error", errorMessage)
        return jsonObject
    }
}


