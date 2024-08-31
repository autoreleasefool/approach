package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import java.util.UUID
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class BowlerID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): BowlerID = BowlerID(UUID.randomUUID())
		fun fromString(string: String): BowlerID = BowlerID(UUID.fromString(string))
	}
}

enum class BowlerSortOrder {
	MOST_RECENTLY_USED,
	ALPHABETICAL,
}

data class BowlerListItem(val id: BowlerID, val name: String, val average: Double?) {
	fun asSummary(): BowlerSummary = BowlerSummary(id, name)
}

data class BowlerSummary(val id: BowlerID, val name: String)

data class SeriesBowlerSummary(val seriesId: UUID, val id: BowlerID, val name: String) {
	fun asSummary(): BowlerSummary = BowlerSummary(id, name)
}

data class OpponentListItem(val id: BowlerID, val name: String, val kind: BowlerKind)

data class BowlerDetails(val id: BowlerID, val name: String, val kind: BowlerKind)

data class BowlerCreate(val id: BowlerID, val name: String, val kind: BowlerKind)

data class BowlerUpdate(val id: BowlerID, val name: String)

data class ArchivedBowler(
	val id: BowlerID,
	val name: String,
	val numberOfLeagues: Int,
	val numberOfSeries: Int,
	val numberOfGames: Int,
	val archivedOn: Instant,
)

enum class BowlerKind {
	PLAYABLE,
	OPPONENT,
}
