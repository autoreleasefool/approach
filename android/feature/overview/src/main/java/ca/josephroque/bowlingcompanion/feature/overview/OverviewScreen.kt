package ca.josephroque.bowlingcompanion.feature.overview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.overview.ui.Overview
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewFloatingActionButton
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewTopBar
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun OverviewRoute(
	onEditBowler: (UUID) -> Unit,
	onAddBowler: () -> Unit,
	onShowBowlerDetails: (UUID) -> Unit,
	onEditStatisticsWidgets: (String) -> Unit,
	onShowStatistics: (UUID) -> Unit,
	onShowQuickPlay: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: OverviewViewModel = hiltViewModel(),
) {
	val overviewScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is OverviewScreenEvent.ShowBowlerDetails -> onShowBowlerDetails(it.id)
						is OverviewScreenEvent.EditBowler -> onEditBowler(it.id)
						is OverviewScreenEvent.EditStatisticsWidget -> onEditStatisticsWidgets(it.context)
						is OverviewScreenEvent.ShowStatistics -> onShowStatistics(it.widget)
						OverviewScreenEvent.ShowQuickPlay -> onShowQuickPlay()
						OverviewScreenEvent.AddBowler -> onAddBowler()
					}
				}
		}
	}

	OverviewScreen(
		state = overviewScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverviewScreen(
	state: OverviewScreenUiState,
	onAction: (OverviewScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(OverviewScreenUiAction.DidAppear)
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
	var fabHeight by remember { mutableIntStateOf(0) }
	val fabHeightInDp = with(LocalDensity.current) { fabHeight.toDp() }

	Scaffold(
		topBar = {
			OverviewTopBar(
				onAction = { onAction(OverviewScreenUiAction.OverviewAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		floatingActionButton = {
			OverviewFloatingActionButton(
				modifier = Modifier.onGloballyPositioned { fabHeight = it.size.height },
				onAction = { onAction(OverviewScreenUiAction.OverviewAction(it)) },
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			OverviewScreenUiState.Loading -> Unit
			is OverviewScreenUiState.Loaded -> Overview(
				state = state.overview,
				onAction = { onAction(OverviewScreenUiAction.OverviewAction(it)) },
				modifier = Modifier.padding(padding),
				contentPadding = PaddingValues(bottom = fabHeightInDp + 16.dp),
			)
		}
	}
}