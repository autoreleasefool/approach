package ca.josephroque.bowlingcompanion.games

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single game recording.
 */
data class Game(val score: Int) {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "Game"

        /** Number of frames in a single game. */
        const val NUMBER_OF_FRAMES = 10

        /** Number of pins used in the game. */
        const val NUMBER_OF_PINS = 5

        /** Maximum possible score. */
        const val MAX_SCORE = 450
    }
}
