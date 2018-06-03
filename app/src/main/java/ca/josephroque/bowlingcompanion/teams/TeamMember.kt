package ca.josephroque.bowlingcompanion.teams

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Member of a team.
 */
data class TeamMember(
        override val id: Long,
        val bowlerName: String,
        val bowlerId: Long,
        val leagueName: String? = null,
        val leagueId: Long = -1,
        val seriesId: Long = -1
) : IIdentifiable, KParcelable {

    /**
     * Construct [TeamMember] from a [Parcel]
     */
    private constructor(p: Parcel): this(
            id = p.readLong(),
            bowlerName = p.readString(),
            bowlerId = p.readLong(),
            leagueName = p.readString(),
            leagueId = p.readLong(),
            seriesId = p.readLong()
    )

    /**
     * Construct [TeamMember] from a [TeamMember].
     */
    constructor(teamMember: TeamMember): this(
            id = teamMember.id,
            bowlerName = teamMember.bowlerName,
            bowlerId = teamMember.bowlerId,
            leagueName = teamMember.leagueName,
            leagueId = teamMember.leagueId,
            seriesId = teamMember.seriesId
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(bowlerName)
        writeLong(bowlerId)
        writeString(leagueName)
        writeLong(leagueId)
        writeLong(seriesId)
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "TeamMember"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::TeamMember)
    }

}