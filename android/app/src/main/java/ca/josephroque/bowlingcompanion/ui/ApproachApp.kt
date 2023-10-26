package ca.josephroque.bowlingcompanion.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import ca.josephroque.bowlingcompanion.core.components.ApproachNavigationBarItem
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.gamesEditorNavigationRoute
import ca.josephroque.bowlingcompanion.feature.onboarding.navigation.onboardingNavigationRoute
import ca.josephroque.bowlingcompanion.navigation.ApproachNavHost
import ca.josephroque.bowlingcompanion.navigation.TopLevelDestination

@Composable
fun ApproachApp(
	isOnboardingComplete: Boolean,
	finishActivity: () -> Unit,
	appState: ApproachAppState = rememberApproachAppState(),
) {
	Scaffold(
		bottomBar = {
			ApproachBottomBar(
				destinations = appState.topLevelDestinations,
				onNavigateToDestination = appState::navigateToTopLevelDestination,
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
				isOnboardingComplete = isOnboardingComplete,
				finishActivity = finishActivity,
			)
		}
	}
}

@Composable
private fun ApproachBottomBar(
	destinations: List<TopLevelDestination>,
	onNavigateToDestination: (TopLevelDestination) -> Unit,
	currentDestination: NavDestination?
) {
	// TODO: Find a better way to determine if NavigationBar should be hidden
	// TODO: Animate NavigationBar disappearing https://stackoverflow.com/questions/66837991/
	if (currentDestination?.route != onboardingNavigationRoute && currentDestination?.route != gamesEditorNavigationRoute) {
		NavigationBar {
			destinations.forEach { destination ->
				val isSelected = currentDestination.isTopLevelDestinationInHierarchy(destination)

				ApproachNavigationBarItem(
					isSelected = isSelected,
					onClick = { onNavigateToDestination(destination) },
					icon = {
						Icon(
							painterResource(destination.unselectedIcon),
							contentDescription = null,
							modifier = Modifier.size(24.dp),
						)
				  },
					selectedIcon = {
						Icon(
							painterResource(destination.selectedIcon),
							contentDescription = null,
							modifier = Modifier.size(24.dp),
						)
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

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
	this?.hierarchy?.any {
		it.route?.contains(destination.name, true) ?: false
	} ?: false