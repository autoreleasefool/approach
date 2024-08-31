package ca.josephroque.bowlingcompanion.feature.seriesform.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.DiscardChangesDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormRadioGroup
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSection
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSwitch
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.PickableResourceCard
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.Stepper
import ca.josephroque.bowlingcompanion.core.model.AlleyDetails
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.components.SeriesDatePicker
import kotlinx.datetime.LocalDate

@Composable
fun SeriesForm(
	state: SeriesFormUiState,
	onAction: (SeriesFormUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (state.isShowingArchiveDialog) {
		ArchiveDialog(
			itemName = state.date.simpleFormat(),
			onArchive = { onAction(SeriesFormUiAction.ConfirmArchiveClicked) },
			onDismiss = { onAction(SeriesFormUiAction.DismissArchiveClicked) },
		)
	}

	if (state.isShowingDiscardChangesDialog) {
		DiscardChangesDialog(
			onDiscardChanges = { onAction(SeriesFormUiAction.DiscardChangesClicked) },
			onDismiss = { onAction(SeriesFormUiAction.CancelDiscardChangesClicked) },
		)
	}

	Column(
		modifier = modifier
			.verticalScroll(rememberScrollState())
			.imePadding(),
	) {
		DetailsSection(
			numberOfGames = state.numberOfGames,
			onNumberOfGamesChanged = { onAction(SeriesFormUiAction.NumberOfGamesChanged(it)) },
			isDatePickerVisible = state.isDatePickerVisible,
			currentDate = state.date,
			onDateClicked = { onAction(SeriesFormUiAction.DateClicked) },
			onDatePickerDismissed = { onAction(SeriesFormUiAction.DatePickerDismissed) },
			onDateChanged = { onAction(SeriesFormUiAction.DateChanged(it)) },
			modifier = Modifier.padding(bottom = 16.dp),
		)

		HorizontalDivider()

		AlleySection(
			alley = state.alley,
			onClick = { onAction(SeriesFormUiAction.AlleyClicked) },
		)

		HorizontalDivider()

		if (state.isManualSeriesEnabled && state.isCreatingManualSeries != null) {
			ManualSeriesSection(
				isCreatingManualSeries = state.isCreatingManualSeries,
				manualScores = state.manualScores,
				onManualScoreChanged = { index, score ->
					onAction(SeriesFormUiAction.ManualScoreChanged(index, score))
				},
				onIsCreatingManualSeriesChanged = {
					onAction(SeriesFormUiAction.IsCreatingManualSeriesChanged(it))
				},
				modifier = Modifier.padding(top = 16.dp),
			)
		}

		HorizontalDivider()

		if (state.isPreBowlSectionVisible) {
			PreBowlSection(
				preBowl = state.preBowl,
				isPreBowlFormEnabled = state.isPreBowlFormEnabled,
				onPreBowlChanged = { onAction(SeriesFormUiAction.PreBowlChanged(it)) },
				isAppliedDatePickerVisible = state.isAppliedDatePickerVisible,
				appliedDate = state.appliedDate,
				onAppliedDateClicked = { onAction(SeriesFormUiAction.AppliedDateClicked) },
				onAppliedDatePickerDismissed = { onAction(SeriesFormUiAction.AppliedDatePickerDismissed) },
				onAppliedDateChanged = { onAction(SeriesFormUiAction.AppliedDateChanged(it)) },
				isUsingPreBowl = state.isUsingPreBowl,
				onIsUsingPreBowlChanged = { onAction(SeriesFormUiAction.IsUsingPreBowlChanged(it)) },
				modifier = Modifier.padding(top = 16.dp),
			)

			HorizontalDivider()
		}

		ExcludeFromStatisticsSection(
			excludeFromStatistics = state.excludeFromStatistics,
			leagueExcludeFromStatistics = state.leagueExcludeFromStatistics,
			seriesPreBowl = state.preBowl,
			onExcludeFromStatisticsChanged = {
				onAction(
					SeriesFormUiAction.ExcludeFromStatisticsChanged(it),
				)
			},
			modifier = Modifier.padding(top = 16.dp),
		)

		HorizontalDivider()

		if (state.isArchiveButtonEnabled) {
			Button(
				onClick = { onAction(SeriesFormUiAction.ArchiveClicked) },
				colors = ButtonDefaults.buttonColors(
					containerColor = colorResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
					),
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp),
			) {
				Text(text = stringResource(R.string.series_form_archive))
			}
		}
	}
}

@Composable
private fun DetailsSection(
	numberOfGames: Int?,
	onNumberOfGamesChanged: (Int) -> Unit,
	currentDate: LocalDate,
	isDatePickerVisible: Boolean,
	onDateChanged: (LocalDate) -> Unit,
	onDateClicked: () -> Unit,
	onDatePickerDismissed: () -> Unit,
	modifier: Modifier = Modifier,
) {
	FormSection(
		titleResourceId = R.string.series_form_section_details,
		modifier = modifier,
	) {
		SeriesDatePicker(
			label = R.string.series_form_date,
			currentDate = currentDate,
			isDatePickerVisible = isDatePickerVisible,
			onDateChanged = onDateChanged,
			onDateClicked = onDateClicked,
			onDatePickerDismissed = onDatePickerDismissed,
		)

		numberOfGames?.let {
			Stepper(
				title = stringResource(R.string.series_form_number_of_games),
				value = it,
				onValueChanged = onNumberOfGamesChanged,
				modifier = Modifier.padding(top = 16.dp),
			)
		}
	}
}

@Composable
private fun AlleySection(alley: AlleyDetails?, onClick: () -> Unit) {
	FormSection {
		PickableResourceCard(
			resourceName = stringResource(R.string.series_form_bowling_alley),
			selectedName = alley?.name ?: stringResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.string.none,
			),
			onClick = onClick,
		)
	}
}

@Composable
private fun ManualSeriesSection(
	isCreatingManualSeries: Boolean,
	manualScores: List<Int>,
	onManualScoreChanged: (Int, String) -> Unit,
	onIsCreatingManualSeriesChanged: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	FormSection(
		titleResourceId = R.string.series_form_manual_series,
		footerResourceId = R.string.series_form_manual_series_description,
		modifier = modifier,
	) {
		FormSwitch(
			titleResourceId = R.string.series_form_manual_series_manual,
			isChecked = isCreatingManualSeries,
			onCheckChanged = onIsCreatingManualSeriesChanged,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
		)

		if (isCreatingManualSeries) {
			manualScores.forEachIndexed { index, score ->
				OutlinedTextField(
					value = score.toString(),
					onValueChange = { onManualScoreChanged(index, it) },
					singleLine = true,
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					label = {
						Text(
							stringResource(R.string.series_form_manual_series_game_score, index + 1),
							style = MaterialTheme.typography.bodyMedium,
						)
					},
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp)
						.padding(bottom = if (index == manualScores.size - 1) 0.dp else 16.dp),
				)
			}
		}
	}
}

@Composable
private fun PreBowlSection(
	preBowl: SeriesPreBowl,
	isPreBowlFormEnabled: Boolean,
	onPreBowlChanged: (SeriesPreBowl) -> Unit,
	isUsingPreBowl: Boolean,
	onIsUsingPreBowlChanged: (Boolean) -> Unit,
	appliedDate: LocalDate,
	isAppliedDatePickerVisible: Boolean,
	onAppliedDateChanged: (LocalDate) -> Unit,
	onAppliedDateClicked: () -> Unit,
	onAppliedDatePickerDismissed: () -> Unit,
	modifier: Modifier = Modifier,
) {
	FormSection(modifier = modifier) {
		FormRadioGroup(
			titleResourceId = R.string.series_form_pre_bowl,
			subtitleResourceId = R.string.series_form_section_pre_bowl_description,
			options = SeriesPreBowl.entries.toTypedArray(),
			selected = preBowl,
			titleForOption = {
				when (it) {
					SeriesPreBowl.REGULAR -> stringResource(R.string.series_form_pre_bowl_no)
					SeriesPreBowl.PRE_BOWL -> stringResource(R.string.series_form_pre_bowl_yes)
					null -> ""
				}
			},
			onOptionSelected = {
				onPreBowlChanged(it ?: SeriesPreBowl.REGULAR)
			},
		)

		if (preBowl == SeriesPreBowl.PRE_BOWL && isPreBowlFormEnabled) {
			FormSwitch(
				titleResourceId = R.string.series_form_pre_bowl_use_on_date,
				isChecked = isUsingPreBowl,
				onCheckChanged = onIsUsingPreBowlChanged,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp),
			)

			if (isUsingPreBowl) {
				SeriesDatePicker(
					label = R.string.series_form_pre_bowl_date_to_apply,
					currentDate = appliedDate,
					isDatePickerVisible = isAppliedDatePickerVisible,
					onDateChanged = onAppliedDateChanged,
					onDateClicked = onAppliedDateClicked,
					onDatePickerDismissed = onAppliedDatePickerDismissed,
					modifier = Modifier.padding(bottom = 16.dp),
				)
			}
		}
	}
}

@Composable
private fun ExcludeFromStatisticsSection(
	excludeFromStatistics: ExcludeFromStatistics,
	leagueExcludeFromStatistics: ExcludeFromStatistics,
	seriesPreBowl: SeriesPreBowl,
	onExcludeFromStatisticsChanged: (ExcludeFromStatistics) -> Unit,
	modifier: Modifier = Modifier,
) {
	FormSection(modifier = modifier) {
		FormRadioGroup(
			titleResourceId = R.string.series_form_section_statistics,
			subtitleResourceId = R.string.series_form_section_statistics_description,
			errorResourceId = when {
				leagueExcludeFromStatistics == ExcludeFromStatistics.EXCLUDE ->
					R.string.series_form_section_statistics_description_excluded_when_league_excluded
				seriesPreBowl == SeriesPreBowl.PRE_BOWL ->
					R.string.series_form_section_statistics_description_excluded_when_pre_bowl
				else -> null
			},
			options = ExcludeFromStatistics.entries.toTypedArray(),
			selected = when {
				leagueExcludeFromStatistics == ExcludeFromStatistics.EXCLUDE -> ExcludeFromStatistics.EXCLUDE
				seriesPreBowl == SeriesPreBowl.PRE_BOWL -> ExcludeFromStatistics.EXCLUDE
				else -> excludeFromStatistics
			},
			enabled = when {
				leagueExcludeFromStatistics == ExcludeFromStatistics.EXCLUDE -> false
				seriesPreBowl == SeriesPreBowl.PRE_BOWL -> false
				else -> true
			},
			titleForOption = {
				when (it) {
					ExcludeFromStatistics.INCLUDE -> stringResource(R.string.series_form_exclude_include)
					ExcludeFromStatistics.EXCLUDE -> stringResource(R.string.series_form_exclude_exclude)
					null -> ""
				}
			},
			onOptionSelected = {
				onExcludeFromStatisticsChanged(it ?: ExcludeFromStatistics.INCLUDE)
			},
		)
	}
}

@Preview
@Composable
private fun SeriesFormPreview() {
	Surface {
		SeriesForm(
			state = SeriesFormUiState(
				numberOfGames = 4,
				date = LocalDate(2021, 1, 1),
				appliedDate = LocalDate(2021, 1, 1),
				isUsingPreBowl = true,
				preBowl = SeriesPreBowl.REGULAR,
				excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
				leagueExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
				alley = AlleyDetails(
					id = AlleyID.randomID(),
					name = "Test Alley",
					material = null,
					mechanism = null,
					pinBase = null,
					pinFall = null,
				),
				isDatePickerVisible = false,
				isAppliedDatePickerVisible = false,
				isShowingArchiveDialog = false,
				isArchiveButtonEnabled = true,
				isShowingDiscardChangesDialog = false,
				isPreBowlSectionVisible = false,
				isPreBowlFormEnabled = true,
				isCreatingManualSeries = true,
				manualScores = listOf(100, 200),
				isManualSeriesEnabled = false,
			),
			onAction = {},
		)
	}
}
