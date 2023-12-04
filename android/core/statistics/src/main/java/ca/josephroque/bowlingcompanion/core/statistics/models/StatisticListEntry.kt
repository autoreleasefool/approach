package ca.josephroque.bowlingcompanion.core.statistics.models

import androidx.annotation.StringRes
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID

data class StatisticListEntry(
	val id: StatisticID,
	@StringRes val description: Int?,
	val value: String,
	val isHighlightedAsNew: Boolean,
)

data class StatisticListEntryGroup(
	@StringRes val title: Int,
	@StringRes val description: Int?,
	val images: List<Int>?,
	val entries: List<StatisticListEntry>,
)