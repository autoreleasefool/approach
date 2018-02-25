package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import android.util.Log
import java.io.BufferedReader


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Local file manipulation.
 */
class Files {

    companion object {
        /** Logging identifier. */
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
    }
}