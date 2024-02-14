package ca.josephroque.bowlingcompanion.feature.seriesform.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.DiscardChangesDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormRadioGroup
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSection
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.PickableResourceCard
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.Stepper
import ca.josephroque.bowlingcompanion.core.model.AlleyDetails
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import java.util.UUID
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

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

		Divider()

		AlleySection(
			alley = state.alley,
			onClick = { onAction(SeriesFormUiAction.AlleyClicked) },
		)

		Divider()

		PreBowlSection(
			preBowl = state.preBowl,
			onPreBowlChanged = { onAction(SeriesFormUiAction.PreBowlChanged(it)) },
			modifier = Modifier.padding(top = 16.dp),
		)

		Divider()

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

		Divider()

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeriesDatePicker(
	currentDate: LocalDate,
	isDatePickerVisible: Boolean,
	onDateChanged: (LocalDate) -> Unit,
	onDateClicked: () -> Unit,
	onDatePickerDismissed: () -> Unit,
) {
	val initialSelection = remember(currentDate) {
		currentDate
			.atStartOfDayIn(TimeZone.currentSystemDefault())
			.toEpochMilliseconds()
	}

	val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialSelection)

	val onDismiss = {
		datePickerState.setSelection(initialSelection)
		onDatePickerDismissed()
	}

	if (isDatePickerVisible) {
		DatePickerDialog(
			onDismissRequest = onDismiss,
			confirmButton = {
				TextButton(
					onClick = {
						val currentSelectedDate = datePickerState.selectedDateMillis ?: return@TextButton
						onDateChanged(
							Instant.fromEpochMilliseconds(currentSelectedDate)
								.toLocalDateTime(TimeZone.UTC)
								.date,
						)
					},
				) {
					Text(stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_confirm))
				}
			},
			dismissButton = {
				TextButton(onClick = onDismiss) {
					Text(stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_cancel))
				}
			},
		) {
			DatePicker(state = datePickerState)
		}
	}

	OutlinedTextField(
		label = { Text(stringResource(R.string.series_form_date)) },
		value = currentDate.simpleFormat(),
		onValueChange = {},
		enabled = false,
		leadingIcon = {
			Icon(
				painter = painterResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_event,
				),
				contentDescription = null,
			)
		},
		trailingIcon = {
			Icon(
				Icons.Default.Edit,
				contentDescription = null,
			)
		},
		colors = OutlinedTextFieldDefaults.colors(
			disabledTextColor = MaterialTheme.colorScheme.onSurface,
			disabledBorderColor = MaterialTheme.colorScheme.outline,
			disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
			disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
			disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
			disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
		),
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onDateClicked)
			.padding(horizontal = 16.dp),
	)
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
private fun PreBowlSection(
	preBowl: SeriesPreBowl,
	onPreBowlChanged: (SeriesPreBowl) -> Unit,
	modifier: Modifier = Modifier,
) {
	FormSection(
		modifier = modifier,
	) {
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
				preBowl = SeriesPreBowl.REGULAR,
				excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
				leagueExcludeFromStatistics = ExcludeFromStatistics.EXCLUDE,
				alley = AlleyDetails(
					id = UUID.randomUUID(),
					name = "Test Alley",
					material = null,
					mechanism = null,
					pinBase = null,
					pinFall = null,
				),
				isDatePickerVisible = false,
				isShowingArchiveDialog = false,
				isArchiveButtonEnabled = true,
				isShowingDiscardChangesDialog = false,
			),
			onAction = {},
		)
	}
}
