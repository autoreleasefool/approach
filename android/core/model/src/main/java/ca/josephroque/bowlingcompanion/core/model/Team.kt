package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import java.util.UUID
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class TeamID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): TeamID = TeamID(UUID.randomUUID())
		fun fromString(string: String): TeamID = TeamID(UUID.fromString(string))
	}
}

enum class TeamSortOrder {
	MOST_RECENTLY_USED,
	ALPHABETICAL,
}

data class Team(val id: TeamID, val name: String)

data class TeamCreate(val id: UUID, val name: String, val members: List<TeamMemberListItem>)

data class TeamListItem(val id: UUID, val name: String, val members: String, val average: Double?) {
	fun membersList() = members.split(";").sorted()
}

data class TeamMemberListItem(val id: UUID, val name: String)

data class TeamUpdate(val id: TeamID, val name: String, val members: List<TeamMemberListItem>) {
	data class Properties(val id: TeamID, val name: String)
}
