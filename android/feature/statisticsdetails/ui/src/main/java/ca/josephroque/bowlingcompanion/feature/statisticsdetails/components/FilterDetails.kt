package ca.josephroque.bowlingcompanion.feature.statisticsdetails.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.GameSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.SeriesSummary
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.ui.title
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.ui.R

private val FILTER_ITEM_WIDTH = 180.dp
private fun Modifier.filterItem() = fillMaxHeight()
	.widthIn(max = FILTER_ITEM_WIDTH)

@Composable
fun FilterDetails(
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
