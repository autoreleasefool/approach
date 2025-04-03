package ca.josephroque.bowlingcompanion.feature.announcements.provider

import ca.josephroque.bowlingcompanion.core.common.system.SystemInfoService
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.feature.announcements.ui.AnnouncementUiState
import ca.josephroque.bowlingcompanion.feature.announcements.ui.tenyears.TenYearsAnnouncementUiState
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class LiveAnnouncementsProvider @Inject constructor(
	private val featureFlagsClient: FeatureFlagsClient,
	private val systemInfoService: SystemInfoService,
	private val userDataRepository: UserDataRepository,
) : AnnouncementsProvider {

	override suspend fun getNextAnnouncement(): AnnouncementUiState? {
		return when {
			shouldShowTenYearsAnnouncement() -> AnnouncementUiState.TenYears(TenYearsAnnouncementUiState)
			else -> null
		}
	}

	private suspend fun shouldShowTenYearsAnnouncement(): Boolean {
		val userData = userDataRepository.userData.first()

		val isDateAfterApril1 = Clock.System.now() > Instant.fromEpochMilliseconds(1_743_480_000)
		val isNotDismissed = !userData.isTenYearsAnnouncementDismissed
		val isFeatureEnabled = featureFlagsClient.isEnabled(FeatureFlag.ACHIEVEMENTS)
		val isOneWeekSinceFirstInstall = Clock.System.now() >= (systemInfoService.firstInstallTime + 7.days)

		return isDateAfterApril1 && isNotDismissed && isFeatureEnabled && isOneWeekSinceFirstInstall
	}
}
