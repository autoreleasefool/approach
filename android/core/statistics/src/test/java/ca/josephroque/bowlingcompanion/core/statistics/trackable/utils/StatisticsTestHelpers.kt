package ca.josephroque.bowlingcompanion.core.statistics.trackable.utils

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSeriesConfiguration

fun <S : Statistic> assertStatisticAdjusts(
	statistic: S,
	bySeries: List<TrackableSeries> = emptyList(),
	byGames: List<TrackableGame> = emptyList(),
	byFrames: List<TrackableFrame> = emptyList(),
	perSeriesConfiguration: TrackablePerSeriesConfiguration = TrackablePerSeriesConfiguration,
	perGameConfiguration: TrackablePerGameConfiguration = TrackablePerGameConfiguration,
	perFrameConfiguration: TrackablePerFrameConfiguration = TrackablePerFrameConfiguration(),
): S {
	bySeries.forEach {
		statistic.adjustBySeries(it, perSeriesConfiguration)
	}

	byGames.forEach {
		statistic.adjustByGame(it, perGameConfiguration)
	}

	byFrames.forEach {
		statistic.adjustByFrame(it, perFrameConfiguration)
	}

	return statistic
}
