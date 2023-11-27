package ca.josephroque.bowlingcompanion.feature.overview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.overview.ui.Overview
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewTopBar
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun OverviewRoute(
	onEditBowler: (UUID) -> Unit,
	onAddBowler: () -> Unit,
	onShowBowlerDetails: (UUID) -> Unit,
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
						OverviewScreenEvent.AddBowler -> onAddBowler()
						OverviewScreenEvent.EditStatisticsWidget -> Unit // TODO: Show edit statistics widget
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

@Composable
private fun OverviewScreen(
	state: OverviewScreenUiState,
	onAction: (OverviewScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			OverviewTopBar(onAction = { onAction(OverviewScreenUiAction.OverviewAction(it)) })
		}
	) { padding ->
		when (state) {
			OverviewScreenUiState.Loading -> Unit
			is OverviewScreenUiState.Loaded -> Overview(
				state = state.overview,
				onAction = { onAction(OverviewScreenUiAction.OverviewAction(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}