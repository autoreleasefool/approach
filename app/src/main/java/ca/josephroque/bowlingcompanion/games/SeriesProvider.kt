package ca.josephroque.bowlingcompanion.games

import android.os.Bundle
import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.teams.Team

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Manage the series being edited.
 */
sealed class SeriesProvider : KParcelable {

    // MARK: TeamSeries

    data class TeamSeries(val team: Team) : SeriesProvider() {
        companion object {
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(::TeamSeries)
        }

        private constructor(p: Parcel): this(p.readParcelable<Team>(Team::class.java.classLoader)!!)
    }

    // MARK: BowlerSeries

    data class BowlerSeries(val series: Series) : SeriesProvider() {
        companion object {
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(::BowlerSeries)
        }

        private constructor(p: Parcel): this(p.readParcelable<Series>(Series::class.java.classLoader)!!)
    }

    val seriesList: List<Series>
        get() {
            return when (this) {
                is TeamSeries -> this.team.series
                is BowlerSeries -> listOf(this.series)
            }
        }

    // MARK: Parcelable

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        when (this@SeriesProvider) {
            is TeamSeries -> writeParcelable(team, 0)
            is BowlerSeries -> writeParcelable(series, 0)
        }
    }

    override fun describeContents(): Int {
        // When changing, update `SeriesProvider.getParcelable`
        return when (this) {
            is TeamSeries -> 0
            is BowlerSeries -> 1
        }
    }

    companion object {
        fun getParcelable(arguments: Bundle?, key: String, type: Int): SeriesProvider? {
            return when (type) {
                // When changing, update `SeriesProvider::describeContents`
                0 -> arguments?.getParcelable<TeamSeries>(key)
                1 -> arguments?.getParcelable<BowlerSeries>(key)
                else -> throw IllegalArgumentException("SeriesProvider type $type does not exist")
            }
        }
    }
}
