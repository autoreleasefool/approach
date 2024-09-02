package ca.josephroque.bowlingcompanion.core.analytics.trackable.team

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data class TeamUpdated(
	val numberOfMembers: Int,
) : TrackableEvent {
	override val name = "Team.Updated"
	override val payload = mapOf(
		"NumberOfMembers" to numberOfMembers.toString(),
	)
}
