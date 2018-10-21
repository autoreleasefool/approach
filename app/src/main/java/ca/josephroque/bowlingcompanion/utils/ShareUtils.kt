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
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import android.view.View
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.games.views.ScoreSheet

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Utility functions for sharing data from the app.
 */
object ShareUtils {
    @Suppress("unused")
    private const val TAG = "ShareUtils"

    private const val exportFileType = "png"

    private const val interGameBorderWidth = 4

    fun shareGames(activity: Activity, games: List<Game>) {
        launch(CommonPool) {
            val bitmap = buildBitmap(activity, games)
            val destination = saveBitmap(activity, games.size, bitmap)
            bitmap.recycle()

            if (destination == null) { return@launch }

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/$exportFileType"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(destination))

            launch(Android) {
                activity.startActivity(Intent.createChooser(shareIntent, activity.resources.getString(R.string.share_image)))

                Analytics.trackShareImage(games.size)
            }
        }
    }

    fun saveGames(activity: Activity, games: List<Game>) {
        if (Permission.WriteExternalStorage.requestPermission(activity)) {
            launch(CommonPool) {
                val bitmap = buildBitmap(activity, games)
                val destination = saveBitmap(activity, games.size, bitmap)
                bitmap.recycle()

                if (destination == null) { return@launch }

                launch(Android) {
                    Toast.makeText(activity, activity.resources.getString(R.string.image_export_success), Toast.LENGTH_SHORT).show()

                    Analytics.trackSaveImage(games.size)
                }
            }
        }
    }

    private fun buildBitmap(activity: Activity, games: List<Game>): Bitmap {
        val scoreSheet = ScoreSheet(activity)
        scoreSheet.frameNumbersEnabled = false
        scoreSheet.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val scoreSheetHeight = scoreSheet.measuredHeight

        val bitmapWidth = scoreSheet.measuredWidth
        val bitmapHeight = (scoreSheetHeight * games.size) + (interGameBorderWidth * games.size - 1)
        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paintBlackLine = Paint()
        paintBlackLine.color = android.graphics.Color.BLACK

        games.forEachIndexed { index, game ->
            scoreSheet.apply(-1, -1, game)
            val scoreSheetBitmap = scoreSheet.toBitmap()
            canvas.drawBitmap(scoreSheetBitmap, 0F, (index * (scoreSheetHeight + interGameBorderWidth)).toFloat(), null)
            for (i in 1..interGameBorderWidth) {
                val y = (index * (scoreSheetHeight + interGameBorderWidth) - i).toFloat()
                canvas.drawLine(0F, y, bitmapWidth.toFloat(), y, paintBlackLine)
            }
            scoreSheetBitmap.recycle()
        }

        return bitmap
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
