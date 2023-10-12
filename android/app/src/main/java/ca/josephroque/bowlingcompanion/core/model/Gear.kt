package ca.josephroque.bowlingcompanion.core.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class GearListItem(
	val id: UUID,
	val name: String,
	val kind: GearKind,
	val ownerName: String?,
//	val avatar: Avatar,
)

enum class GearKind {
	SHOES,
	BOWLING_BALL,
	TOWEL,
	OTHER,
}