package ca.josephroque.bowlingcompanion.core.analytics.trackable.matchplay

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data class MatchPlayUpdated(
	val withOpponent: Boolean,
	val withScore: Boolean,
	val withResult: Boolean,
) : TrackableEvent {
	override val name = "MatchPlay.Created"
	override val payload = mapOf(
		"WithOpponent" to withOpponent.toString(),
		"WithScore" to withScore.toString(),
		"WithResult" to withResult.toString(),
	)
}
