package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.pinCount
import ca.josephroque.bowlingcompanion.core.statistics.PreferredTrendDirection
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFirstRoll
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.AveragingStatistic

data class AverageFirstRollStatistic(
	var totalFirstRollPinfall: Int = 0,
	var totalFirstRolls: Int = 0,
) : TrackablePerFirstRoll, AveragingStatistic {
	override val id = StatisticID.AVERAGE_FIRST_ROLL
	override val category = StatisticCategory.FIRST_ROLL
	override val isEligibleForNewLabel = true
	override val preferredTrendDirection = PreferredTrendDirection.UPWARDS
	override fun emptyClone() = AverageFirstRollStatistic()

	override var total: Int
		get() = totalFirstRollPinfall
		set(value) {
			totalFirstRollPinfall = value
		}

	override var divisor: Int
		get() = totalFirstRolls
		set(value) {
			totalFirstRolls = value
		}

	override fun adjustByFirstRoll(
		firstRoll: TrackableFrame.Roll,
		configuration: TrackablePerFrameConfiguration,
	) {
		totalFirstRollPinfall += firstRoll.pinsDowned.pinCount()
		totalFirstRolls++
	}

	override fun supportsSource(source: TrackableFilter.Source): Boolean = when (source) {
		is TrackableFilter.Source.Bowler -> true
		is TrackableFilter.Source.League -> true
		is TrackableFilter.Source.Series -> true
		is TrackableFilter.Source.Game -> true
	}
}
