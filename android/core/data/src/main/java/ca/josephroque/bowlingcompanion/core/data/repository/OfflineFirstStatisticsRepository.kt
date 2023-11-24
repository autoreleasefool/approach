package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.data.queries.sequence.TrackableFramesSequence
import ca.josephroque.bowlingcompanion.core.data.queries.sequence.TrackableGamesSequence
import ca.josephroque.bowlingcompanion.core.data.queries.sequence.TrackableSeriesSequence
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.model.UserData
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSeriesConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.allStatistics
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticListEntry
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticListEntryGroup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineFirstStatisticsRepository @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	private val userDataRepository: UserDataRepository,
	private val statisticsDao: StatisticsDao,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): StatisticsRepository {
	override suspend fun getSourceDetails(source: TrackableFilter.Source): TrackableFilter.SourceSummaries =
		when (source) {
			is TrackableFilter.Source.Bowler -> statisticsDao.getBowlerSourceDetails(source.id)
			is TrackableFilter.Source.League -> statisticsDao.getLeagueSourceDetails(source.id)
			is TrackableFilter.Source.Series -> statisticsDao.getSeriesSourceDetails(source.id)
			is TrackableFilter.Source.Game -> statisticsDao.getGameSourceDetails(source.id)
		}.asModel()

	override suspend fun getDefaultSource(): TrackableFilter.SourceSummaries? {
		val bowlers = bowlersRepository.getBowlersList().first()
		if (bowlers.size != 1) return null
		return TrackableFilter.SourceSummaries(
			bowlers.first().asSummary(),
			league = null,
			series = null,
			game = null,
		)
	}

	override suspend fun getStatisticsList(filter: TrackableFilter): List<StatisticListEntryGroup> = withContext(ioDispatcher) {
		val statistics = allStatistics(filter.source)
		val userData = userDataRepository.userData.first()

		TrackableSeriesSequence(filter, statisticsDao, perSeriesConfiguration())
			.applyToStatistics(statistics)

		TrackableGamesSequence(filter, statisticsDao, perGameConfiguration())
			.applyToStatistics(statistics)

		TrackableFramesSequence(filter, statisticsDao, userData.perFrameConfiguration())
			.applyToStatistics(statistics)

		statisticsAsListEntries(statistics)
	}

	private suspend fun statisticsAsListEntries(statistics: List<Statistic>): List<StatisticListEntryGroup> {
		val userData = userDataRepository.userData.first()
		val isHidingZeroStatistics = !userData.isShowingZeroStatistics
//		TODO: val isShowingStatisticDescriptions

		return StatisticCategory.values().mapNotNull { category ->
			val categoryStatistics = statistics
				.filter { it.category == category }
				.filter { !isHidingZeroStatistics || !it.isEmpty }

			if (categoryStatistics.isEmpty()) return@mapNotNull null
			StatisticListEntryGroup(
				title = category.titleResourceId,
				description = null, // TODO: Showing Statistics Descriptions
				images = emptyList(),
				entries = categoryStatistics.map {
					StatisticListEntry(
						title = it.titleResourceId,
						description = null, // TODO: Showing Statistics Descriptions
						value = it.formattedValue,
						isHighlightedAsNew = false, // TODO: use isEligibleForNewLabel && isSeenKey
					)
				}
			)
		}
	}
}

fun UserData.perFrameConfiguration(): TrackablePerFrameConfiguration = TrackablePerFrameConfiguration(
	countHeadPin2AsHeadPin = !isCountingH2AsHDisabled,
	countSplitWithBonusAsSplit = !isCountingSplitWithBonusAsSplitDisabled
)

fun perGameConfiguration(): TrackablePerGameConfiguration = TrackablePerGameConfiguration

fun perSeriesConfiguration(): TrackablePerSeriesConfiguration = TrackablePerSeriesConfiguration