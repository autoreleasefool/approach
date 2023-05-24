package ca.josephroque.bowlingcompanion.scoring

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Provides utility methods for calculating averages.
 */
object Average {

    @Suppress("unused")
    private const val TAG = "Average"

    fun getAdjustedAverage(trackedPinfall: Int, trackedGames: Int, additionalPinfall: Int, additionalGames: Int): Double {
        val totalPinfall = trackedPinfall + additionalPinfall
        val totalGames = trackedGames + additionalGames

        return if (totalGames > 0) totalPinfall.div(totalGames.toDouble()) else 0.0
    }
}
