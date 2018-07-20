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
        @Suppress("unused")
        private const val TAG = "Files"

        /**
         * Retrieve a text file asset as a string.
         *
         * @param context to access local files
         * @param fileName name of the asset
         * @return the text in the asset, or null if the file was not found.
         */
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
