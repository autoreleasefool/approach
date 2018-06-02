package ca.josephroque.bowlingcompanion.games

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single frame in a game.
 */
data class Frame(val number: Int) {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "Frame"

        /** Number of balls in a frame. */
        const val NUMBER_OF_BALLS = 3

        /** Index of the last ball in a frame. */
        const val LAST_BALL = NUMBER_OF_BALLS - 1
    }
}