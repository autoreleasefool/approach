package ca.josephroque.bowlingcompanion.feature.statisticssettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.components.LabeledSwitch
import ca.josephroque.bowlingcompanion.core.components.list.ListSectionHeader

@Composable
internal fun StatisticsSettingsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsSettingsViewModel = hiltViewModel(),
) {
	val statisticsSettingsState by viewModel.uiState.collectAsStateWithLifecycle()
	
	StatisticsSettingsScreen(
		statisticsSettingsState = statisticsSettingsState,
		onBackPressed = onBackPressed,
		onToggleCountH2AsH = viewModel::toggleIsCountingH2AsH,
		onToggleCountSplitWithBonusAsSplit = viewModel::toggleIsCountingSplitWithBonusAsSplit,
		onToggleHidingZeroStatistics = viewModel::toggleIsHidingZeroStatistics,
		onToggleHidingWidgetsInBowlersList = viewModel::toggleIsHidingWidgetsInBowlersList,
		onToggleHidingWidgetsInLeaguesList = viewModel::toggleIsHidingWidgetsInLeaguesList,
		modifier = modifier,
	)
}

@Composable
fun StatisticsSettingsScreen(
	statisticsSettingsState: StatisticsSettingsUiState,
	onBackPressed: () -> Unit,
	onToggleCountH2AsH: (Boolean?) -> Unit,
	onToggleCountSplitWithBonusAsSplit: (Boolean?) -> Unit,
	onToggleHidingZeroStatistics: (Boolean?) -> Unit,
	onToggleHidingWidgetsInBowlersList: (Boolean?) -> Unit,
	onToggleHidingWidgetsInLeaguesList: (Boolean?) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			StatisticsSettingsTopBar(onBackPressed = onBackPressed)
		}
	) { padding ->
		Column(
			modifier = modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(padding)
		) {
			when (statisticsSettingsState) {
				StatisticsSettingsUiState.Loading -> Unit
				is StatisticsSettingsUiState.Success -> {
					ListSectionHeader(titleResourceId = R.string.statistics_settings_per_frame)

					LabeledSwitch(
						checked = statisticsSettingsState.isCountingH2AsH,
						onCheckedChange = onToggleCountH2AsH,
						titleResourceId = R.string.statistics_settings_count_h2_as_h,
					)

					LabeledSwitch(
						checked = statisticsSettingsState.isCountingSplitWithBonusAsSplit,
						onCheckedChange = onToggleCountSplitWithBonusAsSplit,
						titleResourceId = R.string.statistics_settings_count_s2_as_s,
					)

					Divider()

					ListSectionHeader(titleResourceId = R.string.statistics_settings_overall)

					LabeledSwitch(
						checked = statisticsSettingsState.isHidingZeroStatistics,
						onCheckedChange = onToggleHidingZeroStatistics,
						titleResourceId = R.string.statistics_settings_hide_zero,
					)

					Divider()

					ListSectionHeader(titleResourceId = R.string.statistics_settings_widgets)

					LabeledSwitch(
						checked = statisticsSettingsState.isHidingWidgetsInBowlersList,
						onCheckedChange = onToggleHidingWidgetsInBowlersList,
						titleResourceId = R.string.statistics_settings_hide_widgets_in_bowlers,
					)

					LabeledSwitch(
						checked = statisticsSettingsState.isHidingWidgetsInLeaguesList,
						onCheckedChange = onToggleHidingWidgetsInLeaguesList,
						titleResourceId = R.string.statistics_settings_hide_widgets_in_leagues,
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsSettingsTopBar(
	onBackPressed: () -> Unit,
) {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.settings_item_analytics),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			IconButton(onClick = onBackPressed) {
				Icon(
					imageVector = Icons.Default.ArrowBack,
					contentDescription = stringResource(R.string.cd_back),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	)
}

@Preview
@Composable
fun StatisticsSettingsScreenPreview() {
	Surface {
		StatisticsSettingsScreen(
			statisticsSettingsState = StatisticsSettingsUiState.Success(
				isCountingH2AsH = true,
				isCountingSplitWithBonusAsSplit = true,
				isHidingZeroStatistics = true,
				isHidingWidgetsInBowlersList = true,
				isHidingWidgetsInLeaguesList = true,
			),
			onBackPressed = {},
			onToggleCountH2AsH = {},
			onToggleCountSplitWithBonusAsSplit = {},
			onToggleHidingZeroStatistics = {},
			onToggleHidingWidgetsInBowlersList = {},
			onToggleHidingWidgetsInLeaguesList = {},
		)
	}
}