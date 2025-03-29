package ca.josephroque.bowlingcompanion.feature.overview.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ca.josephroque.bowlingcompanion.core.model.BowlerSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewTopBar(
	state: OverviewTopBarUiState,
	onAction: (OverviewUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	CenterAlignedTopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = stringResource(R.string.overview_title),
				style = MaterialTheme.typography.titleLarge,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		},
		actions = {
			when (state) {
				is OverviewTopBarUiState.BowlerTab -> {
					BowlerTabTopBarActions(state, onAction)
				}
				is OverviewTopBarUiState.TeamTab -> {
					TeamTabTopBarActions(state, onAction)
				}
			}
		},
	)
}

@Composable
fun TeamTabTopBarActions(state: OverviewTopBarUiState.TeamTab, onAction: (OverviewUiAction) -> Unit) {
	if (state.isSortOrderMenuVisible) {
		Box {
			IconButton(onClick = { onAction(OverviewUiAction.TeamsSortClicked) }) {
				Icon(
					painter = painterResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_sort,
					),
					contentDescription = stringResource(R.string.overview_team_list_sort_order),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}

			DropdownMenu(
				expanded = state.isSortOrderMenuExpanded,
				onDismissRequest = { onAction(OverviewUiAction.TeamsSortDismissed) },
			) {
				TeamSortOrder.entries.forEach { order ->
					DropdownMenuItem(
						text = {
							Text(
								when (order) {
									TeamSortOrder.MOST_RECENTLY_USED -> stringResource(
										R.string.overview_team_list_sort_order_most_recently_used,
									)
									TeamSortOrder.ALPHABETICAL -> stringResource(
										R.string.overview_team_list_sort_order_alphabetical,
									)
								},
								style = MaterialTheme.typography.bodyMedium,
							)
						},
						trailingIcon = {
							if (state.sortOrder == order) {
								Icon(
									imageVector = Icons.Filled.Check,
									contentDescription = null,
									tint = MaterialTheme.colorScheme.primary,
								)
							}
						},
						onClick = { onAction(OverviewUiAction.TeamsSortOrderClicked(order)) },
					)
				}
			}
		}
	}

	IconButton(onClick = { onAction(OverviewUiAction.AddTeamClicked) }) {
		Icon(
			painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_add_group),
			contentDescription = stringResource(R.string.team_list_add),
			tint = MaterialTheme.colorScheme.onSurface,
		)
	}
}

@Composable
fun BowlerTabTopBarActions(state: OverviewTopBarUiState.BowlerTab, onAction: (OverviewUiAction) -> Unit) {
	if (state.isSortOrderMenuVisible) {
		Box {
			IconButton(onClick = { onAction(OverviewUiAction.BowlersSortClicked) }) {
				Icon(
					painter = painterResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_sort,
					),
					contentDescription = stringResource(R.string.overview_bowler_list_sort_order),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}

			DropdownMenu(
				expanded = state.isSortOrderMenuExpanded,
				onDismissRequest = { onAction(OverviewUiAction.BowlersSortDismissed) },
			) {
				BowlerSortOrder.entries.forEach { order ->
					DropdownMenuItem(
						text = {
							Text(
								when (order) {
									BowlerSortOrder.MOST_RECENTLY_USED -> stringResource(
										R.string.overview_bowler_list_sort_order_most_recently_used,
									)
									BowlerSortOrder.ALPHABETICAL -> stringResource(
										R.string.overview_bowler_list_sort_order_alphabetical,
									)
								},
								style = MaterialTheme.typography.bodyMedium,
							)
						},
						trailingIcon = {
							if (state.sortOrder == order) {
								Icon(
									imageVector = Icons.Filled.Check,
									contentDescription = null,
									tint = MaterialTheme.colorScheme.primary,
								)
							}
						},
						onClick = { onAction(OverviewUiAction.BowlersSortOrderClicked(order)) },
					)
				}
			}
		}
	}

	IconButton(onClick = { onAction(OverviewUiAction.AddBowlerClicked) }) {
		Icon(
			painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_add_person),
			contentDescription = stringResource(R.string.bowler_list_add),
			tint = MaterialTheme.colorScheme.onSurface,
		)
	}
}
