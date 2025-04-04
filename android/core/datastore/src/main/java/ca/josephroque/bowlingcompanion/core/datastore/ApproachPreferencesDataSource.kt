package ca.josephroque.bowlingcompanion.core.datastore

import androidx.datastore.core.DataStore
import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.UserData
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.map

const val RECENTLY_USED_LIMIT = 10

class ApproachPreferencesDataSource @Inject constructor(
	private val userPreferences: DataStore<UserPreferences>,
) {
	val userData = userPreferences.data
		.map {
			UserData(
				isOnboardingComplete = it.isOnboardingComplete,
				isOpponentMigrationComplete = it.isOpponentMigrationComplete,
				isLegacyMigrationComplete = it.isLegacyMigrationComplete,
				hasOpenedAccessoriesTab = it.hasOpenedAccessoriesTab,
				isTenYearsAnnouncementDismissed = it.isTenYearsAnnouncementDismissed,
				userAnalyticsId =
				if (it.userAnalyticsId.isNullOrBlank()) null else UUID.fromString(it.userAnalyticsId),
				analyticsOptIn = when (it.analyticsOptIn) {
					AnalyticsOptInProto.ANALYTICS_OPT_IN_OPTED_IN,
					AnalyticsOptInProto.UNRECOGNIZED,
					null,
					-> AnalyticsOptInStatus.OPTED_IN
					AnalyticsOptInProto.ANALYTICS_OPT_IN_OPTED_OUT -> AnalyticsOptInStatus.OPTED_OUT
				},
				seriesItemSize = when (it.seriesItemSize) {
					SeriesItemSizeProto.SERIES_ITEM_SIZE_COMPACT -> SeriesItemSize.COMPACT
					SeriesItemSizeProto.SERIES_ITEM_SIZE_DEFAULT,
					SeriesItemSizeProto.UNRECOGNIZED,
					null,
					-> SeriesItemSize.DEFAULT
				},
				isCountingH2AsHDisabled = it.isCountingH2AsHDisabled,
				isCountingSplitWithBonusAsSplitDisabled = it.isCountingSplitWithBonusAsSplitDisabled,
				isShowingZeroStatistics = it.isShowingZeroStatistics,
				isHidingStatisticDescriptions = it.isHidingStatisticDescriptions,
				isHidingWidgetsInBowlersList = it.isHidingWidgetsInBowlersList,
				isHidingWidgetsInLeaguesList = it.isHidingWidgetsInLeaguesList,
				recentlyUsedBowlerIds = it.recentlyUsedBowlerIdsList,
				recentlyUsedLeagueIds = it.recentlyUsedLeagueIdsList,
				recentlyUsedAlleyIds = it.recentlyUsedAlleyIdsList,
				recentlyUsedGearIds = it.recentlyUsedGearIdsList,
				recentlyUsedTeamIds = it.recentlyUsedTeamIdsList,
				isLaneFormSwipeToEditTipDismissed = it.isLaneFormSwipeToEditTipDismissed,
				isQuickPlayTipDismissed = it.isQuickPlayTipDismissed,
				isStatisticsTapToViewChartTipDismissed = it.isStatisticsTapToViewChartTipDismissed,
				isSwipeRowsTipDismissed = it.isSwipeRowsTipDismissed,
				isFrameDragHintDismissed = it.isFrameDragHintDismissed,
				lastTrackableFilter = it.parseTrackableFilterSource(),
				seenStatisticIds = it.seenStatisticsIdsList.toSet(),
				latestSeriesInEditor = it.latestSeriesInEditorList,
				latestGameInEditor = if (it.latestGameInEditor.isNullOrBlank()) null else it.latestGameInEditor,
				latestTeamSeriesInEditor =
				if (it.latestTeamSeriesInEditor.isNullOrBlank()) null else it.latestTeamSeriesInEditor,
				isHidingTeamScoresInGameDetails = it.isHidingTeamScoresInGameDetails,
			)
		}

	suspend fun setOnboardingComplete(isOnboardingComplete: Boolean) {
		userPreferences.updateData {
			it.copy {
				this.isOnboardingComplete = isOnboardingComplete
			}
		}
	}

	suspend fun setOpponentMigrationComplete(isOpponentMigrationComplete: Boolean) {
		userPreferences.updateData {
			it.copy {
				this.isOpponentMigrationComplete = isOpponentMigrationComplete
			}
		}
	}

	suspend fun setHasOpenedAccessoriesTab(hasOpenedAccessoriesTab: Boolean) {
		userPreferences.updateData {
			it.copy {
				this.hasOpenedAccessoriesTab = hasOpenedAccessoriesTab
			}
		}
	}

	suspend fun setTenYearsAnnouncementDismissed(isTenYearsAnnouncementDismissed: Boolean) {
		userPreferences.updateData {
			it.copy {
				this.isTenYearsAnnouncementDismissed = isTenYearsAnnouncementDismissed
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

	suspend fun setSeriesItemSize(size: SeriesItemSize) {
		userPreferences.updateData {
			it.copy {
				this.seriesItemSize = when (size) {
					SeriesItemSize.COMPACT -> SeriesItemSizeProto.SERIES_ITEM_SIZE_COMPACT
					SeriesItemSize.DEFAULT -> SeriesItemSizeProto.SERIES_ITEM_SIZE_DEFAULT
				}
			}
		}
	}

	suspend fun setUserAnalyticsID(id: UUID) {
		userPreferences.updateData {
			it.copy { this.userAnalyticsId = id.toString() }
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

	suspend fun setIsCountingH2AsH(enabled: Boolean) {
		userPreferences.updateData {
			it.copy { this.isCountingH2AsHDisabled = !enabled }
		}
	}

	suspend fun setIsCountingSplitWithBonusAsSplit(enabled: Boolean) {
		userPreferences.updateData {
			it.copy { this.isCountingSplitWithBonusAsSplitDisabled = !enabled }
		}
	}

	suspend fun setIsHidingZeroStatistics(isHiding: Boolean) {
		userPreferences.updateData {
			it.copy { this.isShowingZeroStatistics = !isHiding }
		}
	}

	suspend fun setIsHidingStatisticDescriptions(isHiding: Boolean) {
		userPreferences.updateData {
			it.copy { this.isHidingStatisticDescriptions = isHiding }
		}
	}

	suspend fun setIsHidingWidgetsInBowlersList(isHiding: Boolean) {
		userPreferences.updateData {
			it.copy { this.isHidingWidgetsInBowlersList = isHiding }
		}
	}

	suspend fun setIsHidingWidgetsInLeaguesList(isHiding: Boolean) {
		userPreferences.updateData {
			it.copy { this.isHidingWidgetsInLeaguesList = isHiding }
		}
	}

	suspend fun setStatisticsIdsSeen(id: String) {
		userPreferences.updateData {
			val seenStatistics = it.seenStatisticsIdsList
				.toMutableList()
				.replaceOrInsert(id)

			it.toBuilder()
				.clearSeenStatisticsIds()
				.addAllSeenStatisticsIds(seenStatistics)
				.build()
		}
	}

	suspend fun setLastTrackableFilterSource(source: TrackableFilter.Source?) {
		userPreferences.updateData {
			it.copy {
				when (source) {
					null -> {
						this.trackableFilterSource = TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_NONE
						this.trackableFilterSourceId = ""
					}
					is TrackableFilter.Source.Team -> {
						this.trackableFilterSource = TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_TEAM
						this.trackableFilterSourceId = source.id.toString()
					}
					is TrackableFilter.Source.Bowler -> {
						this.trackableFilterSource = TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_BOWLER
						this.trackableFilterSourceId = source.id.toString()
					}
					is TrackableFilter.Source.League -> {
						this.trackableFilterSource = TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_LEAGUE
						this.trackableFilterSourceId = source.id.toString()
					}
					is TrackableFilter.Source.Series -> {
						this.trackableFilterSource = TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_SERIES
						this.trackableFilterSourceId = source.id.toString()
					}
					is TrackableFilter.Source.Game -> {
						this.trackableFilterSource = TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_GAME
						this.trackableFilterSourceId = source.id.toString()
					}
				}
			}
		}
	}

	suspend fun setIsHidingTeamScoresInGameDetails(isHiding: Boolean) {
		userPreferences.updateData {
			it.copy { this.isHidingTeamScoresInGameDetails = isHiding }
		}
	}

	suspend fun setIsLaneFormSwipeToEditTipDismissed(isDismissed: Boolean) {
		userPreferences.updateData {
			it.copy { this.isLaneFormSwipeToEditTipDismissed = isDismissed }
		}
	}

	suspend fun setIsQuickPlayTipDismissed(isDismissed: Boolean) {
		userPreferences.updateData {
			it.copy { this.isQuickPlayTipDismissed = isDismissed }
		}
	}

	suspend fun setIsStatisticsTapToViewChartTipDismissed(isDismissed: Boolean) {
		userPreferences.updateData {
			it.copy { this.isStatisticsTapToViewChartTipDismissed = isDismissed }
		}
	}

	suspend fun setIsSwipeRowsTipDismissed(isDismissed: Boolean) {
		userPreferences.updateData {
			it.copy { this.isSwipeRowsTipDismissed = isDismissed }
		}
	}

	suspend fun setIsFrameDragHintDismissed(isDismissed: Boolean) {
		userPreferences.updateData {
			it.copy { this.isFrameDragHintDismissed = isDismissed }
		}
	}

	suspend fun insertRecentlyUsedBowler(id: String) {
		userPreferences.updateData {
			val recentBowlers = it.recentlyUsedBowlerIdsList
				.toMutableList()
				.insertAndTrim(id)

			it.toBuilder()
				.clearRecentlyUsedBowlerIds()
				.addAllRecentlyUsedBowlerIds(recentBowlers)
				.build()
		}
	}

	suspend fun insertRecentlyUsedAlley(id: String) {
		userPreferences.updateData {
			val recentAlleys = it.recentlyUsedAlleyIdsList
				.toMutableList()
				.insertAndTrim(id)

			it.toBuilder()
				.clearRecentlyUsedAlleyIds()
				.addAllRecentlyUsedAlleyIds(recentAlleys)
				.build()
		}
	}

	suspend fun insertRecentlyUsedGear(id: String) {
		userPreferences.updateData {
			val recentGear = it.recentlyUsedGearIdsList
				.toMutableList()
				.insertAndTrim(id)

			it.toBuilder()
				.clearRecentlyUsedGearIds()
				.addAllRecentlyUsedGearIds(recentGear)
				.build()
		}
	}

	suspend fun insertRecentlyUsedLeague(id: String) {
		userPreferences.updateData {
			val recentLeagues = it.recentlyUsedLeagueIdsList
				.toMutableList()
				.insertAndTrim(id)

			it.toBuilder()
				.clearRecentlyUsedLeagueIds()
				.addAllRecentlyUsedLeagueIds(recentLeagues)
				.build()
		}
	}

	suspend fun insertRecentlyUsedTeam(id: String) {
		userPreferences.updateData {
			val recentTeams = it.recentlyUsedTeamIdsList
				.toMutableList()
				.insertAndTrim(id)

			it.toBuilder()
				.clearRecentlyUsedTeamIds()
				.addAllRecentlyUsedTeamIds(recentTeams)
				.build()
		}
	}

	suspend fun setLatestSeriesInEditor(ids: List<String>) {
		userPreferences.updateData {
			it.toBuilder()
				.clearLatestSeriesInEditor()
				.addAllLatestSeriesInEditor(ids)
				.build()
		}
	}

	suspend fun setLatestGameInEditor(id: String?) {
		userPreferences.updateData {
			it.copy { this.latestGameInEditor = id ?: "" }
		}
	}

	suspend fun setLatestTeamSeriesInEditor(id: String?) {
		userPreferences.updateData {
			it.copy { this.latestTeamSeriesInEditor = id ?: "" }
		}
	}
}

private fun <T> MutableList<T>.insertAndTrim(id: T): List<T> {
	this.remove(id)
	this.add(0, id)
	return this.take(RECENTLY_USED_LIMIT)
}

private fun <T> MutableList<T>.replaceOrInsert(id: T): List<T> {
	this.remove(id)
	this.add(0, id)
	return this
}

private fun UserPreferences.parseTrackableFilterSource(): TrackableFilter.Source? =
	if (this.trackableFilterSourceId != null && this.trackableFilterSourceId.isNotBlank()) {
		try {
			UUID.fromString(this.trackableFilterSourceId).let {
				when (this.trackableFilterSource) {
					TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_TEAM ->
						TrackableFilter.Source.Team(TeamID(it))
					TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_BOWLER ->
						TrackableFilter.Source.Bowler(BowlerID(it))
					TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_LEAGUE ->
						TrackableFilter.Source.League(LeagueID(it))
					TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_SERIES ->
						TrackableFilter.Source.Series(SeriesID(it))
					TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_GAME ->
						TrackableFilter.Source.Game(GameID(it))
					null,
					TrackableFilterSourceProto.UNRECOGNIZED,
					TrackableFilterSourceProto.TRACKABLE_FILTER_SOURCE_NONE,
					-> null
				}
			}
		} catch (ex: Exception) {
			null
		}
	} else {
		null
	}
