package ca.josephroque.bowlingcompanion.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator

/**
 * The navigation result callback between two call screens.
 */
typealias NavResultCallback<T> = (T) -> Unit

// A SavedStateHandle key is used to set/get NavResultCallback<T>
private const val NAV_RESULT_CALLBACK_KEY = "NavResultCallbackKey"

/**
 * Set the navigation result callback on calling screen.
 *
 * @param callback The navigation result callback.
 */
fun <T> NavController.setNavResultCallback(callback: NavResultCallback<T>) {
	currentBackStackEntry?.savedStateHandle?.set(NAV_RESULT_CALLBACK_KEY, callback)
}

/**
 *  Get the navigation result callback on called screen.
 *
 * @return The navigation result callback if the previous backstack entry exists
 */
fun <T> NavController.getNavResultCallback(): NavResultCallback<T>? {
	return previousBackStackEntry?.savedStateHandle?.remove(NAV_RESULT_CALLBACK_KEY)
}

/**
 *  Attempts to pop the controller's back stack and returns the result.
 *
 * @param result the navigation result
 */
fun <T> NavController.popBackStackWithResult(result: T) {
	getNavResultCallback<T>()?.invoke(result)
	popBackStack()
}

/**
 * Navigate to a route in the current NavGraph. If an invalid route is given, an
 * [IllegalArgumentException] will be thrown.
 *
 * @param route route for the destination
 * @param navResultCallback the navigation result callback
 * @param navOptions special options for this navigation operation
 * @param navigatorExtras extras to pass to the [Navigator]
 *
 * @throws IllegalArgumentException if the given route is invalid
 */
fun <T> NavController.navigateForResult(
	route: String,
	navResultCallback: NavResultCallback<T>,
	navOptions: NavOptions? = null,
	navigatorExtras: Navigator.Extras? = null,
) {
	setNavResultCallback(navResultCallback)
	navigate(route, navOptions, navigatorExtras)
}

/**
 * Navigate to a route in the current NavGraph. If an invalid route is given, an
 * [IllegalArgumentException] will be thrown.
 *
 * @param route route for the destination
 * @param navResultCallback the navigation result callback
 * @param builder DSL for constructing a new [NavOptions]
 *
 * @throws IllegalArgumentException if the given route is invalid
 */
@Suppress("unused")
fun <T> NavController.navigateForResult(
	route: String,
	navResultCallback: NavResultCallback<T>,
	builder: NavOptionsBuilder.() -> Unit,
) {
	setNavResultCallback(navResultCallback)
	navigate(route, builder)
}
