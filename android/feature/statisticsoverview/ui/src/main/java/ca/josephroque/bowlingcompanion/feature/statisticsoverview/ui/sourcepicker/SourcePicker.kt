package ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.format
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.PickableResourceCard
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.R

@Composable
fun SourcePicker(
	state: SourcePickerUiState,
	onAction: (SourcePickerUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.padding(bottom = 16.dp),
	) {
		PickableResourceCard(
			resourceName = stringResource(R.string.statistics_filter_bowler),
			selectedName = state.source?.bowler?.name ?: stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.all),
			onClick = { onAction(SourcePickerUiAction.BowlerClicked) },
		)

		PickableResourceCard(
			resourceName = stringResource(R.string.statistics_filter_league),
			selectedName = state.source?.league?.name ?: stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.all),
			onClick = { onAction(SourcePickerUiAction.LeagueClicked) },
			enabled = state.source?.bowler != null,
		)

		PickableResourceCard(
			resourceName = stringResource(R.string.statistics_filter_series),
			selectedName = state.source?.series?.date?.format("yyyy-MM-dd")
				?: stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.all),
			onClick = { onAction(SourcePickerUiAction.SeriesClicked) },
			enabled = state.source?.league != null,
		)

		val gameOrdinal = state.source?.game?.index?.plus(1)
		PickableResourceCard(
			resourceName = stringResource(R.string.statistics_filter_game),
			selectedName = if (gameOrdinal == null) {
				stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.all)
			} else {
				stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.game_with_ordinal, gameOrdinal)
			},
			onClick = { onAction(SourcePickerUiAction.GameClicked) },
			enabled = state.source?.series != null,
			modifier = Modifier.padding(bottom = 16.dp),
		)
	}
}