package ca.josephroque.bowlingcompanion.core.analytics.trackable.team

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data class TeamCreated(
	val numberOfMembers: Int,
) : TrackableEvent {
	override val name = "Team.Created"
	override val payload = mapOf(
		"NumberOfMembers" to numberOfMembers.toString(),
	)
}
