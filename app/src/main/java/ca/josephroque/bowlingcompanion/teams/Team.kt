package ca.josephroque.bowlingcompanion.teams

import ca.josephroque.bowlingcompanion.common.INameAverage

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single Team, which has a set of bowlers.
 */

data class Team(private val teamName: String,
                private val teamAverage: Double,
                private val teamId: Long): INameAverage {

    override val name: String
        get() = teamName

    override val average: Double
        get() = teamAverage

    override val id: Long
        get() = teamId
}