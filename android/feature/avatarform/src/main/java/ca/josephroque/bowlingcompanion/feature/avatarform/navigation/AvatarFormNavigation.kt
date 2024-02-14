package ca.josephroque.bowlingcompanion.feature.avatarform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.avatarform.AvatarFormRoute

fun NavController.navigateToAvatarFormForResult(
	avatar: Avatar,
	navResultCallback: NavResultCallback<Avatar>,
	navOptions: NavOptions? = null,
) {
	this.navigateForResult(
		route = Route.EditAvatar.createRoute(avatar.toString()),
		navResultCallback = navResultCallback,
		navOptions = navOptions,
	)
}

fun NavGraphBuilder.avatarFormScreen(onDismissWithResult: (Avatar?) -> Unit) {
	composable(
		route = Route.EditAvatar.route,
		arguments = listOf(
			navArgument(Route.EditAvatar.ARG_AVATAR) { type = NavType.StringType },
		),
	) {
		AvatarFormRoute(
			onDismissWithResult = onDismissWithResult,
		)
	}
}
