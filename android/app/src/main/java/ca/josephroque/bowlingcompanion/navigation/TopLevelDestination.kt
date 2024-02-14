package ca.josephroque.bowlingcompanion.navigation

import androidx.annotation.DrawableRes
import ca.josephroque.bowlingcompanion.R

enum class TopLevelDestination(
	@DrawableRes val selectedIcon: Int,
	@DrawableRes val unselectedIcon: Int,
	val iconTextId: Int,
) {
	APP_OVERVIEW(
		selectedIcon = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_bowling_ball,
		unselectedIcon = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_bowling_ball,
		iconTextId = R.string.destination_overview,
	),

	STATISTICS_OVERVIEW(
		selectedIcon = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_monitoring,
		unselectedIcon = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_monitoring,
		iconTextId = R.string.destination_statistics,
	),

	ACCESSORIES_OVERVIEW(
		selectedIcon = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_alley,
		unselectedIcon = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_alley,
		iconTextId = R.string.destination_accessories,
	),

	SETTINGS_OVERVIEW(
		selectedIcon = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_settings,
		unselectedIcon = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_settings,
		iconTextId = R.string.destination_settings,
	),
	;

	val graphName: String
		get() = "${name.lowercase()}_graph"
}
