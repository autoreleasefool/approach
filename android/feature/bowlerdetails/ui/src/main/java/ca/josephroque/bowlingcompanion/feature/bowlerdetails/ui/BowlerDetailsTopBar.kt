package ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.model.LeagueSortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BowlerDetailsTopBar(
	state: BowlerDetailsTopBarUiState,
	onAction: (BowlerDetailsUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	MediumTopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = state.bowlerName,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(BowlerDetailsUiAction.BackClicked) })
		},
		actions = {
			if (state.isSortOrderMenuVisible) {
				Box {
					IconButton(onClick = { onAction(BowlerDetailsUiAction.SortClicked) }) {
						Icon(
							painter = painterResource(
								ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_sort,
							),
							contentDescription = stringResource(R.string.bowler_details_leagues_list_sort_order),
							tint = MaterialTheme.colorScheme.onSurface,
						)
					}
					
					DropdownMenu(
						expanded = state.isSortOrderMenuExpanded,
						onDismissRequest = { onAction(BowlerDetailsUiAction.SortDismissed) },
					) {
						LeagueSortOrder.entries.forEach { order ->
							DropdownMenuItem(
								text = {
									Text(
										when (order) {
											LeagueSortOrder.MOST_RECENTLY_USED -> stringResource(
												R.string.bowler_details_leagues_list_sort_order_most_recently_used,
											)
											LeagueSortOrder.ALPHABETICAL -> stringResource(
												R.string.bowler_details_leagues_list_sort_order_alphabetical,
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
								onClick = { onAction(BowlerDetailsUiAction.SortOrderClicked(order)) },
							)
						}
					}
				}
			}

			IconButton(onClick = { onAction(BowlerDetailsUiAction.AddLeagueClicked) }) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.bowler_details_league_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
		scrollBehavior = scrollBehavior,
	)
}
