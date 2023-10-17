package ca.josephroque.bowlingcompanion.feature.leagueform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.components.BackButton
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueForm

@Composable
internal fun LeagueFormRoute(
	onBackPressed: () -> Unit,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: LeagueFormViewModel = hiltViewModel(),
) {
	val leagueFormState = viewModel.uiState.collectAsState().value

	when (leagueFormState) {
		LeagueFormUiState.Dismissed -> onDismiss()
		else -> Unit
	}

	LeagueFormScreen(
		leagueFormState = leagueFormState,
		loadLeague = viewModel::loadLeague,
		saveLeague = viewModel::saveLeague,
		deleteLeague = viewModel::deleteLeague,
		onNameChanged = viewModel::updateName,
		onRecurrenceChanged = viewModel::updateRecurrence,
		onExcludeFromStatisticsChanged = viewModel::updateExcludeFromStatistics,
		onNumberOfGamesChanged = viewModel::updateNumberOfGames,
		onGamesPerSeriesChanged = viewModel::updateGamesPerSeries,
		onAdditionalGamesChanged = viewModel::updateAdditionalGames,
		onAdditionalPinFallChanged = viewModel::updateAdditionalPinFall,
		onIncludeAdditionalPinFallChanged = viewModel::updateIncludeAdditionalPinFall,
		onBackPressed = onBackPressed,
		modifier = modifier,
	)
}

@Composable
internal fun LeagueFormScreen(
	leagueFormState: LeagueFormUiState,
	onBackPressed: () -> Unit,
	loadLeague: () -> Unit,
	saveLeague: () -> Unit,
	deleteLeague: () -> Unit,
	onNameChanged: (String) -> Unit,
	onRecurrenceChanged: (LeagueRecurrence) -> Unit,
	onExcludeFromStatisticsChanged: (ExcludeFromStatistics) -> Unit,
	onIncludeAdditionalPinFallChanged: (IncludeAdditionalPinFall) -> Unit,
	onNumberOfGamesChanged: (Int) -> Unit,
	onGamesPerSeriesChanged: (GamesPerSeries) -> Unit,
	onAdditionalPinFallChanged: (Int) -> Unit,
	onAdditionalGamesChanged: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		loadLeague()
	}

	Scaffold(
		topBar = {
			LeagueFormTopBar(
				leagueFormState = leagueFormState,
				onBackPressed = onBackPressed,
				saveLeague = saveLeague,
			)
		}
	) { padding ->
		when (leagueFormState) {
			LeagueFormUiState.Loading, LeagueFormUiState.Dismissed -> Unit
			is LeagueFormUiState.Create ->
				LeagueForm(
					name = leagueFormState.properties.name,
					onNameChanged = onNameChanged,
					nameErrorId = leagueFormState.fieldErrors.nameErrorId,
					recurrence = leagueFormState.properties.recurrence,
					onRecurrenceChanged = onRecurrenceChanged,
					excludeFromStatistics = leagueFormState.properties.excludeFromStatistics,
					onExcludeFromStatisticsChanged = onExcludeFromStatisticsChanged,
					numberOfGames = leagueFormState.properties.numberOfGames,
					onNumberOfGamesChanged = onNumberOfGamesChanged,
					gamesPerSeries = leagueFormState.gamesPerSeries,
					onGamesPerSeriesChanged = onGamesPerSeriesChanged,
					includeAdditionalPinFall = leagueFormState.includeAdditionalPinFall,
					onIncludeAdditionalPinFallChanged = onIncludeAdditionalPinFallChanged,
					additionalPinFall = leagueFormState.properties.additionalPinFall ?: 0,
					onAdditionalPinFallChanged = onAdditionalPinFallChanged,
					additionalGames = leagueFormState.properties.additionalGames ?: 0,
					onAdditionalGamesChanged = onAdditionalGamesChanged,
					modifier = modifier.padding(padding),
				)
			is LeagueFormUiState.Edit ->
				LeagueForm(
					name = leagueFormState.properties.name,
					onNameChanged = onNameChanged,
					nameErrorId = leagueFormState.fieldErrors.nameErrorId,
					excludeFromStatistics = leagueFormState.properties.excludeFromStatistics,
					onExcludeFromStatisticsChanged = onExcludeFromStatisticsChanged,
					includeAdditionalPinFall = leagueFormState.includeAdditionalPinFall,
					onIncludeAdditionalPinFallChanged = onIncludeAdditionalPinFallChanged,
					additionalPinFall = leagueFormState.properties.additionalPinFall ?: 0,
					onAdditionalPinFallChanged = onAdditionalPinFallChanged,
					additionalGames = leagueFormState.properties.additionalGames ?: 0,
					onAdditionalGamesChanged = onAdditionalGamesChanged,
					modifier = modifier.padding(padding),
				)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LeagueFormTopBar(
	leagueFormState: LeagueFormUiState,
	onBackPressed: () -> Unit,
	saveLeague: () -> Unit,
) {
	TopAppBar(
		title = { Title(leagueFormState) },
		navigationIcon = { BackButton(onClick = onBackPressed) },
		actions = { Actions(leagueFormState, saveLeague) },
	)
}

@Composable
internal fun Title(leagueFormState: LeagueFormUiState) {
	when (leagueFormState) {
		LeagueFormUiState.Loading, LeagueFormUiState.Dismissed -> Text("")
		is LeagueFormUiState.Create -> Text(stringResource(R.string.league_form_title_new))
		is LeagueFormUiState.Edit -> Text(stringResource(R.string.league_form_title_edit, leagueFormState.initialValue.name))
	}
}

@Composable
internal fun Actions(
	leagueFormState: LeagueFormUiState,
	saveLeague: () -> Unit,
) {
	when (leagueFormState) {
		LeagueFormUiState.Loading, LeagueFormUiState.Dismissed -> Unit
		is LeagueFormUiState.Edit, is LeagueFormUiState.Create ->
			Text(
				stringResource(R.string.action_save),
				modifier = Modifier
					.clickable(onClick = saveLeague)
					.padding(16.dp),
			)
	}
}