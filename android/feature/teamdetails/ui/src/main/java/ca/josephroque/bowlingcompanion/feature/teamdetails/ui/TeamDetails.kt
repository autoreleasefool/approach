package ca.josephroque.bowlingcompanion.feature.teamdetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.ListSectionHeader
import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem
import ca.josephroque.bowlingcompanion.core.model.charts.ui.SeriesChartData
import ca.josephroque.bowlingcompanion.core.model.ui.BowlerRow
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.util.UUID
import kotlinx.datetime.LocalDate

@Composable
fun TeamDetails(
	state: TeamDetailsUiState,
	@Suppress("UNUSED_PARAMETER") onAction: (TeamDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyColumn(
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

		itemsIndexed(
			items = state.series,
			key = { _, series -> series.id },
		) { index, series ->
			TeamSeriesRow(
				date = series.date,
				total = series.total,
				teamChart = SeriesChartData(
					numberOfGames = series.numberOfGames,
					scoreRange = series.scoreRange,
					model = series.chart,
				),
				memberCharts = series.members.map {
					TeamMemberSeriesChartData(
						name = it.name,
						chart = SeriesChartData(
							numberOfGames = series.numberOfGames,
							scoreRange = it.scoreRange,
							model = it.chart,
						),
					)
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
			id = UUID.randomUUID(),
			name = "John Doe",
		),
		TeamMemberListItem(
			id = UUID.randomUUID(),
			name = "Jane Doe",
		),
	)

	Surface {
		TeamDetails(
			state = TeamDetailsUiState(
				members = teamMembers,
				series = listOf(
					TeamSeriesListChartItem(
						id = UUID.randomUUID(),
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
								id = UUID.randomUUID(),
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
								id = UUID.randomUUID(),
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
					TeamSeriesListChartItem(
						id = UUID.randomUUID(),
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
								id = UUID.randomUUID(),
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
								id = UUID.randomUUID(),
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
			onAction = {},
		)
	}
}
