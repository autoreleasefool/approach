package ca.josephroque.bowlingcompanion.games

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
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

    /** Manage a team's series. */
    data class TeamSeries(val team: Team) : SeriesProvider() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(::TeamSeries)
        }

        /**
         * Construct [TeamSeries] from a [Parcel].
         */
        constructor(p: Parcel): this(p.readParcelable<Team>(Team::class.java.classLoader))
    }

    /** Manage a bowler's series. */
    data class BowlerSeries(val series: Series) : SeriesProvider() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(::BowlerSeries)
        }

        /**
         * Construct [BowlerSeries] from a [Parcel].
         */
        constructor(parcel: Parcel): this(parcel.readParcelable<Series>(Series::class.java.classLoader))
    }

    /** List of series. */
    val seriesList: List<Series>
        get() {
            return when (this) {
                is TeamSeries -> this.team.series
                is BowlerSeries -> listOf(this.series)
            }
        }

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        when (this@SeriesProvider) {
            is TeamSeries -> writeParcelable(team, 0)
            is BowlerSeries -> writeParcelable(series, 0)
        }
    }

    /** @Override */
    override fun describeContents(): Int {
        // When changing, update `SeriesProvider.getParcelable`
        return when (this) {
            is TeamSeries -> 0
            is BowlerSeries -> 1
        }
    }

    companion object {
        /**
         * Get the [SeriesProvider] from the [Bundle] depending on the given [type].
         *
         * @param arguments bundle to get provider from
         * @param key the key identifying the [Parcel] in the [Bundle]
         * @param type the type of provider to get
         * @return the [SeriesProvider]
         */
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
