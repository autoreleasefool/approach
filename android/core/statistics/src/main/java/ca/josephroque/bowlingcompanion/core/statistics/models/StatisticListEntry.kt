package ca.josephroque.bowlingcompanion.core.statistics.models

import androidx.annotation.StringRes

data class StatisticListEntry(
	@StringRes val title: Int,
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