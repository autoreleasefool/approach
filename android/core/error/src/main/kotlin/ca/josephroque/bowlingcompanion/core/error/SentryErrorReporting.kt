package ca.josephroque.bowlingcompanion.core.error

import io.sentry.Sentry
import javax.inject.Inject

class SentryErrorReporting @Inject constructor() : ErrorReporting {
	override fun captureException(throwable: Throwable) {
		Sentry.captureException(throwable)
	}
}
