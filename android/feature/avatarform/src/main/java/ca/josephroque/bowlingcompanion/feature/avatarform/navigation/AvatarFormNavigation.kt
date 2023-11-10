package ca.josephroque.bowlingcompanion.feature.avatarform.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.common.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.feature.avatarform.AvatarFormRoute

const val AVATAR_VALUE = "avatardef"
const val avatarFormNavigationRoute = "edit_avatar/{$AVATAR_VALUE}"

fun NavController.navigateToAvatarFormForResult(
	avatar: Avatar,
	navResultCallback: NavResultCallback<Avatar>,
) {
	val encoded = Uri.encode(avatar.toString())
	this.navigateForResult("edit_avatar/$encoded", navResultCallback)
}

fun NavGraphBuilder.avatarFormScreen(
	onDismissWithResult: (Avatar?) -> Unit,
) {
	composable(
		route = avatarFormNavigationRoute,
		arguments = listOf(
			navArgument(AVATAR_VALUE) { type = NavType.StringType },
		),
	) {
		AvatarFormRoute(
			onDismissWithResult = onDismissWithResult,
		)
	}
}