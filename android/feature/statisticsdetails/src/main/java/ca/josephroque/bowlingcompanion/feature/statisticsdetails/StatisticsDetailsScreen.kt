package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChart
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsList
import kotlinx.coroutines.launch

@Composable
fun StatisticsDetailsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsDetailsViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	val lifecycle = lifecycleOwner.lifecycle

	DisposableEffect(lifecycle) {
		lifecycle.addObserver(viewModel)
		onDispose {
			lifecycle.removeObserver(viewModel)
		}
	}

	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						StatisticsDetailsScreenEvent.Dismissed -> onBackPressed()
					}
				}
		}
	}

	DisposableEffect(Unit) {
		onDispose {
			lifecycleOwner.lifecycleScope.launch {
				viewModel.handleAction(StatisticsDetailsScreenUiAction.OnDismissed)
			}
		}
	}

	StatisticsDetailsScreen(
		state = uiState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsDetailsScreen(
	state: StatisticsDetailsScreenUiState,
	onAction: (StatisticsDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val bottomSheetState = rememberStandardBottomSheetState(
		skipHiddenState = false,
		confirmValueChange = { targetValue ->
			onAction(
				StatisticsDetailsScreenUiAction.BottomSheet(
					StatisticsDetailsBottomSheetUiAction.SheetValueChanged(targetValue),
				),
			)
			true
		},
	)
	val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
	val handleHeight = remember { mutableFloatStateOf(56f) }

	val nextSheetValue = when (state) {
		StatisticsDetailsScreenUiState.Loading -> SheetValue.Hidden
		is StatisticsDetailsScreenUiState.Loaded -> state.bottomSheetValue
	}
	LaunchedEffect(nextSheetValue) {
		if (nextSheetValue != bottomSheetState.currentValue) {
			when (nextSheetValue) {
				SheetValue.Hidden -> bottomSheetState.hide()
				SheetValue.PartiallyExpanded -> bottomSheetState.partialExpand()
				SheetValue.Expanded -> bottomSheetState.expand()
			}
		}
	}

	BottomSheetScaffold(
		scaffoldState = scaffoldState,
		topBar = {
			StatisticsDetailsTopBar(
				state = when (state) {
					StatisticsDetailsScreenUiState.Loading -> null
					is StatisticsDetailsScreenUiState.Loaded -> state.chart
				},
				onAction = { onAction(StatisticsDetailsScreenUiAction.TopBar(it)) },
			)
		},
		sheetSwipeEnabled = false,
		sheetDragHandle = {
			val dragHandleDescription =
				@Suppress("ktlint:standard:max-line-length")
				stringResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.string.bottom_sheet_drag_handle_description,
				)
			Surface(
				modifier = modifier
					.padding(vertical = 8.dp)
					.semantics { contentDescription = dragHandleDescription }
					.onGloballyPositioned { handleHeight.floatValue = it.size.height.toFloat() },
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				shape = MaterialTheme.shapes.extraLarge,
			) {
				Box(modifier = Modifier.size(width = 32.dp, height = 4.dp))
			}
		},
		sheetPeekHeight =
		maxOf(
			(
				(state as? StatisticsDetailsScreenUiState.Loaded)?.headerPeekHeight?.plus(
					handleHeight.floatValue,
				) ?: 0f
				).dp,
			120.dp,
		),
		sheetContent = {
			when (state) {
				StatisticsDetailsScreenUiState.Loading -> Unit
				is StatisticsDetailsScreenUiState.Loaded -> StatisticsDetailsList(
					state = state.list,
					onAction = { onAction(StatisticsDetailsScreenUiAction.List(it)) },
				)
			}
		},
	) { padding ->
		when (state) {
			StatisticsDetailsScreenUiState.Loading -> Unit
			is StatisticsDetailsScreenUiState.Loaded -> StatisticsDetailsChart(
				state = state.chart,
				onAction = { onAction(StatisticsDetailsScreenUiAction.Chart(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}
