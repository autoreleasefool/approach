package ca.josephroque.bowlingcompanion.core.statistics.trackable.series

import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.HighSeriesStatistic

data class HighSeriesOf8Statistic(override var highSeries: Int = 0) : HighSeriesStatistic {
	override val id = StatisticID.HIGH_SERIES_OF_8
	override val seriesSize: Int = 8
	override val isEligibleForNewLabel = true
	override fun emptyClone() = HighSeriesOf8Statistic()
}
