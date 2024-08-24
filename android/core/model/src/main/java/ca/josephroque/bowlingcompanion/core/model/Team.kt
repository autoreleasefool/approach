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

data class TeamListItem(
	val id: TeamID,
	val name: String,
	val bowlers: List<String>,
	val average: Double?,
)


data class TeamListItem(val id: TeamID, val name: String, val bowlers: String, val average: Double?)

data class TeamCreate(val id: TeamID, val name: String, val bowlers: List<BowlerID>)
