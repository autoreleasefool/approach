package ca.josephroque.bowlingcompanion.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.feature.settings.acknowledgements.AcknowledgementsSettingsRoute
import ca.josephroque.bowlingcompanion.feature.settings.acknowledgements.details.AcknowledgementDetailsRoute

const val ACKNOWLEDGEMENT = "acknowledgement"
const val acknowledgementsSettingsNavigationRoute = "settings/acknowledgements"
const val acknowledgementDetailsNavigationRoute = "settings/acknowledgements/{$ACKNOWLEDGEMENT}"

fun NavController.navigateToAcknowledgementDetails(
	acknowledgement: String,
	navOptions: NavOptions? = null,
) {
	this.navigate(
		acknowledgementDetailsNavigationRoute.replace("{$ACKNOWLEDGEMENT}", acknowledgement),
		navOptions
	)
}

fun NavGraphBuilder.acknowledgementDetailsScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = acknowledgementDetailsNavigationRoute,
		arguments = listOf(
			navArgument(ACKNOWLEDGEMENT) { type = NavType.StringType }
		),
	) {
		AcknowledgementDetailsRoute(
			onBackPressed = onBackPressed,
		)
	}
}

fun NavController.navigateToAcknowledgementsSettings(navOptions: NavOptions? = null) {
	this.navigate(acknowledgementsSettingsNavigationRoute, navOptions)
}

fun NavGraphBuilder.acknowledgementsSettingsScreen(
	onBackPressed: () -> Unit,
	onShowAcknowledgementDetails: (String) -> Unit,
) {
	composable(
		route = acknowledgementsSettingsNavigationRoute,
	) {
		AcknowledgementsSettingsRoute(
			onBackPressed = onBackPressed,
			onShowAcknowledgementDetails = onShowAcknowledgementDetails,
		)
	}
}