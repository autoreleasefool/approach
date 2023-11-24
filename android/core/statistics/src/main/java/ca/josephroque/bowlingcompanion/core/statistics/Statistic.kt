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

	fun supportsSource(source: TrackableFilter.Source): Boolean
	fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration)
	fun adjustByGame(game: TrackableGame, configuration: TrackablePerGameConfiguration)
	fun adjustBySeries(series: TrackableSeries, configuration: TrackablePerSeriesConfiguration)
	fun aggregateWithStatistic(statistic: Statistic)
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

	val titleResourceId: Int
		get() = when (this) {
			OVERALL -> R.string.statistic_category_overall
			MIDDLE_HITS -> R.string.statistic_category_middle_hits
			STRIKES_AND_SPARES -> R.string.statistic_category_strikes_and_spares
			HEAD_PINS -> R.string.statistic_category_head_pins
			FIVES -> R.string.statistic_category_fives
			THREES -> R.string.statistic_category_threes
			ACES -> R.string.statistic_category_aces
			CHOPS -> R.string.statistic_category_chops
			SPLITS -> R.string.statistic_category_splits
			TAPS -> R.string.statistic_category_taps
			TWELVES -> R.string.statistic_category_twelves
			FOULS -> R.string.statistic_category_fouls
			PINS_LEFT_ON_DECK -> R.string.statistic_category_pins_left_on_deck
			MATCH_PLAY_RESULTS -> R.string.statistic_category_match_play_results
			SERIES -> R.string.statistic_category_series
		}
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