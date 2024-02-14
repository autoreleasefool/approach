package ca.josephroque.bowlingcompanion.core.statistics.models

import androidx.annotation.StringRes
import ca.josephroque.bowlingcompanion.core.statistics.Statistic

data class StatisticGroup(
	@StringRes val title: Int,
	val statistics: List<Statistic>,
)
