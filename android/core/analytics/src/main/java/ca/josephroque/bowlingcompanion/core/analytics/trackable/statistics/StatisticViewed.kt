package ca.josephroque.bowlingcompanion.core.analytics.trackable.statistics

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

data class StatisticViewed(
	val statisticName: String,
	val countsH2AsH: Boolean,
	val countsS2AsS: Boolean,
): TrackableEvent {
	override val name = "Statistics.Viewed"
	override val payload = mapOf(
		"StatisticName" to statisticName,
		"CountsH2AsH" to countsH2AsH.toString(),
		"CountsS2AsS" to countsS2AsS.toString(),
	)
}