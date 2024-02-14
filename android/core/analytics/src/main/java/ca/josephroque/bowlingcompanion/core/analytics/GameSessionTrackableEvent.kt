package ca.josephroque.bowlingcompanion.core.analytics

import java.util.UUID

interface GameSessionTrackableEvent : TrackableEvent {
	val eventId: UUID
}
