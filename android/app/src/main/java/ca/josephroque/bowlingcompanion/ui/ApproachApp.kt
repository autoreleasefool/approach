@file:Suppress("UsingMaterialAndMaterial3Libraries")

package ca.josephroque.bowlingcompanion.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import ca.josephroque.bowlingcompanion.core.designsystem.components.ApproachNavigationBarItem
import ca.josephroque.bowlingcompanion.feature.achievementslist.navigation.navigateToAchievementsList
import ca.josephroque.bowlingcompanion.feature.announcements.AnnouncementDialog
import ca.josephroque.bowlingcompanion.navigation.ApproachNavHost
import ca.josephroque.bowlingcompanion.navigation.TopLevelDestination

@Composable
fun ApproachApp(
	state: ApproachAppUiState,
	finishActivity: () -> Unit,
	onTabChanged: (TopLevelDestination) -> Unit,
	appState: ApproachAppState,
) {
	AnnouncementDialog(
		onNavigateToAchievements = {
			appState.navigateToTopLevelDestination(TopLevelDestination.SETTINGS_OVERVIEW)
			appState.navController.navigateToAchievementsList()
		},
	)

	ModalBottomSheetLayout(
		sheetShape = RoundedCornerShapeWithOffset(
			offset = Offset(0f, WindowInsets.statusBars.getTop(LocalDensity.current).toFloat()),
			topStart = 16.dp,
			topEnd = 16.dp,
		),
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

private fun RoundedCornerShapeWithOffset(
	offset: Offset,
	topStart: Dp = 0.dp,
	topEnd: Dp = 0.dp,
	bottomEnd: Dp = 0.dp,
	bottomStart: Dp = 0.dp,
) = RoundedCornerShapeWithOffset(
	offset = offset,
	topStart = CornerSize(topStart),
	topEnd = CornerSize(topEnd),
	bottomEnd = CornerSize(bottomEnd),
	bottomStart = CornerSize(bottomStart),
)

private class RoundedCornerShapeWithOffset(
	val offset: Offset,
	topStart: CornerSize,
	topEnd: CornerSize,
	bottomEnd: CornerSize,
	bottomStart: CornerSize,
) : CornerBasedShape(
	topStart,
	topEnd,
	bottomEnd,
	bottomStart,
) {
	override fun copy(topStart: CornerSize, topEnd: CornerSize, bottomEnd: CornerSize, bottomStart: CornerSize) =
		RoundedCornerShape(
			topStart = topStart,
			topEnd = topEnd,
			bottomEnd = bottomEnd,
			bottomStart = bottomStart,
		)

	override fun createOutline(
		size: Size,
		topStart: Float,
		topEnd: Float,
		bottomEnd: Float,
		bottomStart: Float,
		layoutDirection: LayoutDirection,
	): Outline = if (topStart + topEnd + bottomEnd + bottomStart == 0.0f) {
		Outline.Rectangle(size.toRect())
	} else {
		Outline.Rounded(
			RoundRect(
				rect = Rect(offset, size),
				topLeft = CornerRadius(if (layoutDirection == LayoutDirection.Ltr) topStart else topEnd),
				topRight = CornerRadius(if (layoutDirection == LayoutDirection.Ltr) topEnd else topStart),
				bottomRight = CornerRadius(if (layoutDirection == LayoutDirection.Ltr) bottomEnd else bottomStart),
				bottomLeft = CornerRadius(if (layoutDirection == LayoutDirection.Ltr) bottomStart else bottomEnd),
			),
		)
	}
}
