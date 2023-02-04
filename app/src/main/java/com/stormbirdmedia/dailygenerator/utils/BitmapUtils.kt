package com.stormbirdmedia.dailygenerator.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File

object BitmapUtils {


    val file_name = "daily_generator_image.png"

    fun saveBitmapAndPrepareUri(context : Context, bitmap : Bitmap): String? {
        val file = createFileInExternalStorage(context,
            file_name)

        Timber.d("file path %s", file!!.absolutePath)
        if (!file.exists()) {
            file.createNewFile()
        }
        try {
            file.writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 85)
            return provideContentUri(context, file)
        } catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    private fun createFileInExternalStorage(context: Context, fileName: String): File? {
        val timeStamp: String = System.currentTimeMillis().toString()+ ".png"
        val name = if (fileName.isBlank()) timeStamp else fileName
        return File(getAppExternalFilesDir(context), name)
    }

    private fun getAppExternalFilesDir(context: Context): File? {
        val file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (file != null && !file.exists()) {
            file.mkdirs()
        }
        return file
    }

    private fun provideContentUri(context: Context, file: File): String {
        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "com.stormbirdmedia.dailygenerator", file
        )
        return contentUri.toString()
    }

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }

    fun deleteScreenshot(context: Context){
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(dir, file_name)
         if (file.exists()) {
            file.delete()
        }
    }
}