package ca.josephroque.bowlingcompanion.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import ca.josephroque.bowlingcompanion.core.designsystem.components.ApproachNavigationBarItem
import ca.josephroque.bowlingcompanion.navigation.ApproachNavHost
import ca.josephroque.bowlingcompanion.navigation.TopLevelDestination

@Composable
fun ApproachApp(
	state: ApproachAppUiState,
	finishActivity: () -> Unit,
	onTabChanged: (TopLevelDestination) -> Unit,
	appState: ApproachAppState,
) {
	ModalBottomSheetLayout(
		sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
		bottomSheetNavigator = appState.bottomSheetNavigator,
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
					currentDestination = appState.currentDestination,
				)
			},
		) { padding ->
			Row(
				modifier = Modifier
					.fillMaxSize()
					.padding(padding)
					.consumeWindowInsets(padding),
			) {
				ApproachNavHost(
					appState = appState,
					onboarding = state.onboarding,
					finishActivity = finishActivity,
				)
			}
		}
	}
}

@Composable
private fun ApproachBottomBar(
	destinations: List<TopLevelDestination>,
	badgeCount: Map<TopLevelDestination, Int>,
	onNavigateToDestination: (TopLevelDestination) -> Unit,
	currentDestination: NavDestination?,
) {
	val isBottomBarVisible = currentDestination?.route?.contains("?hide_bottom_bar=true") != true

	if (isBottomBarVisible) {
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
								},
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
								},
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
					},
				)
			}
		}
	}
}
