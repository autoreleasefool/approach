package ca.josephroque.bowlingcompanion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import ca.josephroque.bowlingcompanion.R

enum class TopLevelDestination(
	val selectedIcon: ImageVector,
	val unselectedIcon: ImageVector,
	val iconTextId: Int,
	val titleTextId: Int
) {
	OVERVIEW(
		selectedIcon = Icons.Filled.AccountCircle,
		unselectedIcon = Icons.Outlined.AccountCircle,
		iconTextId = R.string.destination_overview,
		titleTextId = R.string.app_name
	),

	STATISTICS(
		selectedIcon = Icons.Filled.Add,
		unselectedIcon = Icons.Outlined.Add,
		iconTextId = R.string.destination_statistics,
		titleTextId = R.string.destination_statistics
	),

	ACCESSORIES(
		selectedIcon = Icons.Filled.Call,
		unselectedIcon = Icons.Outlined.Call,
		iconTextId = R.string.destination_accessories,
		titleTextId = R.string.destination_accessories
	),

	SETTINGS(
		selectedIcon = Icons.Filled.Settings,
		unselectedIcon = Icons.Outlined.Settings,
		iconTextId = R.string.destination_settings,
		titleTextId = R.string.destination_settings
	)
}