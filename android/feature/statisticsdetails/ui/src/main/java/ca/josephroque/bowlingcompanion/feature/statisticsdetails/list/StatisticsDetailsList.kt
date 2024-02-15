package ca.josephroque.bowlingcompanion.feature.statisticsdetails.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.designsystem.components.LabeledSwitch
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.MutedEmptyState
import ca.josephroque.bowlingcompanion.core.designsystem.theme.ApproachTheme
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.GameSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.SeriesSummary
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.stub.BowlerSummaryStub
import ca.josephroque.bowlingcompanion.core.model.stub.LeagueSummaryStub
import ca.josephroque.bowlingcompanion.core.model.ui.title
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticListEntry
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticListEntryGroup
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.ui.R
import java.util.UUID

private val FILTER_ITEM_WIDTH = 180.dp
private fun Modifier.filterItem() = fillMaxHeight()
	.widthIn(max = FILTER_ITEM_WIDTH)

@Composable
fun StatisticsDetailsList(
	state: StatisticsDetailsListUiState,
	onAction: (StatisticsDetailsListUiAction) -> Unit,
	modifier: Modifier = Modifier,
	listState: LazyListState = rememberLazyListState(),
) {
	LazyColumn(
		state = listState,
		modifier = modifier,
	) {
		if (state.statistics.isEmpty()) {
			item {
				MutedEmptyState(
					title = R.string.statistics_list_empty_title,
					icon = R.drawable.statistics_list_empty_state,
					message = R.string.statistics_list_empty_message,
					modifier = Modifier
						.padding(vertical = 16.dp)
						.onGloballyPositioned {
							onAction(StatisticsDetailsListUiAction.HeaderHeightMeasured(it.size.height.toFloat()))
						},
				)
			}

			if (state.filterSources != null) {
				item {
					FilterDetails(
						filter = state.filter,
						filterSource = state.filterSources,
						modifier = Modifier.padding(bottom = 16.dp),
					)
				}
			}
		} else if (state.isShowingTitle || state.filterSources != null) {
			item {
				Column(
					modifier = Modifier
						.onGloballyPositioned {
							onAction(StatisticsDetailsListUiAction.HeaderHeightMeasured(it.size.height.toFloat()))
						},
				) {
					if (state.isShowingTitle) {
						Text(
							text = stringResource(R.string.statistics_filter_title),
							style = MaterialTheme.typography.titleLarge,
							modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
						)
					}

					if (state.filterSources != null) {
						FilterDetails(
							filter = state.filter,
							filterSource = state.filterSources,
							modifier = Modifier.padding(bottom = 16.dp),
						)
					}
				}
			}

			items(
				items = state.statistics,
				key = { it.title },
			) {
				ListGroup(
					group = it,
					onAction = onAction,
					modifier = Modifier
						.padding(bottom = 16.dp)
						.padding(horizontal = 16.dp),
				)
			}
		}

		item(contentType = "settings") {
			Settings(
				isHidingZeroStatistics = state.isHidingZeroStatistics,
				isHidingStatisticDescriptions = state.isHidingStatisticDescriptions,
				onAction = onAction,
				modifier = Modifier
					.padding(bottom = 16.dp)
					.padding(horizontal = 16.dp),
			)
		}
	}
}

@Composable
private fun FilterDetails(
	filter: TrackableFilter,
	filterSource: TrackableFilter.SourceSummaries,
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.horizontalScroll(rememberScrollState())
			.height(intrinsicSize = IntrinsicSize.Max),
	) {
		Spacer(modifier = Modifier.width(8.dp))

		BowlerFilters(
			bowler = filterSource.bowler,
		)

		LeagueFilters(
			league = filterSource.league,
			filter = filter,
		)

		SeriesFilters(
			series = filterSource.series,
			filter = filter,
		)

		GameFilters(
			game = filterSource.game,
			filter = filter,
		)

		FrameFilters(
			filter = filter,
		)

		Spacer(modifier = Modifier.width(8.dp))
	}
}

@Composable
private fun BowlerFilters(bowler: BowlerSummary) {
	FilterItem(
		label = stringResource(R.string.statistics_filter_label_bowler),
		text = bowler.name,
		modifier = Modifier.filterItem(),
	)
}

@Composable
private fun LeagueFilters(league: LeagueSummary?, filter: TrackableFilter) {
	if (league != null) {
		FilterItem(
			label = stringResource(R.string.statistics_filter_label_league),
			text = league.name,
			modifier = Modifier.filterItem(),
		)
	} else if (filter.leagues.recurrence != null) {
		FilterItem(
			label = stringResource(R.string.statistics_filter_label_league_recurrence),
			text = when (filter.leagues.recurrence!!) {
				LeagueRecurrence.REPEATING -> stringResource(
					R.string.statistics_filter_label_league_recurrence_repeats,
				)
				LeagueRecurrence.ONCE -> stringResource(
					R.string.statistics_filter_label_league_recurrence_never,
				)
			},
			modifier = Modifier.filterItem(),
		)
	}
}

@Composable
private fun SeriesFilters(series: SeriesSummary?, filter: TrackableFilter) {
	if (series != null) {
		FilterItem(
			label = stringResource(R.string.statistics_filter_label_series),
			text = series.date.simpleFormat(),
			modifier = Modifier.filterItem(),
		)
	} else {
		if (filter.series.startDate != null) {
			FilterItem(
				label = stringResource(R.string.statistics_filter_label_series_starts),
				text = filter.series.startDate!!.simpleFormat(),
				modifier = Modifier.filterItem(),
			)
		}

		if (filter.series.endDate != null) {
			FilterItem(
				label = stringResource(R.string.statistics_filter_label_series_ends),
				text = filter.series.endDate!!.simpleFormat(),
				modifier = Modifier.filterItem(),
			)
		}

		when (val alley = filter.series.alleys) {
			is TrackableFilter.AlleyFilter.Alley -> Unit // FIXME: show alley name
			is TrackableFilter.AlleyFilter.Properties -> {
				if (alley.material != null) {
					FilterItem(
						label = stringResource(R.string.statistics_filter_label_series_alley_material),
						text = alley.material?.title() ?: "",
						modifier = Modifier.filterItem(),
					)
				}

				if (alley.mechanism != null) {
					FilterItem(
						label = stringResource(R.string.statistics_filter_label_series_alley_mechanism),
						text = alley.mechanism?.title() ?: "",
						modifier = Modifier.filterItem(),
					)
				}

				if (alley.pinFall != null) {
					FilterItem(
						label = stringResource(R.string.statistics_filter_label_series_alley_pin_fall),
						text = alley.pinFall?.title() ?: "",
						modifier = Modifier.filterItem(),
					)
				}

				if (alley.pinBase != null) {
					FilterItem(
						label = stringResource(R.string.statistics_filter_label_series_alley_pin_base),
						text = alley.pinBase?.title() ?: "",
						modifier = Modifier.filterItem(),
					)
				}
			}
			null -> Unit
		}
	}
}

@Composable
private fun GameFilters(game: GameSummary?, filter: TrackableFilter) {
	if (game != null) {
		FilterItem(
			label = stringResource(R.string.statistics_filter_label_game),
			text = stringResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.string.game_with_ordinal,
				game.index + 1,
			),
			modifier = Modifier.filterItem(),
		)
	} else {
		// FIXME: show lanes
// 		if (filter.games.lanes != null) {
// 		}

		// FIXME: show gear used
// 		if (filter.games.gearUsed.isNotEmpty()) {
// 		}

		// FIXME: show opponent name
// 		if (filter.games.opponent != null) {
// 		}
	}
}

@Composable
private fun FrameFilters(filter: TrackableFilter) {
// 	if (filter.frames.bowlingBallsUsed.isNotEmpty()) {
	// FIXME: Show gear used
// 	}
}

@Composable
private fun FilterItem(label: String, modifier: Modifier = Modifier, text: String? = null) {
	Card(
		modifier = modifier,
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.primaryContainer,
		),
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(2.dp),
			modifier = Modifier.padding(8.dp),
		) {
			Text(
				text = label,
				style = MaterialTheme.typography.labelMedium,
			)

			if (text != null) {
				Text(
					text = text,
					style = MaterialTheme.typography.bodyMedium,
				)
			}

			Spacer(modifier = Modifier.weight(1f))
		}
	}
}

@Composable
private fun ListGroup(
	group: StatisticListEntryGroup,
	onAction: (StatisticsDetailsListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	OutlinedCard(modifier = modifier) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.padding(horizontal = 16.dp, vertical = 8.dp),
		) {
			Column(
				modifier = Modifier.weight(1f),
			) {
				Text(
					text = stringResource(group.title),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
				)

				val description = group.description
				if (description != null) {
					Text(
						text = stringResource(description),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontStyle = FontStyle.Italic,
					)
				}
			}

			if (group.entries.any { it.isHighlightedAsNew }) {
				Surface(
					color = MaterialTheme.colorScheme.primaryContainer,
					shape = MaterialTheme.shapes.small,
					modifier = Modifier.padding(top = 8.dp),
				) {
					Text(
						text = stringResource(R.string.statistics_list_new).uppercase(),
						style = MaterialTheme.typography.labelSmall,
						modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
					)
				}
			}
		}

		Column(
			modifier = Modifier
				.padding(bottom = 8.dp),
		) {
			group.entries.forEach {
				ListEntry(
					entry = it,
					onAction = onAction,
				)
			}
		}
	}
}

@Composable
private fun ListEntry(
	entry: StatisticListEntry,
	onAction: (StatisticsDetailsListUiAction) -> Unit,
) {
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = { onAction(StatisticsDetailsListUiAction.StatisticClicked(entry.id)) })
			.padding(horizontal = 16.dp, vertical = 8.dp),
	) {
		Text(
			text = stringResource(entry.id.titleResourceId),
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface,
		)

		Column(
			horizontalAlignment = Alignment.End,
			verticalArrangement = Arrangement.spacedBy(2.dp),
		) {
			Text(
				text = entry.value,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface,
			)

			val valueDescription = entry.valueDescription
			if (valueDescription != null) {
				Text(
					text = valueDescription,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					fontStyle = FontStyle.Italic,
				)
			}
		}
	}
}

@Composable
private fun Settings(
	isHidingZeroStatistics: Boolean,
	isHidingStatisticDescriptions: Boolean,
	onAction: (StatisticsDetailsListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(modifier = modifier) {
		Text(
			text = stringResource(R.string.statistics_list_settings),
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier
				.padding(horizontal = 16.dp, vertical = 8.dp),
		)

		LabeledSwitch(
			checked = isHidingZeroStatistics,
			onCheckedChange = { onAction(StatisticsDetailsListUiAction.HidingZeroStatisticsToggled(it)) },
			titleResourceId = R.string.statistics_list_setting_hide_zero,
			compact = true,
		)

		LabeledSwitch(
			checked = isHidingStatisticDescriptions,
			onCheckedChange = {
				onAction(StatisticsDetailsListUiAction.HidingStatisticDescriptionsToggled(it))
			},
			titleResourceId = R.string.statistics_list_setting_hide_descriptions,
			compact = true,
		)
	}
}

private class StatisticsListEntryGroupProvider :
	PreviewParameterProvider<List<StatisticListEntryGroup>> {

	override val values: Sequence<List<StatisticListEntryGroup>> = sequenceOf(
		emptyList(),
		listOf(
			StatisticListEntryGroup(
				title = ca.josephroque.bowlingcompanion.core.statistics.R.string.statistic_category_fives,
				description = null,
				images = null,
				entries = listOf(
					StatisticListEntry(
						id = StatisticID.FIVES,
						value = "0",
						description = null,
						valueDescription = null,
						isHighlightedAsNew = false,
					),
					StatisticListEntry(
						id = StatisticID.FIVES_SPARED,
						value = "25",
						description = null,
						valueDescription = "(1/4)",
						isHighlightedAsNew = true,
					),
				),
			),
			StatisticListEntryGroup(
				title = ca.josephroque.bowlingcompanion.core.statistics.R.string.statistic_category_aces,
				description = ca.josephroque.bowlingcompanion.core.statistics.R.string
					.statistic_category_aces_description,
				images = listOf(R.drawable.ic_aces),
				entries = listOf(
					StatisticListEntry(
						id = StatisticID.ACES,
						value = "0",
						valueDescription = null,
						description = null,
						isHighlightedAsNew = true,
					),
					StatisticListEntry(
						id = StatisticID.ACES_SPARED,
						value = "25",
						description = null,
						valueDescription = "(1/4)",
						isHighlightedAsNew = false,
					),
				),
			),
			StatisticListEntryGroup(
				title = ca.josephroque.bowlingcompanion.core.statistics.R.string.statistic_category_chops,
				description = ca.josephroque.bowlingcompanion.core.statistics.R.string
					.statistic_category_chops_descriptions,
				images = null,
				entries = listOf(
					StatisticListEntry(
						id = StatisticID.CHOPS,
						value = "0",
						description = null,
						valueDescription = null,
						isHighlightedAsNew = false,
					),
					StatisticListEntry(
						id = StatisticID.CHOPS_SPARED,
						value = "25%",
						valueDescription = "(1/4)",
						description = null,
						isHighlightedAsNew = false,
					),
				),
			),
		),
	)
}

@Preview
@Composable
private fun StatisticsDetailsListPreview(
	@Suppress("ktlint:standard:max-line-length")
	@PreviewParameter(StatisticsListEntryGroupProvider::class)
	statistics: List<StatisticListEntryGroup>,
) {
	ApproachTheme {
		Surface {
			StatisticsDetailsList(
				state = StatisticsDetailsListUiState(
					filter = TrackableFilter(source = TrackableFilter.Source.Bowler(UUID.randomUUID())),
					filterSources = TrackableFilter.SourceSummaries(
						bowler = BowlerSummaryStub.single(),
						league = LeagueSummaryStub.single(),
						series = null,
						game = null,
					),
					statistics = statistics,
				),
				onAction = {},
			)
		}
	}
}
