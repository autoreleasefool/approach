package ca.josephroque.bowlingcompanion.feature.teamdetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Composable
fun TeamDetails(
	state: TeamDetailsUiState,
	onAction: (TeamDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {
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
			
			TeamSeriesRow(
				date = series.date,
				total = series.total,
				itemSize = state.seriesItemSize,
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

			if (index < state.series.size - 1) {
				HorizontalDivider()
			}
		}
	}
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
