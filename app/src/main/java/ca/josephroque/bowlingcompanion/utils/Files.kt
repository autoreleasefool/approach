package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Local file manipulation.
 */
object Files {

    @Suppress("unused")
    private const val TAG = "Files"

    fun retrieveTextFileAsset(context: Context, fileName: String): String? {
        try {
            val inputStream = context.assets.open(fileName)
            return inputStream.bufferedReader().use(BufferedReader::readText)
        } catch (ex: Exception) {
            Log.e(TAG, String.format("Could not read text file: %s", fileName), ex)
        }

        return null
    }

    fun copyFile(source: File, dest: File): Deferred<Boolean> {
        val bufferSize = 1024
        return async(CommonPool) {
            if (dest.exists()) {
                dest.delete()
            }

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                inputStream = FileInputStream(source)
                outputStream = FileOutputStream(dest)

                val buffer = ByteArray(bufferSize)
                var dataLength = inputStream.read(buffer)
                while (dataLength > 0) {
                    outputStream.write(buffer, 0, dataLength)
                    dataLength = inputStream.read(buffer)
                }

                return@async true
            } catch (ex: IOException) {
                Log.e(TAG, "Failed to backup file.", ex)
            } finally {
                try {
                    inputStream?.close()
                    outputStream?.close()
                } catch (ex: IOException) {
                    Log.e(TAG, "Failed to close backup streams.", ex)
                }
            }

            return@async false
        }
    }
}
