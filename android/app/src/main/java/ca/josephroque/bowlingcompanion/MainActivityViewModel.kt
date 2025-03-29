package ca.josephroque.bowlingcompanion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.achievements.AchievementEarned
import ca.josephroque.bowlingcompanion.core.analytics.trackable.app.AppLaunched
import ca.josephroque.bowlingcompanion.core.analytics.trackable.app.AppTabSwitched
import ca.josephroque.bowlingcompanion.core.data.repository.AchievementsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.navigation.TopLevelDestination
import ca.josephroque.bowlingcompanion.ui.ApproachAppUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@HiltViewModel
class MainActivityViewModel @Inject constructor(
	private val achievementsRepository: AchievementsRepository,
	private val analyticsClient: AnalyticsClient,
	private val gamesRepository: GamesRepository,
	private val userDataRepository: UserDataRepository,
	private var featureFlagsClient: FeatureFlagsClient,
) : ViewModel() {
	private val isLaunchComplete: MutableStateFlow<Boolean> = MutableStateFlow(false)

	val mainActivityUiState = combine(
		userDataRepository.userData,
		isLaunchComplete,
	) { userData, isLaunchComplete ->
		MainActivityUiState.Success(
			appState = ApproachAppUiState(
				onboarding = userData.onboarding,
				destinations = TopLevelDestination.entries,
				badgeCount = if (!userData.hasOpenedAccessoriesTab) {
					mapOf(TopLevelDestination.ACCESSORIES_OVERVIEW to 1)
				} else {
					emptyMap()
				},
			),
			isLaunchComplete = isLaunchComplete,
		)
	}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = MainActivityUiState.Loading,
		)

	init {
		if (featureFlagsClient.isEnabled(FeatureFlag.ACHIEVEMENTS)) {
			viewModelScope.launch {
				achievementsRepository.getLatestAchievement(Clock.System.now())
					.filterNotNull()
					.collect { latestAchievement ->
						analyticsClient.trackEvent(AchievementEarned(latestAchievement.title))

						// TODO: Add a dialog to display the achievement
					}
			}
		}
	}

	fun didFirstLaunch() {
		if (isLaunchComplete.value) return

		viewModelScope.launch {
			analyticsClient.initialize()
			analyticsClient.trackEvent(AppLaunched)
		}

		viewModelScope.launch {
			val userData = userDataRepository.userData.first()
			if (userData.isOnboardingComplete) {
				gamesRepository.lockStaleGames()
			}
		}

		isLaunchComplete.value = true
	}

	fun didChangeTab(destination: TopLevelDestination) {
		analyticsClient.trackEvent(AppTabSwitched(destination.name))
	}
}
