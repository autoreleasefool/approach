package ca.josephroque.bowlingcompanion.feature.gearlist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.components.filterDescription

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearListTopBar(
	state: GearListTopBarState,
	onBackPressed: () -> Unit,
	onShowGearFilter: () -> Unit,
	onMinimizeGearFilter: () -> Unit,
	onGearFilterChanged: (GearKind?) -> Unit,
	onAddGear: () -> Unit,
) {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.gear_list_title),
				style = MaterialTheme.typography.titleLarge,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		},
		navigationIcon = { BackButton(onClick = onBackPressed) },
		actions = {
			FilterMenuItem(
				state = state,
				onShowGearFilter = onShowGearFilter,
				onMinimizeGearFilter = onMinimizeGearFilter,
				onGearFilterChanged = onGearFilterChanged,
			)

			IconButton(onClick = onAddGear) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.gear_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	)
}

@Composable
private fun FilterMenuItem(
	state: GearListTopBarState,
	onShowGearFilter: () -> Unit,
	onMinimizeGearFilter: () -> Unit,
	onGearFilterChanged: (GearKind?) -> Unit,
) {
	Box {
		IconButton(onClick = onShowGearFilter) {
			Icon(
				painter = painterResource(if (state.kindFilter == null) RCoreDesign.drawable.ic_filter_off else RCoreDesign.drawable.ic_filter),
				contentDescription = stringResource(R.string.gear_list_filter),
				tint = MaterialTheme.colorScheme.onSurface,
			)
		}

		DropdownMenu(
			expanded = state.isFilterMenuVisible,
			onDismissRequest = onMinimizeGearFilter
		) {
			GearKind.values().forEach {
				DropdownMenuItem(
					text = {
						Text(
							text = it.filterDescription(),
							style = MaterialTheme.typography.bodyMedium,
						)
					},
					trailingIcon = {
						if (state.kindFilter == it) {
							Icon(
								imageVector = Icons.Default.Check,
								contentDescription = stringResource(RCoreDesign.string.cd_selected),
							)
						}
					},
					onClick = { onGearFilterChanged(it) },
				)
			}

			DropdownMenuItem(
				text = {
					Text(
						text = stringResource(if (state.kindFilter == null) RCoreDesign.string.filter_none else RCoreDesign.string.filter_reset),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.error,
					)
				},
				trailingIcon = {
					if (state.kindFilter == null) {
						Icon(
							imageVector = Icons.Default.Check,
							contentDescription = stringResource(RCoreDesign.string.cd_selected),
						)
					}
				},
				onClick = { onGearFilterChanged(null) },
			)
		}
	}
}

data class GearListTopBarState(
	val kindFilter: GearKind? = null,
	val isFilterMenuVisible: Boolean = false,
)