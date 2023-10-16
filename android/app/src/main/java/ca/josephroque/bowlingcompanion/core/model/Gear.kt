package ca.josephroque.bowlingcompanion.core.model

import androidx.compose.runtime.Immutable
import ca.josephroque.bowlingcompanion.utils.SortableByUUID
import java.util.UUID

@Immutable
data class GearListItem(
	override val id: UUID,
	val name: String,
	val kind: GearKind,
	val ownerName: String?,
//	val avatar: Avatar,
): SortableByUUID

enum class GearKind {
	SHOES,
	BOWLING_BALL,
	TOWEL,
	OTHER,
}