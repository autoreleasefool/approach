package ca.josephroque.bowlingcompanion.feature.avatarform.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.navigation.AvatarFormResultViewModel
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.avatarform.AvatarFormRoute

fun NavController.navigateToAvatarFormForResult(avatar: Avatar, navOptions: NavOptions? = null) {
	this.navigate(
		route = Route.EditAvatar.createRoute(avatar.toString()),
		navOptions = navOptions,
	)
}

fun NavGraphBuilder.avatarFormScreen(navController: NavController, onDismiss: () -> Unit) {
	composable(
		route = Route.EditAvatar.route,
		arguments = listOf(
			navArgument(Route.EditAvatar.ARG_AVATAR) { type = NavType.StringType },
		),
	) {
		val parentEntry = remember(it) {
			navController.previousBackStackEntry
		}

		val resultViewModel = if (parentEntry == null) {
			hiltViewModel<AvatarFormResultViewModel>()
		} else {
			hiltViewModel<AvatarFormResultViewModel>(parentEntry)
		}

		AvatarFormRoute(
			onDismissWithResult = { avatar ->
				resultViewModel.setResult(avatar)
				onDismiss()
			},
		)
	}
}
