package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID
import kotlinx.datetime.Instant

data class BowlerListItem(
	val id: UUID,
	val name: String,
	val average: Double?,
) {
	fun asSummary(): BowlerSummary = BowlerSummary(id, name)
}

data class BowlerSummary(
	val id: UUID,
	val name: String,
)

data class SeriesBowlerSummary(
	val seriesId: UUID,
	val id: UUID,
	val name: String,
) {
	fun asSummary(): BowlerSummary = BowlerSummary(id, name)
}

data class OpponentListItem(
	val id: UUID,
	val name: String,
	val kind: BowlerKind,
)

data class BowlerDetails(
	val id: UUID,
	val name: String,
	val kind: BowlerKind,
)

data class BowlerCreate(
	val id: UUID,
	val name: String,
	val kind: BowlerKind,
)

data class BowlerUpdate(
	val id: UUID,
	val name: String,
)

data class ArchivedBowler(
	val id: UUID,
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
