package ca.josephroque.bowlingcompanion.statistics.provider

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
 * A single unit which provides a list of statistics to be displayed.
 */
class StatisticsUnit(
    val name: String,
    val statistics: List<Statistic>
) : KParcelable {

    companion object {
        /** Logging identifier */
        @Suppress("unused")
        private const val TAG = "StatisticsUnit"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::StatisticsUnit)

        fun buildFromTeam(team: Team): List<StatisticsUnit> {
            return ArrayList()
        }

        fun buildFromBowler(bowler: Bowler): List<StatisticsUnit> {
            return ArrayList()
        }

        fun buildFromLeague(league: League): List<StatisticsUnit> {
            return ArrayList()
        }

        fun buildFromSeries(series: Series): List<StatisticsUnit> {
            return ArrayList()
        }

        fun buildFromGame(game: Game): List<StatisticsUnit> {
            return ArrayList()
        }
    }

    /**
     * Construct a [StatisticsUnit] from a [Parcel].
     */
    constructor(p: Parcel): this(
            name = p.readString(),
            statistics = arrayListOf<Statistic>().apply {
                val parcelableArray = p.readParcelableArray(Statistic::class.java.classLoader)
                this.addAll(parcelableArray.map {
                    return@map it as Statistic
                })
            }
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelableArray(statistics.toTypedArray(), 0)
    }
}
