package ca.josephroque.bowlingcompanion.core.analytics.trackable.onboarding

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object OnboardingErrorReported: TrackableEvent {
	override val name = "Onboarding.ErrorReported"
	override val payload = emptyMap<String, String>()
}