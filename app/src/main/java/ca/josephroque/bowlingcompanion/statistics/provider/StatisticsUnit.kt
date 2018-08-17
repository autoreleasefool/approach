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
    private val levelOfDetail: LevelOfDetail,
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

        /** Level of statistic specificity which the unit will show. */
        enum class LevelOfDetail {
            Team,
            Bowler,
            League,
            Series,
            Game;

            /** Categories to exclude when display this unit's statistics. */
            val excludedCategories: Set<StatisticsCategory>
                get() {
                    return when (this) {
                        Team, Bowler, League -> emptySet()
                        Series -> setOf(StatisticsCategory.Average)
                        Game -> setOf(StatisticsCategory.Average, StatisticsCategory.MatchPlay, StatisticsCategory.Overall)
                    }
                }

            /** Statistics to exclude when display this unit's statistics. */
            val exludedStatistics: Set<Statistic.Companion.Identifier>
                get() {
                    return when (this) {
                        Team -> setOf(Statistic.Companion.Identifier.Bowler, Statistic.Companion.Identifier.League, Statistic.Companion.Identifier.Series, Statistic.Companion.Identifier.Game)
                        Bowler -> setOf(Statistic.Companion.Identifier.League, Statistic.Companion.Identifier.Series, Statistic.Companion.Identifier.Game)
                        League -> setOf(Statistic.Companion.Identifier.Series, Statistic.Companion.Identifier.Game)
                        Series -> setOf(Statistic.Companion.Identifier.Game)
                        Game -> setOf(Statistic.Companion.Identifier.AveragePinsLeft)
                    }
                }

            companion object {
                private val map = LevelOfDetail.values().associateBy(LevelOfDetail::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }

    /**
     * Construct a [StatisticsUnit] from a [Parcel].
     */
    constructor(p: Parcel): this(
            levelOfDetail = LevelOfDetail.fromInt(p.readInt())!!,
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
        writeInt(levelOfDetail.ordinal)
        writeString(name)
        writeParcelableArray(statistics.toTypedArray(), 0)
    }
}
