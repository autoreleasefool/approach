package ca.josephroque.bowlingcompanion.games

/**
 * Copyright (C) 2018 Joseph Roque
 */
data class Game(val score: Int) {

    companion object {
        /** Logging identifier. */
        private const val TAG = "Game"

        /** Number of frames in a single game. */
        const val NUMBER_OF_FRAMES = 10
    }
}