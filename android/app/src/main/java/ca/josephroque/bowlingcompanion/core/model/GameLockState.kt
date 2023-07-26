package ca.josephroque.bowlingcompanion.core.model

enum class GameLockState {
	LOCKED,
	UNLOCKED,
}

fun String?.asGameLockState() = when (this) {
	null -> null
	else -> GameLockState.values()
		.firstOrNull { it.name == this }
}