package ca.josephroque.bowlingcompanion.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import ca.josephroque.bowlingcompanion.core.designsystem.components.ApproachNavigationBarItem
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.navigation.ApproachNavHost
import ca.josephroque.bowlingcompanion.navigation.TopLevelDestination

@Composable
fun ApproachApp(
	state: ApproachAppUiState,
	finishActivity: () -> Unit,
	onTabChanged: (TopLevelDestination) -> Unit,
	appState: ApproachAppState = rememberApproachAppState(),
) {
	Scaffold(
		bottomBar = {
			ApproachBottomBar(
				destinations = appState.topLevelDestinations,
				badgeCount = state.badgeCount,
				onNavigateToDestination = {
					appState.navigateToTopLevelDestination(it)
					onTabChanged(it)
				},
				currentDestination = appState.currentDestination
			)
		}
	) { padding ->
		Row(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
			ApproachNavHost(
				appState = appState,
				isOnboardingComplete = state.isOnboardingComplete,
				finishActivity = finishActivity,
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApproachBottomBar(
	destinations: List<TopLevelDestination>,
	badgeCount: Map<TopLevelDestination, Int>,
	onNavigateToDestination: (TopLevelDestination) -> Unit,
	currentDestination: NavDestination?
) {
	val isBottomBarVisible = remember { MutableTransitionState(false) }
	LaunchedEffect(currentDestination?.route) {
		isBottomBarVisible.targetState = !bottomBarHiddenRoutes.contains(currentDestination?.route)
	}

	AnimatedVisibility(
		visibleState = isBottomBarVisible,
		enter = slideInVertically(initialOffsetY = { it }),
		exit = slideOutVertically(targetOffsetY = { it }),
	) {
		NavigationBar {
			destinations.forEach { destination ->
				val isSelected = currentDestination.isTopLevelDestinationInHierarchy(destination)

				ApproachNavigationBarItem(
					isSelected = isSelected,
					onClick = { onNavigateToDestination(destination) },
					icon = {
						val badgeNumber = badgeCount[destination]
						if (badgeNumber == null) {
							Icon(
								painterResource(destination.unselectedIcon),
								contentDescription = null,
								modifier = Modifier.size(24.dp),
							)
						} else {
							BadgedBox(
								badge = {
									Badge {
										Text(badgeNumber.toString())
									}
								}
							) {
								Icon(
									painterResource(destination.unselectedIcon),
									contentDescription = null,
									modifier = Modifier.size(24.dp),
								)
							}
						}
					},
					selectedIcon = {
						val badgeNumber = badgeCount[destination]
						if (badgeNumber == null) {
							Icon(
								painterResource(destination.selectedIcon),
								contentDescription = null,
								modifier = Modifier.size(24.dp),
							)
						} else {
							BadgedBox(
								badge = {
									Badge {
										Text(badgeNumber.toString())
									}
								}
							) {
								Icon(
									painterResource(destination.selectedIcon),
									contentDescription = null,
									modifier = Modifier.size(24.dp),
								)
							}
						}
					},
					label = {
						Text(
							stringResource(destination.iconTextId),
							style = MaterialTheme.typography.bodyMedium,
						)
					}
				)
			}
		}
	}
}

val bottomBarHiddenRoutes = Route::class
	.sealedSubclasses
	.mapNotNull { it.objectInstance }
	.filter { !it.isBottomBarVisible }
	.map(Route::route)
	.toSet()