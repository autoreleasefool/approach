package ca.josephroque.bowlingcompanion.feature.leaguedetails.ui

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueDetailsTopBar(
	state: LeagueDetailsTopBarUiState,
	onAction: (LeagueDetailsUiAction) -> Unit,
) {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = state.leagueName ?: "",
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(LeagueDetailsUiAction.BackClicked) })
		},
		actions = {
			Box {
				IconButton(onClick = { onAction(LeagueDetailsUiAction.SortClicked) }) {
					Icon(
						painter = painterResource(R.drawable.ic_sort),
						contentDescription = stringResource(R.string.series_list_sort_order),
						tint = MaterialTheme.colorScheme.onSurface,
					)
				}

				DropdownMenu(
					expanded = state.isSortOrderMenuExpanded,
					onDismissRequest = { onAction(LeagueDetailsUiAction.SortDismissed) },
				) {
					SeriesSortOrder.values().forEach { order ->
						DropdownMenuItem(
							text = {
								Text(
									text = when (order) {
										SeriesSortOrder.NEWEST_TO_OLDEST -> stringResource(R.string.series_list_sort_order_newest_to_oldest)
										SeriesSortOrder.OLDEST_TO_NEWEST -> stringResource(R.string.series_list_sort_order_oldest_to_newest)
										SeriesSortOrder.HIGHEST_TO_LOWEST -> stringResource(R.string.series_list_sort_order_highest_to_lowest)
										SeriesSortOrder.LOWEST_TO_HIGHEST -> stringResource(R.string.series_list_sort_order_lowest_to_highest)
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
							onClick = { onAction(LeagueDetailsUiAction.SortOrderClicked(order)) },
						)
					}
				}
			}

			IconButton(onClick = { onAction(LeagueDetailsUiAction.AddSeriesClicked) }) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.series_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	)
}