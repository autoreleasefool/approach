package ca.josephroque.bowlingcompanion.core.analytics

interface TrackableEvent {
	val name: String
	val payload: Map<String, String>?
}
