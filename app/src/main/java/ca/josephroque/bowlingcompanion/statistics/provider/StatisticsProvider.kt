package ca.josephroque.bowlingcompanion.statistics.provider

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.teams.Team

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Provide statistics from a bowler to display.
 */
sealed class StatisticsProvider : KParcelable {

    /** Provide a team's statistics. */
    data class TeamStatistics(val team: Team) : StatisticsProvider() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(StatisticsProvider::TeamStatistics)
        }

        /**
         * Construct [TeamStatistics] from a [Parcel].
         */
        constructor(p: Parcel): this(p.readParcelable<Team>(Team::class.java.classLoader))
    }

    /** Provide a bowler's statistics. */
    data class BowlerStatistics(val bowler: Bowler) : StatisticsProvider() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(StatisticsProvider::BowlerStatistics)
        }

        /**
         * Construct [BowlerStatistics] from a [Parcel].
         */
        constructor(p: Parcel): this(p.readParcelable<Bowler>(Bowler::class.java.classLoader))
    }

    /** Provide a league's statistics. */
    data class LeagueStatistics(val league: League) : StatisticsProvider() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(StatisticsProvider::LeagueStatistics)
        }

        /**
         * Construct [LeagueStatistics] from a [Parcel].
         */
        constructor(p: Parcel): this(p.readParcelable<League>(League::class.java.classLoader))
    }

    /** Provide a series's statistics. */
    data class SeriesStatistics(val series: Series) : StatisticsProvider() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(StatisticsProvider::SeriesStatistics)
        }

        /**
         * Construct [SeriesStatistics] from a [Parcel].
         */
        constructor(p: Parcel): this(p.readParcelable<Series>(Series::class.java.classLoader))
    }

    /** Provide a game's statistics. */
    data class GameStatistics(val game: Game) : StatisticsProvider() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(StatisticsProvider::GameStatistics)
        }

        /**
         * Construct [GameStatistics] from a [Parcel].
         */
        constructor(p: Parcel): this(p.readParcelable<Game>(Game::class.java.classLoader))
    }

    /** The statistics to be displayed. */
    val units: List<StatisticsUnit> by lazy {
        return@lazy when (this) {
            is TeamStatistics -> StatisticsUnit.buildFromTeam(team)
            is BowlerStatistics -> StatisticsUnit.buildFromBowler(bowler)
            is LeagueStatistics -> StatisticsUnit.buildFromLeague(league)
            is SeriesStatistics -> StatisticsUnit.buildFromSeries(series)
            is GameStatistics -> StatisticsUnit.buildFromGame(game)
        }
    }

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        when (this@StatisticsProvider) {
            is TeamStatistics -> writeParcelable(team, 0)
            is BowlerStatistics -> writeParcelable(bowler, 0)
            is LeagueStatistics -> writeParcelable(league, 0)
            is SeriesStatistics -> writeParcelable(series, 0)
            is GameStatistics -> writeParcelable(game, 0)
        }
    }

    /** @Override */
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
        /**
         * Get the [StatisticsProvider] from the [Bundle] depending on the given [type].
         *
         * @param arguments bundle to get provider from
         * @param key the key identifying the [Parcel] in the [Bundle]
         * @param type the type of provider to get
         * @return the [StatisticsProvider]
         */
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
