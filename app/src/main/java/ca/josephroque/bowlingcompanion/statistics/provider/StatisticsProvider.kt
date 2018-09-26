package ca.josephroque.bowlingcompanion.statistics.provider

import android.os.Bundle
import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.statistics.unit.BowlerUnit
import ca.josephroque.bowlingcompanion.statistics.unit.GameUnit
import ca.josephroque.bowlingcompanion.statistics.unit.LeagueUnit
import ca.josephroque.bowlingcompanion.statistics.unit.SeriesUnit
import ca.josephroque.bowlingcompanion.statistics.unit.StatisticsUnit
import ca.josephroque.bowlingcompanion.statistics.unit.TeamUnit
import ca.josephroque.bowlingcompanion.teams.Team

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Provide statistics from a bowler to display.
 */
sealed class StatisticsProvider : IIdentifiable, KParcelable {

    abstract val name: String
    abstract val typeName: Int

    // MARK: TeamStatistics

    data class TeamStatistics(val team: Team) : StatisticsProvider() {
        companion object {
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(StatisticsProvider::TeamStatistics)
        }

        private constructor(p: Parcel): this(p.readParcelable<Team>(Team::class.java.classLoader)!!)

        override val id = team.id.and(0xF00000000000000L)
        override val name = team.name
        override val typeName = R.string.team
    }

    // MARK: BowlerStatistics

    data class BowlerStatistics(val bowler: Bowler) : StatisticsProvider() {
        companion object {
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(StatisticsProvider::BowlerStatistics)
        }

        private constructor(p: Parcel): this(p.readParcelable<Bowler>(Bowler::class.java.classLoader)!!)

        override val id = bowler.id.and(0xE00000000000000L)
        override val name = bowler.name
        override val typeName = R.string.bowler
    }

    // MARK: LeagueStatistics

    data class LeagueStatistics(val league: League) : StatisticsProvider() {
        companion object {
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(StatisticsProvider::LeagueStatistics)
        }

        private constructor(p: Parcel): this(p.readParcelable<League>(League::class.java.classLoader)!!)

        override val id = league.id.and(0xD00000000000000L)
        override val name = league.name
        override val typeName = R.string.league
    }

    // MARK: SeriesStatistics

    data class SeriesStatistics(val series: Series) : StatisticsProvider() {
        companion object {
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(StatisticsProvider::SeriesStatistics)
        }

        private constructor(p: Parcel): this(p.readParcelable<Series>(Series::class.java.classLoader)!!)

        override val id = series.id.and(0xC00000000000000L)
        override val name = series.prettyDate
        override val typeName = R.string.series
    }

    // MARK: GameStatistics

    data class GameStatistics(val game: Game) : StatisticsProvider() {
        companion object {
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(StatisticsProvider::GameStatistics)
        }

        private constructor(p: Parcel): this(p.readParcelable<Game>(Game::class.java.classLoader)!!)

        override val id = game.id.and(0xB00000000000000L)
        override val name = "Game ${game.ordinal}"
        override val typeName = R.string.game
    }

    val units: List<StatisticsUnit> by lazy {
        return@lazy when (this) {
            is TeamStatistics -> {
                val units: MutableList<StatisticsUnit> = ArrayList(team.members.size + 1)
                units.add(TeamUnit(team.id, team.name))
                units.addAll(team.members.map { BowlerUnit(it.bowlerId, it.bowlerName) })
                units
            }
            is BowlerStatistics -> listOf(BowlerUnit(bowler.id, bowler.name))
            is LeagueStatistics -> listOf(LeagueUnit(league.bowler.name, league.id, league.name))
            is SeriesStatistics -> listOf(SeriesUnit(series.league.bowler.name, series.league.name, series.id, series.date))
            is GameStatistics -> listOf(GameUnit(game.series.league.bowler.name, game.series.league.name, game.series.date, game.series.id, game.id, game.ordinal))
        }
    }

    // MARK: Parcelable

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        when (this@StatisticsProvider) {
            is TeamStatistics -> writeParcelable(team, 0)
            is BowlerStatistics -> writeParcelable(bowler, 0)
            is LeagueStatistics -> writeParcelable(league, 0)
            is SeriesStatistics -> writeParcelable(series, 0)
            is GameStatistics -> writeParcelable(game, 0)
        }
    }

    override fun describeContents(): Int {
        return when (this) {
            // When changing, update `StatisticsProvider.getParcelable`
            is TeamStatistics -> 0
            is BowlerStatistics -> 1
            is LeagueStatistics -> 2
            is SeriesStatistics -> 3
            is GameStatistics -> 4
        }
    }

    companion object {
        fun getParcelable(arguments: Bundle?, key: String, type: Int): StatisticsProvider? {
            return when (type) {
                // When changing, update `StatisticsProvider::describeContents`
                0 -> arguments?.getParcelable<TeamStatistics>(key)
                1 -> arguments?.getParcelable<BowlerStatistics>(key)
                2 -> arguments?.getParcelable<LeagueStatistics>(key)
                3 -> arguments?.getParcelable<SeriesStatistics>(key)
                4 -> arguments?.getParcelable<GameStatistics>(key)
                else -> throw IllegalArgumentException("StatisticsProvider type $type does not exist")
            }
        }
    }
}
