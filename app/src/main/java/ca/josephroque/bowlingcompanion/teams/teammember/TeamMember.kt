package ca.josephroque.bowlingcompanion.teams.teammember

import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.series.Series

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Member of a team.
 */
class TeamMember(
    val teamId: Long,
    val bowlerName: String,
    val bowlerId: Long,
    val league: League? = null,
    val series: Series? = null
) : IIdentifiable, KParcelable {

    companion object {
        @Suppress("unused")
        private const val TAG = "TeamMember"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::TeamMember)

        private const val TEAM_ID_SHIFT = 32
        private const val BOWLER_ID_TRIM = 0xFFFFFFFF
    }

    /**
     * ID is a concatenation of the first 32 bits of the team ID and first 32 bits
     * of the bowler ID.
     */
    override val id: Long
        get() = teamId.shl(TEAM_ID_SHIFT).or(bowlerId.and(BOWLER_ID_TRIM))

    // MARK: Constructors

    private constructor(p: Parcel): this(
            teamId = p.readLong(),
            bowlerName = p.readString()!!,
            bowlerId = p.readLong(),
            league = p.readParcelable<League>(League::class.java.classLoader),
            series = p.readParcelable<Series>(Series::class.java.classLoader)
    )

    constructor(teamMember: TeamMember): this(
            teamId = teamMember.teamId,
            bowlerName = teamMember.bowlerName,
            bowlerId = teamMember.bowlerId,
            league = teamMember.league,
            series = teamMember.series
    )

    // MARK: Parcelable

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(teamId)
        writeString(bowlerName)
        writeLong(bowlerId)
        writeParcelable(league, 0)
        writeParcelable(series, 0)
    }
}
