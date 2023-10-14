package ca.josephroque.bowlingcompanion.core.datastore

import androidx.datastore.core.DataStore
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsOptInProto
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.core.model.UserData
import ca.josephroque.bowlingcompanion.core.model.UserPreferences
import ca.josephroque.bowlingcompanion.core.model.copy
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ApproachPreferencesDataSource @Inject constructor(
	private val userPreferences: DataStore<UserPreferences>,
) {
	val userData = userPreferences.data
		.map {
			UserData(
				isOnboardingComplete = it.isOnboardingComplete,
				isLegacyMigrationComplete = it.isLegacyMigrationComplete,
				analyticsOptIn = when (it.analyticsOptIn) {
					AnalyticsOptInProto.ANALYTICS_OPT_IN_OPTED_IN,
					AnalyticsOptInProto.UNRECOGNIZED,
					null -> AnalyticsOptInStatus.OPTED_IN
					AnalyticsOptInProto.ANALYTICS_OPT_IN_OPTED_OUT -> AnalyticsOptInStatus.OPTED_OUT
				},
				isCountingH2AsHDisabled = it.isCountingH2AsHDisabled,
				isCountingSplitWithBonusAsSplitDisabled = it.isCountingSplitWithBonusAsSplitDisabled,
				isShowingZeroStatistics = it.isShowingZeroStatistics,
				isHidingWidgetsInBowlersList = it.isHidingWidgetsInBowlersList,
				isHidingWidgetsInLeaguesList = it.isHidingWidgetsInLeaguesList,
			)
		}

	suspend fun setOnboardingComplete(isOnboardingComplete: Boolean) {
		userPreferences.updateData {
			it.copy {
				this.isOnboardingComplete = isOnboardingComplete
			}
		}
	}

	suspend fun setLegacyMigrationComplete(isLegacyMigrationComplete: Boolean) {
		userPreferences.updateData {
			it.copy {
				this.isLegacyMigrationComplete = isLegacyMigrationComplete
			}
		}
	}

	suspend fun setAnalyticsOptInStatus(status: AnalyticsOptInStatus) {
		userPreferences.updateData {
			it.copy {
				this.analyticsOptIn = when (status) {
					AnalyticsOptInStatus.OPTED_IN -> AnalyticsOptInProto.ANALYTICS_OPT_IN_OPTED_IN
					AnalyticsOptInStatus.OPTED_OUT -> AnalyticsOptInProto.ANALYTICS_OPT_IN_OPTED_OUT
				}
			}
		}
	}
}