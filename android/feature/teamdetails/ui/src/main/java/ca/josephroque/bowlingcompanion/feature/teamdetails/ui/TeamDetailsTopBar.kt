package ca.josephroque.bowlingcompanion.feature.teamdetails.ui

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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.ui.contentDescription
import ca.josephroque.bowlingcompanion.core.model.ui.icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsTopBar(
	state: TeamDetailsTopBarUiState,
	onAction: (TeamDetailsTopBarUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	MediumTopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = state.teamName ?: "",
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(TeamDetailsTopBarUiAction.BackClicked) })
		},
		actions = {
			if (state.isSeriesItemSizeVisible) {
				IconButton(onClick = {
					onAction(TeamDetailsTopBarUiAction.SeriesItemSizeToggled(state.seriesItemSize.next))
				}) {
					Icon(
						painter = state.seriesItemSize.icon(),
						contentDescription = state.seriesItemSize.contentDescription(),
						tint = MaterialTheme.colorScheme.onSurface,
					)
				}

				if (state.isSortOrderMenuVisible) {
					Box {
						IconButton(onClick = { onAction(TeamDetailsTopBarUiAction.SortClicked) }) {
							Icon(
								painter = painterResource(
									ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_sort,
								),
								contentDescription = stringResource(R.string.team_series_list_sort_order),
								tint = MaterialTheme.colorScheme.onSurface,
							)
						}

						DropdownMenu(
							expanded = state.isSortOrderMenuExpanded,
							onDismissRequest = { onAction(TeamDetailsTopBarUiAction.SortDismissed) },
						) {
							TeamSeriesSortOrder.entries.forEach { order ->
								DropdownMenuItem(
									text = {
										Text(
											text = when (order) {
												TeamSeriesSortOrder.NEWEST_TO_OLDEST -> stringResource(
													R.string.team_series_list_sort_order_newest_to_oldest,
												)
												TeamSeriesSortOrder.OLDEST_TO_NEWEST -> stringResource(
													R.string.team_series_list_sort_order_oldest_to_newest,
												)
												TeamSeriesSortOrder.HIGHEST_TO_LOWEST -> stringResource(
													R.string.team_series_list_sort_order_highest_to_lowest,
												)
												TeamSeriesSortOrder.LOWEST_TO_HIGHEST -> stringResource(
													R.string.team_series_list_sort_order_lowest_to_highest,
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
									onClick = { onAction(TeamDetailsTopBarUiAction.SortOrderClicked(order)) },
								)
							}
						}
					}
				}
			}

			IconButton(onClick = { onAction(TeamDetailsTopBarUiAction.AddSeriesClicked) }) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.team_series_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	)
}
