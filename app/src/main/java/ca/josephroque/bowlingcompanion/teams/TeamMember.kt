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
        val teamId: Long,
        val bowlerName: String,
        val bowlerId: Long,
        val leagueName: String? = null,
        val leagueId: Long = -1,
        val seriesName: String? = null,
        val seriesId: Long = -1
) : IIdentifiable, KParcelable {

    /**
     * ID is a concatenation of the first 32 bits of the team ID and first 32 bits
     * of the bowler ID.
     */
    override val id: Long
        get() = teamId.shl(TEAM_ID_SHIFT).or(bowlerId.and(BOWLER_ID_TRIM))

    /**
     * Construct [TeamMember] from a [Parcel]
     */
    private constructor(p: Parcel): this(
            teamId = p.readLong(),
            bowlerName = p.readString(),
            bowlerId = p.readLong(),
            leagueName = p.readString(),
            leagueId = p.readLong(),
            seriesName = p.readString(),
            seriesId = p.readLong()
    )

    /**
     * Construct [TeamMember] from a [TeamMember].
     */
    constructor(teamMember: TeamMember): this(
            teamId = teamMember.teamId,
            bowlerName = teamMember.bowlerName,
            bowlerId = teamMember.bowlerId,
            leagueName = teamMember.leagueName,
            leagueId = teamMember.leagueId,
            seriesName = teamMember.seriesName,
            seriesId = teamMember.seriesId
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(teamId)
        writeString(bowlerName)
        writeLong(bowlerId)
        writeString(leagueName)
        writeLong(leagueId)
        writeString(seriesName)
        writeLong(seriesId)
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "TeamMember"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::TeamMember)

        /** Bit count to shift team id for team member id. */
        private const val TEAM_ID_SHIFT = 32
        /** Number of bits to use from beginning of bowler id for team member id. */
        private const val BOWLER_ID_TRIM = 0xFFFFFFFF
    }

}