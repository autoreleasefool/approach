package ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.HeaderAction
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.footer
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.header
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.MutedEmptyState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.gearList
import ca.josephroque.bowlingcompanion.feature.leagueslist.ui.LeaguesListUiAction
import ca.josephroque.bowlingcompanion.feature.leagueslist.ui.LeaguesListUiState
import ca.josephroque.bowlingcompanion.feature.leagueslist.ui.leaguesList
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayout
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.placeholder.StatisticsWidgetPlaceholderCard

@Composable
fun BowlerDetails(
	state: BowlerDetailsUiState,
	onAction: (BowlerDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	state.leaguesList.leagueToArchive?.let {
		ArchiveDialog(
			itemName = it.name,
			onArchive = { onAction(BowlerDetailsUiAction.LeaguesListAction(LeaguesListUiAction.ConfirmArchiveClicked)) },
			onDismiss = { onAction(BowlerDetailsUiAction.LeaguesListAction(LeaguesListUiAction.DismissArchiveClicked)) },
		)
	}

	LazyColumn(
		modifier = modifier
			.fillMaxSize()
	) {
		state.widgets?.let { layout ->
			item(contentType = "statistics_widget_layout") {
				StatisticsWidgetLayout(
					state = layout,
					onAction = { onAction(BowlerDetailsUiAction.StatisticsWidgetLayout(it)) },
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp),
				)
			}
		}

		bowlerLeaguesList(
			state = state.leaguesList,
			onAction = { onAction(BowlerDetailsUiAction.LeaguesListAction(it)) },
		)

		item {
			Divider(thickness = 8.dp)
		}

		bowlerGearList(
			state = state.gearList,
			onAction = onAction,
		)
	}
}

private fun LazyListScope.bowlerLeaguesList(
	state: LeaguesListUiState,
	onAction: (LeaguesListUiAction) -> Unit,
) {
	header(R.string.bowler_details_league_list_title)
	if (state.list.isEmpty()) {
		item {
			MutedEmptyState(
				title = ca.josephroque.bowlingcompanion.feature.leagueslist.ui.R.string.league_list_empty_title,
				message = ca.josephroque.bowlingcompanion.feature.leagueslist.ui.R.string.league_list_empty_message,
				icon = ca.josephroque.bowlingcompanion.feature.leagueslist.ui.R.drawable.league_list_empty_state,
				modifier = Modifier.padding(bottom = 16.dp),
			)
		}
	} else {
		leaguesList(
			list = state.list,
			onLeagueClick = { onAction(LeaguesListUiAction.LeagueClicked(it.id)) },
			onArchiveLeague = { onAction(LeaguesListUiAction.LeagueArchived(it)) },
			onEditLeague = { onAction(LeaguesListUiAction.LeagueEdited(it.id)) },
		)
	}
}

private fun LazyListScope.bowlerGearList(
	state: GearListUiState,
	onAction: (BowlerDetailsUiAction) -> Unit
) {
	header(
		titleResourceId = R.string.bowler_details_preferred_gear_title,
		action = HeaderAction(
			actionResourceId = RCoreDesign.string.action_manage,
			onClick = { onAction(BowlerDetailsUiAction.ManageGearClicked) },
		),
	)

	if (state.list.isNotEmpty()) {
		gearList(
			list = state.list,
			onGearClick = { onAction(BowlerDetailsUiAction.GearClicked(it.id)) },
		)
	}

	footer(R.string.bowler_details_preferred_gear_description)
}