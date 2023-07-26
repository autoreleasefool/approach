package ca.josephroque.bowlingcompanion.core.model

enum class GearKind {
	SHOES,
	BOWLING_BALL,
	TOWEL,
	OTHER,
}

fun String?.asGearKind() = when (this) {
	null -> null
	else -> GearKind.values()
		.firstOrNull { it.name == this }
}