package com.example.androidapp.media

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


val String.fileNameExtension: String
    get() = this.substringAfterLast('.', "")

val Uri.fileNameExtension: String
    get() = MimeTypeMap.getFileExtensionFromUrl(this.toString())

fun getPrivateStorageUri(context: Context, filePath: String): Uri? {
    val file = File(context.filesDir, filePath)
    if (!file.exists())
        return null;

    return Uri.fromFile(file)
}

fun saveMediaToInternalStorage(context: Context, uri: Uri): String? {
    try {
        val filename = generateFilenameFromUri(uri)

        context.contentResolver.openInputStream(uri)?.use { input ->
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { output ->
                input.copyTo(output)
            }

            return filename
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

fun generateFilenameFromUri(uri: Uri): String {
    return "${generateUUID()}.${uri.fileNameExtension}"
}

fun generateUUID(): String {
    return UUID.randomUUID().toString()
}