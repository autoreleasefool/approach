package ca.josephroque.bowlingcompanion.utils

import android.app.Activity
import android.graphics.Bitmap
import android.widget.Toast
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import android.provider.MediaStore
import android.content.ContentValues
import ca.josephroque.bowlingcompanion.R
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import android.content.Intent
import android.net.Uri
import android.util.Log
import ca.josephroque.bowlingcompanion.common.Android

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Utility functions for sharing data from the app.
 */
object ShareUtils {
    @Suppress("unused")
    private const val TAG = "ShareUtils"

    private const val exportFileType = "png"

    fun shareGames(activity: Activity, numberOfGames: Int, bitmapBuilder: () -> Bitmap?) {
        launch(CommonPool) {
            val bitmap = bitmapBuilder() ?: return@launch
            val destination = saveBitmap(activity, numberOfGames, bitmap)
            bitmap.recycle()

            if (destination == null) { return@launch }

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/$exportFileType"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(destination))

            launch(Android) {
                activity.startActivity(Intent.createChooser(shareIntent, activity.resources.getString(R.string.share_image)))

                Analytics.trackShareImage(numberOfGames)
            }
        }
    }

    fun saveGames(activity: Activity, numberOfGames: Int, bitmapBuilder: () -> Bitmap?) {
        if (Permission.WriteExternalStorage.requestPermission(activity)) {
            launch(CommonPool) {
                val bitmap = bitmapBuilder() ?: return@launch
                val destination = saveBitmap(activity, numberOfGames, bitmap)
                bitmap.recycle()

                if (destination == null) { return@launch }

                launch(Android) {
                    Toast.makeText(activity, activity.resources.getString(R.string.image_export_success), Toast.LENGTH_SHORT).show()

                    Analytics.trackSaveImage(numberOfGames)
                }
            }
        }
    }

    private fun saveBitmap(activity: Activity, numberOfGames: Int, bitmap: Bitmap): File? {
        // Get filepath to store the image
        val externalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        var destination = File(externalStorage, "BowlingCompanion")
        destination.mkdirs()
        destination = File(destination, "bc_${System.currentTimeMillis()}.$exportFileType")

        // Output the file and report any errors
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(destination)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } catch (ex: Exception) {
            Log.e(TAG, "Error writing image to file", ex)
            launch(Android) {
                BCError(
                        R.string.error_failed_image_export_title,
                        R.string.error_failed_image_export_message,
                        BCError.Severity.Error
                ).show(activity)
            }

            Analytics.trackSaveImageFailed(numberOfGames)
            return null
        } finally {
            outputStream?.flush()
            outputStream?.close()
        }

        // Update file metadata and add it to the user's gallery
        launch(Android) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.MIME_TYPE, "image/$exportFileType")
                put(MediaStore.MediaColumns.DATA, destination.absolutePath)
            }

            activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }

        return destination
    }
}
