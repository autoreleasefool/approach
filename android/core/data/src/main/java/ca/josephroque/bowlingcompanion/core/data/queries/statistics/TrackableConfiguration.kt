package ca.josephroque.bowlingcompanion.core.data.queries.statistics

import ca.josephroque.bowlingcompanion.core.model.UserData
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSeriesConfiguration

fun UserData.perFrameConfiguration(): TrackablePerFrameConfiguration = TrackablePerFrameConfiguration(
	countHeadPin2AsHeadPin = !isCountingH2AsHDisabled,
	countSplitWithBonusAsSplit = !isCountingSplitWithBonusAsSplitDisabled
)

@Suppress("UnusedReceiverParameter")
fun UserData.perGameConfiguration(): TrackablePerGameConfiguration = TrackablePerGameConfiguration

@Suppress("UnusedReceiverParameter")
fun UserData.perSeriesConfiguration(): TrackablePerSeriesConfiguration = TrackablePerSeriesConfiguration