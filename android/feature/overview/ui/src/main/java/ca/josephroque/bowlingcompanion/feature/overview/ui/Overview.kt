package ca.josephroque.bowlingcompanion.feature.overview.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersList
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.placeholder.StatisticsWidgetPlaceholderCard

@Composable
fun Overview(
	state: OverviewUiState,
	onAction: (OverviewUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	BowlersList(
		state = state.bowlersList,
		onAction = { onAction(OverviewUiAction.BowlersListAction(it)) },
		modifier = modifier,
		header = {
			StatisticsWidgetPlaceholderCard(
				onClick = { onAction(OverviewUiAction.EditStatisticsWidgetClicked) },
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 16.dp),
			)

			Text(
				text = stringResource(R.string.bowler_list_title),
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 16.dp),
			)
		}
	)
}