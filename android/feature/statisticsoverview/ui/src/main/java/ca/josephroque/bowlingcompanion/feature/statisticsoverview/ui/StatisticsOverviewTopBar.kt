package ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsOverviewTopBar() {
	TopAppBar(
		title = {
			Text(text = stringResource(R.string.statistics_overview_title))
		}
	)
}