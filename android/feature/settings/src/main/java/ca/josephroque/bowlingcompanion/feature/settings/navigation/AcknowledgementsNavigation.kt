package ca.josephroque.bowlingcompanion.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.settings.acknowledgements.AcknowledgementsSettingsRoute
import ca.josephroque.bowlingcompanion.feature.settings.acknowledgements.details.AcknowledgementDetailsRoute

fun NavController.navigateToAcknowledgementDetails(
	acknowledgement: String,
	navOptions: NavOptions? = null,
) {
	this.navigate(
		route = Route.AcknowledgementDetails.createRoute(acknowledgement),
		navOptions = navOptions,
	)
}

fun NavGraphBuilder.acknowledgementDetailsScreen(onBackPressed: () -> Unit) {
	composable(
		route = Route.AcknowledgementDetails.route,
		arguments = listOf(
			navArgument(Route.AcknowledgementDetails.ARG_ACKNOWLEDGEMENT) { type = NavType.StringType },
		),
	) {
		AcknowledgementDetailsRoute(
			onBackPressed = onBackPressed,
		)
	}
}

fun NavController.navigateToAcknowledgementsSettings(navOptions: NavOptions? = null) {
	this.navigate(Route.Acknowledgements.route, navOptions)
}

fun NavGraphBuilder.acknowledgementsSettingsScreen(
	onBackPressed: () -> Unit,
	onShowAcknowledgementDetails: (String) -> Unit,
) {
	composable(
		route = Route.Acknowledgements.route,
	) {
		AcknowledgementsSettingsRoute(
			onBackPressed = onBackPressed,
			onShowAcknowledgementDetails = onShowAcknowledgementDetails,
		)
	}
}
