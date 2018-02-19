package ca.josephroque.bowlingcompanion.scoring

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Provides utility methods for calculating averages.
 */
class Average {

    companion object {

        /**
         * Calculates the average of a league based on the games played so far, their scores, and any additional games
         * and pinfall specified by the user.
         *
         * @param trackedPinfall total pinfall of the games recorded in the league
         * @param trackedGames number of games recorded in the league
         * @param additionalPinfall additional, untracked pinfall for the league
         * @param additionalGames additional, untracked games for the league
         * @return the average of the league
         */
        fun getAdjustedAverage(trackedPinfall: Int, trackedGames: Int, additionalPinfall: Int, additionalGames: Int): Double {
            val totalPinfall = trackedPinfall + additionalPinfall
            val totalGames = trackedGames + additionalGames

            if (totalGames > 0) {
                return totalPinfall.div(totalGames.toDouble())
            } else {
                return 0.0
            }
        }
    }

}