package ca.josephroque.bowlingcompanion.teams

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.INameAverage
import ca.josephroque.bowlingcompanion.common.KParcelable
import ca.josephroque.bowlingcompanion.common.parcelableCreator

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single Team, which has a set of bowlers.
 */
data class Team(
        private val teamName: String,
        private val teamAverage: Double,
        private val teamId: Long
): INameAverage, KParcelable {

    override val name: String
        get() = teamName

    override val average: Double
        get() = teamAverage

    override val id: Long
        get() = teamId

    override var isDeleted: Boolean = false

    /**
     * Construct [Team] from a [Parcel]
     */
    private constructor(p: Parcel): this(
            teamName = p.readString(),
            teamAverage = p.readDouble(),
            teamId = p.readLong()
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeDouble(average)
        writeLong(id)
    }

    companion object {
        /** Logging identifier. */
        private const val TAG = "Team"

        /** Creator, required by [Parcelable]. */
        @JvmField val CREATOR = parcelableCreator(::Team)
    }
}