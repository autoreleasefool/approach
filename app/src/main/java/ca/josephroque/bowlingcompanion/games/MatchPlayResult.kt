package ca.josephroque.bowlingcompanion.games

import ca.josephroque.bowlingcompanion.R

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

    /**
     * Get an icon which represents the match play result.
     *
     * @return id of the icon drawable
     */
    fun getIcon(): Int {
        return when (this) {
            NONE -> R.drawable.ic_match_play_none
            WON -> R.drawable.ic_match_play_won
            LOST -> R.drawable.ic_match_play_lost
            TIED -> R.drawable.ic_match_play_tied
        }
    }
}
