package ca.josephroque.bowlingcompanion.feature.overview.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersList
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayout
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun Overview(
	state: OverviewUiState,
	onAction: (OverviewUiAction) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {
	BowlersList(
		state = state.bowlersList,
		onAction = { onAction(OverviewUiAction.BowlersListAction(it)) },
		contentPadding = contentPadding,
		modifier = modifier,
		header = {
			state.widgets?.let { layout ->
				StatisticsWidgetLayout(
					state = layout,
					onAction = { onAction(OverviewUiAction.StatisticsWidgetLayout(it)) },
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp),
				)
			}

			Text(
				text = stringResource(R.string.bowler_list_title),
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 16.dp),
			)

			if (state.isShowingSwipeHint) {
				SwipeHint(onAction = onAction)
			}
		},
	)
}

@Composable
private fun SwipeHint(onAction: (OverviewUiAction) -> Unit) {
	val archiveAction = SwipeAction(
		icon = painterResource(
			ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_archive,
		),
		background = colorResource(
			ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
		),
		onSwipe = { onAction(OverviewUiAction.SwipeHintDismissed) },
	)

	SwipeableActionsBox(
		startActions = listOf(archiveAction),
		endActions = listOf(archiveAction),
	) {
		Card(modifier = Modifier.padding(horizontal = 16.dp)) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier.padding(8.dp),
			) {
				Icon(
					imageVector = Icons.Default.Info,
					contentDescription = null,
					tint = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.purple_300),
				)

				Column(
					horizontalAlignment = Alignment.Start,
					verticalArrangement = Arrangement.spacedBy(4.dp),
					modifier = Modifier.weight(1f),
				) {
					Text(
						text = stringResource(R.string.swipe_hint_did_you_know),
						style = MaterialTheme.typography.titleSmall,
					)

					Text(
						text = stringResource(R.string.swipe_hint_swipe_to_edit_or_archive),
						style = MaterialTheme.typography.bodyMedium,
					)
				}
			}
		}
	}
}
