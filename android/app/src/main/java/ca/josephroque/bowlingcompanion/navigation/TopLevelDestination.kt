package ca.josephroque.bowlingcompanion.navigation

import androidx.annotation.DrawableRes
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.R

enum class TopLevelDestination(
	@DrawableRes val selectedIcon: Int,
	@DrawableRes val unselectedIcon: Int,
	val iconTextId: Int,
	val titleTextId: Int,
) {
	OVERVIEW(
		selectedIcon = RCoreDesign.drawable.ic_bowling_ball,
		unselectedIcon = RCoreDesign.drawable.ic_bowling_ball,
		iconTextId = R.string.destination_overview,
		titleTextId = R.string.app_name,
	),

	STATISTICS(
		selectedIcon = RCoreDesign.drawable.ic_monitoring,
		unselectedIcon = RCoreDesign.drawable.ic_monitoring,
		iconTextId = R.string.destination_statistics,
		titleTextId = R.string.destination_statistics,
	),

	ACCESSORIES(
		selectedIcon = RCoreDesign.drawable.ic_accessory,
		unselectedIcon = RCoreDesign.drawable.ic_accessory,
		iconTextId = R.string.destination_accessories,
		titleTextId = R.string.destination_accessories,
	),

	SETTINGS(
		selectedIcon = RCoreDesign.drawable.ic_settings,
		unselectedIcon = RCoreDesign.drawable.ic_settings,
		iconTextId = R.string.destination_settings,
		titleTextId = R.string.destination_settings,
	),
}