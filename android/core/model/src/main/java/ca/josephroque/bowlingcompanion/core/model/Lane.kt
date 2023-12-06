package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class LaneListItem(
	val id: UUID,
	val label: String,
	val position: LanePosition,
) {
	fun createForAlley(alleyId: UUID): LaneCreate = LaneCreate(
		id = id,
		alleyId = alleyId,
		label = label,
		position = position,
	)
}

data class LaneCreate(
	val id: UUID,
	val alleyId: UUID,
	val label: String,
	val position: LanePosition,
) {
	fun asListItem(): LaneListItem = LaneListItem(
		id = id,
		label = label,
		position = position,
	)
}

enum class LanePosition {
	LEFT_WALL,
	RIGHT_WALL,
	NO_WALL,
}