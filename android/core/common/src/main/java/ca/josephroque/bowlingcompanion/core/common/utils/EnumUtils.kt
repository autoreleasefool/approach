package ca.josephroque.bowlingcompanion.core.common.utils

inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String): T? {
	return try {
		enumValueOf<T>(name)
	} catch (e: IllegalArgumentException) {
		null
	}
}
