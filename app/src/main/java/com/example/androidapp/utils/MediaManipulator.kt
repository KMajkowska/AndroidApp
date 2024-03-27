package com.example.androidapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

public val String.fileNameExtension: String
    get() = this.substringAfterLast('.', "")

fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): String? {
    val fileName = UUID.randomUUID().toString() + ".png"

    val file = File(context.filesDir, fileName)
    try {
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
    return file.absolutePath
}

fun retrieveImageFromInternalStorage(imagePath: String): Bitmap? {
    return BitmapFactory.decodeFile(imagePath)
}

fun saveVideoToInternalStorage(context: Context, videoUri: Uri, fileName: String): String {
    val newFileName = UUID.randomUUID().toString() + fileName.fileNameExtension

    val newFile = File(context.filesDir, newFileName)
    val input = context.contentResolver.openInputStream(videoUri)
    val output = FileOutputStream(newFile)

    input?.use { inputStream ->
        output.use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    return newFile.absolutePath
}

fun retrieveVideoFromInternalStorage(videoPath: String): Uri {
    return Uri.parse(videoPath)
}

fun saveMusicToInternalStorage(context: Context, musicUri: Uri, fileName: String): String {
    val newFile = File(context.filesDir, fileName)
    val input = context.contentResolver.openInputStream(musicUri)
    val output = FileOutputStream(newFile)

    input?.use { inputStream ->
        output.use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    return newFile.absolutePath
}

fun retrieveMusicFromInternalStorage(musicPath: String): Uri {
    return Uri.parse(musicPath)
}



