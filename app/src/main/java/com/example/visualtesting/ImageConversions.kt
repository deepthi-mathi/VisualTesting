package com.example.visualtesting

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File

class ImageConversions {

    fun getBase64FromDrawable(resId: Int, context: Context): String {
        // Load the image as a bitmap
        val bitmap = BitmapFactory.decodeResource(context.resources, resId)

        // Convert the bitmap to a byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // Encode the byte array to Base64
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun getBase64FromFilePath(filePath: String): String {
        // Create a File object from the given file path
        val file = File(filePath)

        // Check if the file exists and is a PNG file
        if (!file.exists() || file.extension != "png") {
            throw IllegalArgumentException("File does not exist or is not a PNG: $filePath")
        }

        // Load the image as a bitmap
        val bitmap: Bitmap = BitmapFactory.decodeFile(file.absolutePath)
            ?: throw IllegalArgumentException("Failed to decode bitmap from file: $filePath")

        // Convert the bitmap to a byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // Encode the byte array to Base64
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }


}

