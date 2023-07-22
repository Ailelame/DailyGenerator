package com.stormbirdmedia.dailygenerator.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.OutputStream


object BitmapUtils {


    val fileName = "daily_generator_image.png"

    fun saveBitmapAndPrepareUri(context: Context, bitmap: Bitmap): Uri? {

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        var imageUri: Uri? = null
        try {
            context.contentResolver.insert(collection, contentValues)?.let { uri ->
                context.contentResolver.openOutputStream(uri)?.use { outputStream: OutputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    outputStream.flush()
                    imageUri = uri
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return imageUri

    }




    fun deleteScreenshot(context: Context) {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(dir, fileName)
        if (file.exists()) {
            file.delete()
        }
    }
}