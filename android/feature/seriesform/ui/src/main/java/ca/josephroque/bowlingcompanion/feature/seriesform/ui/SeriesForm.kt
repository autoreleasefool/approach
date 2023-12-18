package ca.josephroque.bowlingcompanion.feature.seriesform.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormRadioGroup
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSection
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.PickableResourceCard
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.Stepper
import ca.josephroque.bowlingcompanion.core.model.AlleyDetails
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl

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

	Column(
		modifier = modifier
			.verticalScroll(rememberScrollState())
			.fillMaxSize(),
	) {
		DetailsSection(
			numberOfGames = state.numberOfGames,
			onNumberOfGamesChanged = { onAction(SeriesFormUiAction.NumberOfGamesChanged(it)) },
			modifier = Modifier.padding(bottom = 16.dp),
		)

		Divider()

		AlleySection(
			alley = state.alley,
			onClick = { onAction(SeriesFormUiAction.AlleyClicked) },
			modifier = Modifier.padding(16.dp),
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
			onExcludeFromStatisticsChanged = { onAction(SeriesFormUiAction.ExcludeFromStatisticsChanged(it)) },
			modifier = Modifier.padding(top = 16.dp),
		)
	}
}

@Composable
private fun DetailsSection(
	numberOfGames: Int?,
	onNumberOfGamesChanged: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	FormSection(
		titleResourceId = R.string.series_form_section_details,
		modifier = modifier,
	) {
		numberOfGames?.let {
			Stepper(
				title = stringResource(R.string.series_form_number_of_games),
				value = it,
				onValueChanged = onNumberOfGamesChanged,
			)
		}

		// TODO: date picker
	}
}

@Composable
private fun AlleySection(
	alley: AlleyDetails?,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	FormSection(
		modifier = modifier,
	) {
		PickableResourceCard(
			resourceName = stringResource(R.string.series_form_bowling_alley),
			selectedName = alley?.name ?: stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.none),
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
			subtitleResourceId = when {
				leagueExcludeFromStatistics == ExcludeFromStatistics.EXCLUDE -> R.string.series_form_section_statistics_description_excluded_when_league_excluded
				seriesPreBowl == SeriesPreBowl.PRE_BOWL -> R.string.series_form_section_statistics_description_excluded_when_pre_bowl
				else -> R.string.series_form_section_statistics_description
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