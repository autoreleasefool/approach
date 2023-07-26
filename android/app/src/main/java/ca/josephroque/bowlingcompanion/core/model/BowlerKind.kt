package ca.josephroque.bowlingcompanion.core.model

enum class BowlerKind {
	PLAYABLE,
	OPPONENT,
}

fun String?.asBowlerKind() = when (this) {
	null -> null
	else -> BowlerKind.values()
		.firstOrNull { it.name == this }
}