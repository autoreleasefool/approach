package ca.josephroque.bowlingcompanion.navigation

import androidx.annotation.DrawableRes
import ca.josephroque.bowlingcompanion.R

enum class TopLevelDestination(
	@DrawableRes val selectedIcon: Int,
	@DrawableRes val unselectedIcon: Int,
	val iconTextId: Int,
	val titleTextId: Int,
) {
	OVERVIEW(
		selectedIcon = R.drawable.ic_bowling_ball,
		unselectedIcon = R.drawable.ic_bowling_ball,
		iconTextId = R.string.destination_overview,
		titleTextId = R.string.app_name,
	),

	STATISTICS(
		selectedIcon = R.drawable.ic_monitoring,
		unselectedIcon = R.drawable.ic_monitoring,
		iconTextId = R.string.destination_statistics,
		titleTextId = R.string.destination_statistics,
	),

	ACCESSORIES(
		selectedIcon = R.drawable.ic_accessory,
		unselectedIcon = R.drawable.ic_accessory,
		iconTextId = R.string.destination_accessories,
		titleTextId = R.string.destination_accessories,
	),

	SETTINGS(
		selectedIcon = R.drawable.ic_settings,
		unselectedIcon = R.drawable.ic_settings,
		iconTextId = R.string.destination_settings,
		titleTextId = R.string.destination_settings,
	),
}