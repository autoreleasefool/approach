package ca.josephroque.bowlingcompanion.core.analytics.trackable.app

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data object AppOnboardingCompleted: TrackableEvent {
	override val name = "App.OnboardingCompleted"
	override val payload = emptyMap<String, String>()
}