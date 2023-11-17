package ca.josephroque.bowlingcompanion.core.statistics

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
import ca.josephroque.bowlingcompanion.core.statistics.utils.firstRolls
import ca.josephroque.bowlingcompanion.core.statistics.utils.rollPairs

interface Statistic {
	val titleResourceId: Int
	val category: StatisticCategory
	val isEligibleForNewLabel: Boolean
	val supportsAggregation: Boolean
	val supportsWidgets: Boolean
	val preferredTrendDirection: PreferredTrendDirection?

	val formattedValue: String
	val isEmpty: Boolean

	fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration)
	fun adjustByGame(game: TrackableGame, configuration: TrackablePerGameConfiguration)
	fun adjustBySeries(series: TrackableSeries, configuration: TrackablePerSeriesConfiguration)
}

enum class StatisticCategory {
	OVERALL,
	MIDDLE_HITS,
	STRIKES_AND_SPARES,
	HEAD_PINS,
	FIVES,
	THREES,
	ACES,
	CHOPS,
	SPLITS,
	TAPS,
	TWELVES,
	FOULS,
	PINS_LEFT_ON_DECK,
	MATCH_PLAY_RESULTS,
	SERIES;
}

enum class PreferredTrendDirection {
	UPWARDS,
	DOWNWARDS,
}

data class TrackablePerFrameConfiguration(
	val countHeadPin2AsHeadPin: Boolean = true,
	val countSplitWithBonusAsSplit: Boolean = true,
)

interface TrackablePerFrame: Statistic {
	override fun adjustByGame(game: TrackableGame, configuration: TrackablePerGameConfiguration) {
		// Intentionally left blank
	}

	override fun adjustBySeries(
		series: TrackableSeries,
		configuration: TrackablePerSeriesConfiguration
	) {
		// Intentionally left blank
	}
}

interface TrackablePerFirstRoll: TrackablePerFrame {
	fun adjustByFirstRoll(firstRoll: TrackableFrame.Roll, configuration: TrackablePerFrameConfiguration)

	override fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration) {
		for (roll in frame.firstRolls) {
			adjustByFirstRoll(roll, configuration)
		}
	}
}

interface TrackablePerSecondRoll: TrackablePerFrame {
	fun adjustByFirstRollFollowedBySecondRoll(
		firstRoll: TrackableFrame.Roll,
		secondRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration,
	)

	override fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration) {
		for (rollPair in frame.rollPairs) {
			adjustByFirstRollFollowedBySecondRoll(
				rollPair.firstRoll,
				rollPair.secondRoll,
				configuration
			)
		}
	}
}

data object TrackablePerGameConfiguration

interface TrackablePerGame: Statistic {
	override fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration) {
		// Intentionally left blank
	}

	override fun adjustBySeries(
		series: TrackableSeries,
		configuration: TrackablePerSeriesConfiguration
	) {
		// Intentionally left blank
	}
}

data object TrackablePerSeriesConfiguration

interface TrackablePerSeries: Statistic {
	override fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration) {
		// Intentionally left blank
	}

	override fun adjustByGame(game: TrackableGame, configuration: TrackablePerGameConfiguration) {
		// Intentionally left blank
	}
}