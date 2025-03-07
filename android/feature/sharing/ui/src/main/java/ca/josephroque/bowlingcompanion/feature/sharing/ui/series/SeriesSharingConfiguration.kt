package ca.josephroque.bowlingcompanion.feature.sharing.ui.series

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.SectionHeader
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.Stepper
import ca.josephroque.bowlingcompanion.feature.sharing.ui.R
import ca.josephroque.bowlingcompanion.feature.sharing.ui.components.AppearanceSegmentedButton
import ca.josephroque.bowlingcompanion.feature.sharing.ui.components.FilterItem

@Composable
fun SeriesSharingConfiguration(
	state: SeriesSharingConfigurationUiState,
	onAction: (SeriesSharingConfigurationUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
	) {
		Column(
			modifier = Modifier
				.verticalScroll(rememberScrollState()),
		) {

			OverlaySection(
				state = state,
				onAction = onAction,
			)

			ScoresSection(
				state = state,
				onAction = onAction,
			)

			HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

			ScoreRangeSection(
				state = state,
				onAction = onAction,
			)

			HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

			AppearanceSection(
				state = state,
				onAction = onAction,
			)
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OverlaySection(
	state: SeriesSharingConfigurationUiState,
	onAction: (SeriesSharingConfigurationUiAction) -> Unit,
) {
	SectionHeader(title = stringResource(R.string.sharing_series_modifier_section_overlay))

	FlowRow(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = Modifier.padding(horizontal = 16.dp),
	) {
		FilterItem(
			selected = state.isDateChecked,
			title = R.string.sharing_series_modifier_date,
			onClick = { onAction(SeriesSharingConfigurationUiAction.IsDateCheckedToggled(!state.isDateChecked)) },
			imageVector = Icons.Default.DateRange,
		)

		FilterItem(
			selected = state.isBowlerChecked,
			title = R.string.sharing_series_modifier_bowler,
			onClick = { onAction(SeriesSharingConfigurationUiAction.IsBowlerCheckedToggled(!state.isBowlerChecked)) },
			imageVector = Icons.Default.Person,
		)

		FilterItem(
			selected = state.isLeagueChecked,
			title = R.string.sharing_series_modifier_league,
			onClick = { onAction(SeriesSharingConfigurationUiAction.IsLeagueCheckedToggled(!state.isLeagueChecked)) },
			imageVector = Icons.Default.Refresh,
		)
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScoresSection(
	state: SeriesSharingConfigurationUiState,
	onAction: (SeriesSharingConfigurationUiAction) -> Unit,
) {
	SectionHeader(title = stringResource(R.string.sharing_series_modifier_section_scores))

	FlowRow(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = Modifier.padding(horizontal = 16.dp),
	) {
		FilterItem(
			selected = state.isSeriesTotalChecked,
			title = R.string.sharing_series_modifier_total,
			onClick = {
				onAction(
					SeriesSharingConfigurationUiAction.IsSeriesTotalCheckedToggled(!state.isSeriesTotalChecked),
				)
			},
			imageVector = Icons.AutoMirrored.Default.List,
		)

		FilterItem(
			selected = state.isHighScoreChecked,
			title = R.string.sharing_series_modifier_high_score,
			onClick = {
				onAction(SeriesSharingConfigurationUiAction.IsHighScoreCheckedToggled(!state.isHighScoreChecked))
			},
			imageVector = Icons.Default.KeyboardArrowUp,
		)

		FilterItem(
			selected = state.isLowScoreChecked,
			title = R.string.sharing_series_modifier_low_score,
			onClick = {
				onAction(SeriesSharingConfigurationUiAction.IsLowScoreCheckedToggled(!state.isLowScoreChecked))
			},
			imageVector = Icons.Default.KeyboardArrowDown,
		)
	}
}

@Composable
private fun ScoreRangeSection(
	state: SeriesSharingConfigurationUiState,
	onAction: (SeriesSharingConfigurationUiAction) -> Unit,
) {
	SectionHeader(title = stringResource(R.string.sharing_series_chart_range))

	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Stepper(
			title = stringResource(R.string.sharing_series_modifier_range_lowest_score),
			value = state.chartRange.first,
			range = state.chartLowerBoundRange,
			onValueChanged = { onAction(SeriesSharingConfigurationUiAction.ChartRangeMinimumChanged(it)) },
			step = 5,
			modifier = Modifier.padding(bottom = 8.dp),
		)

		Stepper(
			title = stringResource(R.string.sharing_series_modifier_range_highest_score),
			value = state.chartRange.last,
			range = state.chartUpperBoundRange,
			onValueChanged = { onAction(SeriesSharingConfigurationUiAction.ChartRangeMaximumChanged(it)) },
			step = 5,
			modifier = Modifier.padding(bottom = 8.dp),
		)
	}
}

@Composable
private fun AppearanceSection(
	state: SeriesSharingConfigurationUiState,
	onAction: (SeriesSharingConfigurationUiAction) -> Unit,
) {
	SectionHeader(title = stringResource(R.string.sharing_appearance))

	AppearanceSegmentedButton(
		selected = state.appearance,
		onAppearanceChanged = { onAction(SeriesSharingConfigurationUiAction.AppearanceChanged(it)) },
	)
}

@Preview
@Composable
private fun SeriesSharingPreview() {
	Surface {
		SeriesSharingConfiguration(
			state = SeriesSharingConfigurationUiState(),
			onAction = {},
		)
	}
}
