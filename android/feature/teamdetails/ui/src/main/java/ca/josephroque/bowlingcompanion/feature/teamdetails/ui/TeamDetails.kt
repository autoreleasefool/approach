package ca.josephroque.bowlingcompanion.feature.teamdetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.ListSectionHeader
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.EmptyStateAction
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSummary
import ca.josephroque.bowlingcompanion.core.model.charts.ui.SeriesChartData
import ca.josephroque.bowlingcompanion.core.model.ui.BowlerRow
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.datetime.LocalDate
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun TeamDetails(
	state: TeamDetailsUiState,
	onAction: (TeamDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {
	state.seriesToArchive.seriesToArchive?.let {
		if (state.seriesToArchive.isArchiveMemberSeriesVisible) {
			ArchiveMemberSeriesDialog(
				onArchive = { onAction(TeamDetailsUiAction.ArchiveMemberSeriesClicked) },
				onKeep = { onAction(TeamDetailsUiAction.KeepMemberSeriesClicked) },
				onDismiss = { onAction(TeamDetailsUiAction.DismissArchiveMemberSeriesClicked) },
			)
		} else {
			ArchiveDialog(
				itemName = it.date.simpleFormat(),
				onArchive = { onAction(TeamDetailsUiAction.ConfirmArchiveClicked) },
				onDismiss = { onAction(TeamDetailsUiAction.DismissArchiveClicked) },
			)
		}
	}

	LazyColumn(
		contentPadding = contentPadding,
		modifier = modifier
			.fillMaxSize(),
	) {
		items(
			items = state.members,
			key = { it.id },
		) { member ->
			MemberRow(name = member.name)
		}
		
		item {
			HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

			ListSectionHeader(
				titleResourceId = R.string.team_details_recent_series,
			)
		}

		if (state.series.isEmpty()) {
			item {
				DefaultEmptyState(
					title = R.string.team_details_series_empty_title,
					icon = ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.series_list_empty_state,
					message = R.string.team_details_series_empty_message,
					action = EmptyStateAction(
						title = R.string.team_details_add_series,
						onClick = { onAction(TeamDetailsUiAction.AddSeriesClicked) },
					),
				)
			}
		}

		itemsIndexed(
			items = state.series,
			key = { _, series -> series.id },
		) { index, series ->
			LaunchedEffect(Unit) {
				onAction(TeamDetailsUiAction.SeriesAppeared(series.id))
			}

			val archiveAction = SwipeAction(
				icon = painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_archive),
				background = colorResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
				),
				onSwipe = { onAction(TeamDetailsUiAction.ArchiveSeriesClicked(series)) },
			)

			val editAction = SwipeAction(
				icon = rememberVectorPainter(Icons.Default.Edit),
				background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.blue_300),
				onSwipe = { onAction(TeamDetailsUiAction.EditSeriesClicked(series)) },
			)

			SwipeableActionsBox(
				startActions = listOf(archiveAction),
				endActions = listOf(editAction),
			) {
				TeamSeriesRow(series, state.seriesItemSize)
			}

			if (index < state.series.size - 1) {
				HorizontalDivider()
			}
		}
	}
}

@Composable
private fun TeamSeriesRow(series: TeamSeriesListItem, itemSize: SeriesItemSize) {
	TeamSeriesRow(
		date = series.date,
		total = series.total,
		itemSize = itemSize,
		teamChart = when (series) {
			is TeamSeriesListItem.Summary -> null
			is TeamSeriesListItem.Chart -> SeriesChartData(
				numberOfGames = series.item.numberOfGames,
				scoreRange = series.item.scoreRange,
				model = series.item.chart,
			)
		},
		memberCharts = when (series) {
			is TeamSeriesListItem.Summary -> null
			is TeamSeriesListItem.Chart ->
				series.item.members.map {
					TeamMemberSeriesChartData(
						name = it.name,
						chart = SeriesChartData(
							numberOfGames = series.item.numberOfGames,
							scoreRange = it.scoreRange,
							model = it.chart,
						),
					)
				}
		},
	)
}

@Composable
private fun MemberRow(name: String, modifier: Modifier = Modifier) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.background(MaterialTheme.colorScheme.surface)
			.fillMaxWidth()
			.padding(16.dp),
	) {
		BowlerRow(name = name, modifier = Modifier.weight(1f))
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArchiveMemberSeriesDialog(
	onArchive: () -> Unit,
	onKeep: () -> Unit,
	onDismiss: () -> Unit,
) {
	BasicAlertDialog(
		onDismissRequest = onDismiss,
	) {
		Surface(
			shape = RoundedCornerShape(corner = CornerSize(16.dp)),
			color = AlertDialogDefaults.containerColor,
		) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					text = stringResource(R.string.team_series_archive_member_series_title),
					style = MaterialTheme.typography.titleLarge,
					modifier = Modifier.padding(bottom = 16.dp),
				)

				Text(
					text = stringResource(R.string.team_series_archive_member_series_message),
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier.padding(bottom = 16.dp),
				)

				Column(
					horizontalAlignment = Alignment.End,
					modifier = Modifier.fillMaxWidth(),
				) {
					TextButton(onClick = onArchive) {
						Text(
							text = stringResource(R.string.team_series_archive_member_series_archive),
							color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive),
						)
					}

					TextButton(onClick = onKeep) {
						Text(
							text = stringResource(R.string.team_series_archive_member_series_keep),
						)
					}

					TextButton(onClick = onDismiss) {
						Text(
							text = stringResource(R.string.team_series_archive_member_series_dismiss),
						)
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun ArchiveDialogPreview() {
	Surface {
		ArchiveMemberSeriesDialog(
			onArchive = {},
			onDismiss = {},
			onKeep = {},
		)
	}
}

@Preview
@Composable
private fun TeamDetailsPreview() {
	val teamMembers = listOf(
		TeamMemberListItem(
			id = BowlerID.randomID(),
			name = "John Doe",
		),
		TeamMemberListItem(
			id = BowlerID.randomID(),
			name = "Jane Doe",
		),
	)

	Surface {
		TeamDetails(
			state = TeamDetailsUiState(
				seriesItemSize = SeriesItemSize.DEFAULT,
				seriesToArchive = ArchiveSeriesUiState(
					seriesToArchive = null,
					isArchiveMemberSeriesVisible = false,
				),
				members = teamMembers,
				series = listOf(
					TeamSeriesListItem.Summary(
						TeamSeriesSummary(
							id = TeamSeriesID.randomID(),
							date = LocalDate.parse("2023-01-01"),
							total = 1760,
						),
					),
					TeamSeriesListItem.Chart(
						TeamSeriesListChartItem(
							id = TeamSeriesID.randomID(),
							date = LocalDate.parse("2023-01-01"),
							total = 1760,
							numberOfGames = 4,
							scoreRange = 430..460,
							chart = ChartEntryModelProducer(
								listOf(
									entryOf(0, 440),
									entryOf(1, 460),
									entryOf(2, 430),
									entryOf(3, 450),
								),
							),
							members = listOf(
								TeamMemberSeriesListChartItem(
									id = BowlerID.randomID(),
									name = "John",
									scoreRange = 430..460,
									chart = ChartEntryModelProducer(
										listOf(
											entryOf(0, 440),
											entryOf(1, 460),
											entryOf(2, 430),
											entryOf(3, 450),
										),
									),
								),
								TeamMemberSeriesListChartItem(
									id = BowlerID.randomID(),
									name = "Jane Doe",
									scoreRange = 430..460,
									chart = ChartEntryModelProducer(
										listOf(
											entryOf(0, 440),
											entryOf(1, 460),
											entryOf(2, 430),
											entryOf(3, 450),
										),
									),
								),
							),
						),
					),
					TeamSeriesListItem.Chart(
						TeamSeriesListChartItem(
							id = TeamSeriesID.randomID(),
							date = LocalDate.parse("2023-01-01"),
							total = 1760,
							numberOfGames = 4,
							scoreRange = 430..460,
							chart = ChartEntryModelProducer(
								listOf(
									entryOf(0, 440),
									entryOf(1, 460),
									entryOf(2, 430),
									entryOf(3, 450),
								),
							),
							members = listOf(
								TeamMemberSeriesListChartItem(
									id = BowlerID.randomID(),
									name = "John Doe 1234",
									scoreRange = 100..460,
									chart = ChartEntryModelProducer(
										listOf(
											entryOf(0, 440),
											entryOf(1, 460),
											entryOf(2, 430),
											entryOf(3, 100),
										),
									),
								),
								TeamMemberSeriesListChartItem(
									id = BowlerID.randomID(),
									name = "Jane Doe",
									scoreRange = 430..460,
									chart = ChartEntryModelProducer(
										listOf(
											entryOf(0, 440),
											entryOf(1, 460),
											entryOf(2, 430),
											entryOf(3, 450),
										),
									),
								),
							),
						),
					),
				),
			),
			onAction = {},
		)
	}
}
