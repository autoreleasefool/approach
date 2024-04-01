package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.data.queries.sequence.TrackableFramesSequence
import ca.josephroque.bowlingcompanion.core.data.queries.sequence.TrackableGamesSequence
import ca.josephroque.bowlingcompanion.core.data.queries.sequence.TrackableSeriesSequence
import ca.josephroque.bowlingcompanion.core.data.queries.statistics.buildChartWithEntries
import ca.josephroque.bowlingcompanion.core.data.queries.statistics.buildEntries
import ca.josephroque.bowlingcompanion.core.data.queries.statistics.perFrameConfiguration
import ca.josephroque.bowlingcompanion.core.data.queries.statistics.perGameConfiguration
import ca.josephroque.bowlingcompanion.core.data.queries.statistics.perSeriesConfiguration
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.UserData
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.StatisticCategory
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration
import ca.josephroque.bowlingcompanion.core.statistics.allStatistics
import ca.josephroque.bowlingcompanion.core.statistics.models.ChartEntryKey
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticListEntry
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticListEntryGroup
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class OfflineFirstStatisticsRepository @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	private val userDataRepository: UserDataRepository,
	private val statisticsDao: StatisticsDao,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : StatisticsRepository {
	override suspend fun getSourceDetails(
		source: TrackableFilter.Source,
	): TrackableFilter.SourceSummaries = withContext(ioDispatcher) {
		when (source) {
			is TrackableFilter.Source.Bowler -> statisticsDao.getBowlerSourceDetails(source.id)
			is TrackableFilter.Source.League -> statisticsDao.getLeagueSourceDetails(source.id)
			is TrackableFilter.Source.Series -> statisticsDao.getSeriesSourceDetails(source.id)
			is TrackableFilter.Source.Game -> statisticsDao.getGameSourceDetails(source.id)
		}.asModel()
	}

	override suspend fun getDefaultSource(): TrackableFilter.SourceSummaries? = withContext(
		ioDispatcher,
	) {
		val bowlers = bowlersRepository.getBowlersList().first()
		if (bowlers.size != 1) return@withContext null

		TrackableFilter.SourceSummaries(
			bowlers.first().asSummary(),
			league = null,
			series = null,
			game = null,
		)
	}

	override suspend fun getStatisticsList(filter: TrackableFilter): List<StatisticListEntryGroup> =
		withContext(
			ioDispatcher,
		) {
			val statistics = allStatistics(filter.source)
			val userData = userDataRepository.userData.first()

			val perSeriesConfiguration = userData.perSeriesConfiguration()
			TrackableSeriesSequence(filter, statisticsDao)
				.applySequence { series ->
					statistics.forEach {
						it.adjustBySeries(series, perSeriesConfiguration)
					}
				}

			val perGameConfiguration = userData.perGameConfiguration()
			TrackableGamesSequence(filter, statisticsDao)
				.applySequence { game -> statistics.forEach { it.adjustByGame(game, perGameConfiguration) } }

			val perFrameConfiguration = userData.perFrameConfiguration()
			TrackableFramesSequence(filter, statisticsDao)
				.applySequence { frame ->
					statistics.forEach {
						it.adjustByFrame(
							frame,
							perFrameConfiguration,
						)
					}
				}

			statisticsAsListEntries(statistics, userData)
		}

	private fun statisticsAsListEntries(
		statistics: List<Statistic>,
		userData: UserData,
	): List<StatisticListEntryGroup> {
		val frameConfiguration = userData.perFrameConfiguration()
		val isHidingZeroStatistics = !userData.isShowingZeroStatistics
		val isShowingStatisticDescriptions = !userData.isHidingStatisticDescriptions

		return StatisticCategory.entries.mapNotNull { category ->
			val categoryStatistics = statistics
				.filter { it.category == category }
				.filter { !isHidingZeroStatistics || !it.isEmpty }

			if (categoryStatistics.isEmpty()) return@mapNotNull null
			StatisticListEntryGroup(
				title = category.titleResourceId,
				description = if (isShowingStatisticDescriptions) {
					category.description(
						frameConfiguration,
					)
				} else {
					null
				},
				images = emptyList(),
				entries = categoryStatistics.map {
					StatisticListEntry(
						id = it.id,
						description = null,
						value = it.formattedValue,
						valueDescription = it.formattedValueDescription,
						isHighlightedAsNew = it.isEligibleForNewLabel && !userData.hasSeenStatistic(it.id),
					)
				},
			)
		}
	}

	override suspend fun getStatisticsChart(
		statistic: Statistic,
		filter: TrackableFilter,
	): StatisticChartContent {
		if (!statistic.supportsSource(filter.source)) {
			return StatisticChartContent.ChartUnavailable(statistic.id)
		}

		val userData = userDataRepository.userData.first()

		val chartContent = when (filter.source) {
			is TrackableFilter.Source.Bowler,
			is TrackableFilter.Source.League,
			is TrackableFilter.Source.Game,
			-> {
				val entries = buildEntries(
					statistic = statistic,
					filter = filter,
					statisticsDao = statisticsDao,
					userData = userData,
					createEntryKeyFromSeries = { ChartEntryKey.Date(it.date, 0) },
					createEntryKeyFromGame = { ChartEntryKey.Date(it.date, 0) },
					createEntryKeyFromFrame = { ChartEntryKey.Date(it.date, 0) },
				)
				buildChartWithEntries(entries, statistic, filter.aggregation)
			}
			is TrackableFilter.Source.Series -> {
				val entries = buildEntries(
					statistic = statistic,
					filter = filter,
					statisticsDao = statisticsDao,
					userData = userData,
					createEntryKeyFromSeries = { ChartEntryKey.Game(0) },
					createEntryKeyFromGame = { ChartEntryKey.Game(it.index) },
					createEntryKeyFromFrame = { ChartEntryKey.Game(it.gameIndex) },
				)
				buildChartWithEntries(entries, statistic, filter.aggregation)
			}
		}

		return when (chartContent) {
			is StatisticChartContent.CountableChart -> if (chartContent.data.isEmpty) {
				StatisticChartContent.DataMissing(statistic.id)
			} else {
				chartContent
			}
			is StatisticChartContent.AveragingChart -> if (chartContent.data.isEmpty) {
				StatisticChartContent.DataMissing(statistic.id)
			} else {
				chartContent
			}
			is StatisticChartContent.PercentageChart -> if (chartContent.data.isEmpty) {
				StatisticChartContent.DataMissing(statistic.id)
			} else {
				chartContent
			}
			is StatisticChartContent.ChartUnavailable -> chartContent
			is StatisticChartContent.DataMissing -> chartContent
		}
	}
}

private fun StatisticCategory.description(
	frameConfiguration: TrackablePerFrameConfiguration,
): Int? {
	return when (this) {
		StatisticCategory.OVERALL -> null
		StatisticCategory.MIDDLE_HITS -> R.string.statistic_category_middle_hits_description
		StatisticCategory.STRIKES_AND_SPARES -> R.string.statistic_category_strikes_and_spares_description
		StatisticCategory.FIRST_ROLL -> R.string.statistic_category_first_roll_description
		StatisticCategory.HEAD_PINS -> if (frameConfiguration.countHeadPin2AsHeadPin) {
			R.string.statistic_category_head_pins_description_with_H2
		} else {
			R.string.statistic_category_head_pins_description_without_H2
		}
		StatisticCategory.FIVES -> R.string.statistic_category_fives_description
		StatisticCategory.THREES -> R.string.statistic_category_threes_description
		StatisticCategory.ACES -> R.string.statistic_category_aces_description
		StatisticCategory.CHOPS -> R.string.statistic_category_chops_descriptions
		StatisticCategory.SPLITS -> if (frameConfiguration.countSplitWithBonusAsSplit) {
			R.string.statistic_category_splits_description_with_bonus
		} else {
			R.string.statistic_category_splits_description_without_bonus
		}
		StatisticCategory.TAPS -> R.string.statistic_category_taps_description
		StatisticCategory.TWELVES -> R.string.statistic_category_twelves_description
		StatisticCategory.FOULS -> R.string.statistic_category_fouls_description
		StatisticCategory.PINS_LEFT_ON_DECK -> null
		StatisticCategory.MATCH_PLAY_RESULTS -> null
		StatisticCategory.SERIES -> null
	}
}
