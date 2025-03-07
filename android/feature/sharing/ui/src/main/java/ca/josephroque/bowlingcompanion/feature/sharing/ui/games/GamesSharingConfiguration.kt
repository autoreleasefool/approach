package ca.josephroque.bowlingcompanion.feature.sharing.ui.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.CheckBoxRow
import ca.josephroque.bowlingcompanion.core.designsystem.components.CollapsibleSection
import ca.josephroque.bowlingcompanion.core.designsystem.components.SectionHeader
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.feature.sharing.ui.R
import ca.josephroque.bowlingcompanion.feature.sharing.ui.components.AppearanceSegmentedButton
import ca.josephroque.bowlingcompanion.feature.sharing.ui.components.FilterItem

@Composable
fun GamesSharingConfiguration(
	state: GamesSharingConfigurationUiState,
	onAction: (GamesSharingConfigurationUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {
		Column(modifier = Modifier.verticalScroll(rememberScrollState()))  {
			HeaderSection(state = state, onAction = onAction)

			HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

			GamesSection(state = state, onAction = onAction)

			HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

			AppearanceSection(state = state, onAction = onAction)
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HeaderSection(
	state: GamesSharingConfigurationUiState,
	onAction: (GamesSharingConfigurationUiAction) -> Unit,
) {
	SectionHeader(title = stringResource(R.string.sharing_games_modifier_section_header))

	FlowRow(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = Modifier.padding(horizontal = 16.dp),
	) {
		FilterItem(
			selected = state.isSeriesDateChecked,
			title = R.string.sharing_games_modifier_date,
			onClick = { onAction(GamesSharingConfigurationUiAction.IsSeriesDateCheckedToggled(!state.isSeriesDateChecked)) },
			imageVector = Icons.Default.DateRange,
		)

		FilterItem(
			selected = state.isSeriesDetailChecked,
			title = R.string.sharing_games_modifier_summary,
			onClick = { onAction(GamesSharingConfigurationUiAction.IsSeriesDetailCheckedToggled(!state.isSeriesDetailChecked)) },
			imageVector = Icons.Default.DateRange,
		)

		FilterItem(
			selected = state.isBowlerNameChecked,
			title = R.string.sharing_games_modifier_bowler,
			onClick = { onAction(GamesSharingConfigurationUiAction.IsBowlerNameCheckedToggled(!state.isBowlerNameChecked)) },
			imageVector = Icons.Default.Person,
		)

		FilterItem(
			selected = state.isLeagueNameChecked,
			title = R.string.sharing_games_modifier_league,
			onClick = { onAction(GamesSharingConfigurationUiAction.IsLeagueNameCheckedToggled(!state.isLeagueNameChecked)) },
			imageVector = Icons.Default.Refresh,
		)
	}
}

@Composable
private fun GamesSection(
	state: GamesSharingConfigurationUiState,
	onAction: (GamesSharingConfigurationUiAction) -> Unit,
) {
	CollapsibleSection(
		title = stringResource(R.string.sharing_games_modifier_section_games),
		footer = stringResource(R.string.sharing_games_modifier_section_games_description),
	) {
		state.isGameIncluded.forEach { game ->
			CheckBoxRow(
				isSelected = game.isGameIncluded,
				onClick = {
					onAction(
						GamesSharingConfigurationUiAction.IsGameIncludedToggled(
							game.gameId,
							!game.isGameIncluded
						)
					)
				},
				content = {
					Text(
						text = stringResource(
							ca.josephroque.bowlingcompanion.core.designsystem.R.string.game_with_ordinal,
							game.index + 1
						)
					)
				},
			)
		}
	}
}

@Composable
private fun AppearanceSection(
	state: GamesSharingConfigurationUiState,
	onAction: (GamesSharingConfigurationUiAction) -> Unit,
) {
	SectionHeader(title = stringResource(R.string.sharing_appearance))

	AppearanceSegmentedButton(
		selected = state.appearance,
		onAppearanceChanged = { onAction(GamesSharingConfigurationUiAction.AppearanceChanged(it)) },
	)
}
@Preview
@Composable
private fun GamesSharingConfigurationPreview() {
	Surface {
		GamesSharingConfiguration(
			state = GamesSharingConfigurationUiState(
				isGameIncluded = listOf(
					GamesSharingConfigurationUiState.IncludedGame(
						gameId = GameID.randomID(),
						index = 0,
						isGameIncluded = true,
					),
					GamesSharingConfigurationUiState.IncludedGame(
						gameId = GameID.randomID(),
						index = 1,
						isGameIncluded = false,
					),
				)
			),
			onAction = {},
		)
	}
}