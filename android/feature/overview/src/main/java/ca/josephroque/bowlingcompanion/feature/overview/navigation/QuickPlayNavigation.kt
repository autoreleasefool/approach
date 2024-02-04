package ca.josephroque.bowlingcompanion.feature.overview.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.overview.quickplay.QuickPlayRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import java.util.UUID

fun NavController.navigateToQuickPlay(navOptions: NavOptions? = null) {
	this.navigate(Route.QuickPlay.route, navOptions)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.quickPlay(
	onBackPressed: () -> Unit,
	onBeginRecording: (List<Pair<UUID, UUID>>) -> Unit,
	onPickBowler: (Set<UUID>, NavResultCallback<Set<UUID>>) -> Unit,
	onPickLeague: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
) {
	bottomSheet(
		route = Route.QuickPlay.route,
	) {
		QuickPlayRoute(
			onDismiss = onBackPressed,
			onBeginRecording = onBeginRecording,
			onPickBowler = onPickBowler,
			onPickLeague = onPickLeague,
		)
	}
}