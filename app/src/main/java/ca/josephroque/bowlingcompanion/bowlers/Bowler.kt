package ca.josephroque.bowlingcompanion.bowlers

import ca.josephroque.bowlingcompanion.common.INameAverage

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single Bowler, who has leagues, events, series, games, and stats.
 */

class Bowler(private val bowlerName: String,
             private val bowlerAverage: Double,
             private val bowlerId: Long): INameAverage {

    override val name: String
        get() = bowlerName

    override val average: Double
        get() = bowlerAverage

    override val id: Long
        get() = bowlerId
}