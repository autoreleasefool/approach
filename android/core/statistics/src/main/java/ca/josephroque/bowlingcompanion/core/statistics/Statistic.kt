package ca.josephroque.bowlingcompanion.core.statistics

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
import ca.josephroque.bowlingcompanion.core.statistics.utils.firstRolls
import ca.josephroque.bowlingcompanion.core.statistics.utils.rollPairs

interface Statistic {
	val id: StatisticID
	val category: StatisticCategory
	val isEligibleForNewLabel: Boolean
	val supportsAggregation: Boolean
	val supportsWidgets: Boolean
	val preferredTrendDirection: PreferredTrendDirection?

	val formattedValue: String
	val formattedValueDescription: String?
	val isEmpty: Boolean

	fun supportsSource(source: TrackableFilter.Source): Boolean
	fun adjustByFrame(frame: TrackableFrame, configuration: TrackablePerFrameConfiguration)
	fun adjustByGame(game: TrackableGame, configuration: TrackablePerGameConfiguration)
	fun adjustBySeries(series: TrackableSeries, configuration: TrackablePerSeriesConfiguration)
	fun aggregateWithStatistic(statistic: Statistic)
	fun emptyClone(): Statistic

	fun clone(): Statistic = emptyClone().apply {
		aggregateWithStatistic(this@Statistic)
	}
}

enum class StatisticCategory {
	OVERALL { override val titleResourceId = R.string.statistic_category_overall },
	MIDDLE_HITS { override val titleResourceId = R.string.statistic_category_middle_hits },
	STRIKES_AND_SPARES { override val titleResourceId = R.string.statistic_category_strikes_and_spares },
	HEAD_PINS { override val titleResourceId = R.string.statistic_category_head_pins },
	FIVES { override val titleResourceId = R.string.statistic_category_fives },
	THREES { override val titleResourceId = R.string.statistic_category_threes },
	ACES { override val titleResourceId = R.string.statistic_category_aces },
	CHOPS { override val titleResourceId = R.string.statistic_category_chops },
	SPLITS { override val titleResourceId = R.string.statistic_category_splits },
	TAPS { override val titleResourceId = R.string.statistic_category_taps },
	TWELVES { override val titleResourceId = R.string.statistic_category_twelves },
	FOULS { override val titleResourceId = R.string.statistic_category_fouls },
	PINS_LEFT_ON_DECK { override val titleResourceId = R.string.statistic_category_pins_left_on_deck },
	MATCH_PLAY_RESULTS { override val titleResourceId = R.string.statistic_category_match_play_results },
	SERIES { override val titleResourceId = R.string.statistic_category_series },
	;

	abstract val titleResourceId: Int
}

enum class StatisticID {
	ACES { override val titleResourceId = R.string.statistic_title_aces },
	ACES_SPARED { override val titleResourceId = R.string.statistic_title_aces_spared },
	CHOPS { override val titleResourceId = R.string.statistic_title_chop_offs },
	CHOPS_SPARED { override val titleResourceId = R.string.statistic_title_chop_offs_spared },
	FIVES { override val titleResourceId = R.string.statistic_title_fives },
	FIVES_SPARED { override val titleResourceId = R.string.statistic_title_fives_spared },
	HEAD_PINS { override val titleResourceId = R.string.statistic_title_head_pins },
	HEAD_PINS_SPARED { override val titleResourceId = R.string.statistic_title_head_pins_spared },
	LEFT_CHOPS { override val titleResourceId = R.string.statistic_title_left_chop_offs },
	LEFT_CHOPS_SPARED { override val titleResourceId = R.string.statistic_title_left_chop_offs_spared },
	LEFT_FIVES { override val titleResourceId = R.string.statistic_title_left_fives },
	LEFT_FIVES_SPARED { override val titleResourceId = R.string.statistic_title_left_fives_spared },
	LEFT_SPLITS { override val titleResourceId = R.string.statistic_title_left_splits },
	LEFT_SPLITS_SPARED { override val titleResourceId = R.string.statistic_title_left_splits_spared },
	LEFT_TAPS { override val titleResourceId = R.string.statistic_title_left_taps },
	LEFT_TAPS_SPARED { override val titleResourceId = R.string.statistic_title_left_taps_spared },
	LEFT_THREES { override val titleResourceId = R.string.statistic_title_left_threes },
	LEFT_THREES_SPARED { override val titleResourceId = R.string.statistic_title_left_threes_spared },
	LEFT_TWELVES { override val titleResourceId = R.string.statistic_title_left_twelves },
	LEFT_TWELVES_SPARED { override val titleResourceId = R.string.statistic_title_left_twelves_spared },
	RIGHT_CHOPS { override val titleResourceId = R.string.statistic_title_right_chop_offs },
	RIGHT_CHOPS_SPARED { override val titleResourceId = R.string.statistic_title_right_chop_offs_spared },
	RIGHT_FIVES { override val titleResourceId = R.string.statistic_title_right_fives },
	RIGHT_FIVES_SPARED { override val titleResourceId = R.string.statistic_title_right_fives_spared },
	RIGHT_SPLITS { override val titleResourceId = R.string.statistic_title_right_splits },
	RIGHT_SPLITS_SPARED { override val titleResourceId = R.string.statistic_title_right_splits_spared },
	RIGHT_TAPS { override val titleResourceId = R.string.statistic_title_right_taps },
	RIGHT_TAPS_SPARED { override val titleResourceId = R.string.statistic_title_right_taps_spared },
	RIGHT_THREES { override val titleResourceId = R.string.statistic_title_right_threes },
	RIGHT_THREES_SPARED { override val titleResourceId = R.string.statistic_title_right_threes_spared },
	RIGHT_TWELVES { override val titleResourceId = R.string.statistic_title_right_twelves },
	RIGHT_TWELVES_SPARED { override val titleResourceId = R.string.statistic_title_right_twelves_spared },
	SPLITS { override val titleResourceId = R.string.statistic_title_splits },
	SPLITS_SPARED { override val titleResourceId = R.string.statistic_title_splits_spared },
	TAPS { override val titleResourceId = R.string.statistic_title_taps },
	TAPS_SPARED { override val titleResourceId = R.string.statistic_title_taps_spared },
	THREES { override val titleResourceId = R.string.statistic_title_threes },
	THREES_SPARED { override val titleResourceId = R.string.statistic_title_threes_spared },
	TWELVES { override val titleResourceId = R.string.statistic_title_twelves },
	TWELVES_SPARED { override val titleResourceId = R.string.statistic_title_twelves_spared },
	FOULS { override val titleResourceId = R.string.statistic_title_fouls },
	SPARE_CONVERSIONS { override val titleResourceId = R.string.statistic_title_spare_conversions },
	STRIKES { override val titleResourceId = R.string.statistic_title_strikes },
	MATCHES_LOST { override val titleResourceId = R.string.statistic_title_match_play_losses },
	MATCHES_WON { override val titleResourceId = R.string.statistic_title_match_play_wins },
	MATCHES_TIED { override val titleResourceId = R.string.statistic_title_match_play_ties },
	MATCHES_PLAYED { override val titleResourceId = R.string.statistic_title_match_plays },
	LEFT_OF_MIDDLE_HITS { override val titleResourceId = R.string.statistic_title_left_of_middle_hits },
	RIGHT_OF_MIDDLE_HITS { override val titleResourceId = R.string.statistic_title_right_of_middle_hits },
	MIDDLE_HITS { override val titleResourceId = R.string.statistic_title_middle_hits },
	STRIKE_MIDDLE_HITS { override val titleResourceId = R.string.statistic_title_strike_middle_hits },
	GAME_AVERAGE { override val titleResourceId = R.string.statistic_title_game_average },
	HIGH_SINGLE_GAME { override val titleResourceId = R.string.statistic_title_high_single },
	NUMBER_OF_GAMES { override val titleResourceId = R.string.statistic_title_number_of_games },
	TOTAL_PIN_FALL { override val titleResourceId = R.string.statistic_title_total_pin_fall },
	TOTAL_ROLLS { override val titleResourceId = R.string.statistic_title_total_rolls },
	AVERAGE_PINS_LEFT_ON_DECK { override val titleResourceId = R.string.statistic_title_average_pins_left_on_deck },
	TOTAL_PINS_LEFT_ON_DECK { override val titleResourceId = R.string.statistic_title_pins_left_on_deck },
	HIGH_SERIES_OF_3 { override val titleResourceId = R.string.statistic_title_high_series_of_3 },
	;

	abstract val titleResourceId: Int
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