package ca.josephroque.bowlingcompanion.core.error

interface ErrorReporting {
	fun captureException(throwable: Throwable)
	fun captureMessage(message: String)
}
