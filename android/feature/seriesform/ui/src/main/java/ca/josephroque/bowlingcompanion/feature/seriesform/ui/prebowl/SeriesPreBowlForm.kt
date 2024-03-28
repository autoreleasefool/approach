package ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.format
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSection
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.PickableResourceCard
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.R
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.components.SeriesDatePicker

@Composable
fun SeriesPreBowlForm(
	state: SeriesPreBowlFormUiState,
	onAction: (SeriesPreBowlFormUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.verticalScroll(rememberScrollState()),
	) {
		FormSection {
			DescriptionSection(
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 16.dp),
			)
		}

		HorizontalDivider()

		FormSection {
			PickableResourceCard(
				resourceName = stringResource(R.string.series_pre_bowl_form_series),
				selectedName = state.series?.date?.format("yyyy-MM-dd")
					?: stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.none),
				onClick = { onAction(SeriesPreBowlFormUiAction.SeriesClicked) },
			)
		}

		HorizontalDivider()

		if (state.series != null) {
			FormSection {
				SeriesDatePicker(
					label = R.string.series_pre_bowl_form_date_bowled,
					enabled = false,
					currentDate = state.series.date,
					isDatePickerVisible = false,
					onDateChanged = {},
					onDateClicked = {},
					onDatePickerDismissed = {},
					modifier = Modifier
						.padding(bottom = 8.dp)
						.padding(top = 16.dp),
				)

				SeriesDatePicker(
					label = R.string.series_pre_bowl_form_date_to_apply,
					currentDate = state.appliedDate,
					isDatePickerVisible = state.isAppliedDatePickerVisible,
					onDateChanged = { onAction(SeriesPreBowlFormUiAction.AppliedDateChanged(it)) },
					onDateClicked = { onAction(SeriesPreBowlFormUiAction.AppliedDateClicked) },
					onDatePickerDismissed = { onAction(SeriesPreBowlFormUiAction.AppliedDatePickerDismissed) },
				)
			}
		}
	}
}

@Composable
private fun DescriptionSection(modifier: Modifier = Modifier) {
	Column(
		verticalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier,
	) {
		Text(
			text = stringResource(R.string.series_pre_bowl_form_choose_to_apply),
			style = MaterialTheme.typography.bodyMedium,
		)

		Text(
			text = stringResource(R.string.series_pre_bowl_form_affects_league_average),
			style = MaterialTheme.typography.bodyMedium,
		)

		Text(
			text = stringResource(R.string.series_pre_bowl_form_affects_bowler_average),
			style = MaterialTheme.typography.bodyMedium,
		)
	}
}
