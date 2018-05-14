package ca.josephroque.bowlingcompanion.games

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Possible match play results of a game.
 */
enum class MatchPlayResult {
    NONE, WON, LOST, TIED;

    companion object {
        private val map = MatchPlayResult.values().associateBy(MatchPlayResult::ordinal)
        fun fromInt(type: Int) = map[type]
    }
}
