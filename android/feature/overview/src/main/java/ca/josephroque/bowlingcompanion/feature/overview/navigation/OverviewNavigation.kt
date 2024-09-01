package ca.josephroque.bowlingcompanion.feature.overview.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.overview.OverviewRoute
import java.util.UUID

fun NavGraphBuilder.overviewScreen(
	shouldShowOnboarding: State<Boolean>,
	showOnboarding: () -> Unit,
	onEditBowler: (BowlerID) -> Unit,
	onAddBowler: () -> Unit,
	onShowBowlerDetails: (BowlerID) -> Unit,
	onEditStatisticsWidgets: (String) -> Unit,
	onShowWidgetStatistics: (TrackableFilter) -> Unit,
	onShowQuickPlay: () -> Unit,
	onResumeGame: (List<SeriesID>, UUID) -> Unit,
	onShowWidgetError: () -> Unit,
) {
	composable(
		route = Route.Overview.route,
	) {
		LaunchedEffect(shouldShowOnboarding) {
			if (shouldShowOnboarding.value) {
				showOnboarding()
			}
		}

		if (!shouldShowOnboarding.value) {
			OverviewRoute(
				onEditBowler = onEditBowler,
				onAddBowler = onAddBowler,
				onShowBowlerDetails = onShowBowlerDetails,
				onEditStatisticsWidgets = onEditStatisticsWidgets,
				onShowWidgetStatistics = onShowWidgetStatistics,
				onShowQuickPlay = onShowQuickPlay,
				onResumeGame = onResumeGame,
				onShowWidgetNotEnoughDataError = onShowWidgetError,
				onShowWidgetUnavailableError = onShowWidgetError,
			)
		}
	}
}
