package ca.josephroque.bowlingcompanion.core.data.queries.statistics

import ca.josephroque.bowlingcompanion.core.data.queries.sequence.TrackableFramesSequence
import ca.josephroque.bowlingcompanion.core.data.queries.sequence.TrackableGamesSequence
import ca.josephroque.bowlingcompanion.core.data.queries.sequence.TrackableSeriesSequence
import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
import ca.josephroque.bowlingcompanion.core.model.UserData
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerGame
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSeries
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.AveragingStatistic
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.CountingStatistic
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.HighestOfStatistic
import ca.josephroque.bowlingcompanion.core.statistics.interfaces.PercentageStatistic
import ca.josephroque.bowlingcompanion.core.statistics.models.AveragingChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.AveragingChartEntry
import ca.josephroque.bowlingcompanion.core.statistics.models.ChartEntryKey
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartEntry
import ca.josephroque.bowlingcompanion.core.statistics.models.PercentageChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.PercentageChartEntry
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

private const val MAX_TIME_PERIODS = 20

suspend fun <Key: ChartEntryKey> buildEntries(
	statistic: Statistic,
	filter: TrackableFilter,
	statisticsDao: StatisticsDao,
	userData: UserData,
	createEntryKeyFromSeries: (TrackableSeries) -> Key,
	createEntryKeyFromGame: (TrackableGame) -> Key,
	createEntryKeyFromFrame: (TrackableFrame) -> Key,
): Map<Key, Statistic> {
	val entries = mutableMapOf<Key, Statistic>()

	if (statistic is TrackablePerSeries) {
		val perSeriesConfiguration = userData.perSeriesConfiguration()
		TrackableSeriesSequence(filter, statisticsDao)
			.applySequence {
				val key = createEntryKeyFromSeries(it)
				if (!entries.containsKey(key)) {
					entries[key] = statistic.emptyClone()
				}

				entries[key]!!.adjustBySeries(it, perSeriesConfiguration)
			}
	}

	if (statistic is TrackablePerGame) {
		val perGameConfiguration = userData.perGameConfiguration()
		TrackableGamesSequence(filter, statisticsDao)
			.applySequence {
				val key = createEntryKeyFromGame(it)
				if (!entries.containsKey(key)) {
					entries[key] = statistic.emptyClone()
				}

				entries[key]!!.adjustByGame(it, perGameConfiguration)
			}
	}

	if (statistic is TrackablePerFrame) {
		val perFrameConfiguration = userData.perFrameConfiguration()
		TrackableFramesSequence(filter, statisticsDao)
			.applySequence {
				val key = createEntryKeyFromFrame(it)
				if (!entries.containsKey(key)) {
					entries[key] = statistic.emptyClone()
				}

				entries[key]!!.adjustByFrame(it, perFrameConfiguration)
			}
	}

	return entries
}

private fun aggregateEntriesByDate(
	entries: Map<ChartEntryKey.Date, Statistic>,
	aggregationFilter: TrackableFilter.AggregationFilter,
): Map<ChartEntryKey.Date, Statistic> {
	if (entries.isEmpty()) return emptyMap()
	val sortedEntries = entries.toSortedMap(compareBy { it.date })
	val firstEntry = sortedEntries.firstKey()
	val lastEntry = sortedEntries.lastKey()

	val dayPeriod: Int
	val daysBetweenStartAndEnd = lastEntry.date.toEpochDays() - firstEntry.date.toEpochDays()

	dayPeriod = if (daysBetweenStartAndEnd > 7) {
		maxOf(daysBetweenStartAndEnd / MAX_TIME_PERIODS, 7)
	} else {
		1
	}

	val aggregatedEntries = mutableMapOf<ChartEntryKey.Date, Statistic>()
	var period = ChartEntryKey.Date(
		firstEntry.date.plus(value = dayPeriod, unit = DateTimeUnit.DAY),
		dayPeriod,
	)

	for (entry in sortedEntries.entries) {
		if (entry.key.date > period.date) {
			val nextPeriod = ChartEntryKey.Date(
				period.date.plus(value = dayPeriod, unit = DateTimeUnit.DAY),
				dayPeriod,
			)

			when (aggregationFilter) {
				TrackableFilter.AggregationFilter.ACCUMULATE ->
					aggregatedEntries[nextPeriod] = aggregatedEntries[period]!!.clone()
				TrackableFilter.AggregationFilter.PERIODIC ->
					aggregatedEntries.remove(nextPeriod)
			}

			period = nextPeriod
		}

		if (aggregatedEntries.containsKey(period)) {
			aggregatedEntries[period]!!.aggregateWithStatistic(entry.value)
		} else {
			aggregatedEntries[period] = entry.value
		}
	}

	return aggregatedEntries.toSortedMap(compareBy { it.date })
}

private fun aggregateEntriesByGame(
	entries: Map<ChartEntryKey.Game, Statistic>,
	aggregationFilter: TrackableFilter.AggregationFilter,
): Map<ChartEntryKey.Game, Statistic> {
	if (entries.isEmpty()) return emptyMap()
	val sortedEntries = entries.toSortedMap(compareBy { it.index })
	val firstEntry = sortedEntries.firstKey()

	val aggregatedEntries = mutableMapOf<ChartEntryKey.Game, Statistic>()
	var currentKey = ChartEntryKey.Game(firstEntry.index)

	for (entry in sortedEntries) {
		if (entry.key.index > currentKey.index) {
			val nextKey = ChartEntryKey.Game(entry.key.index)
			when (aggregationFilter) {
				TrackableFilter.AggregationFilter.ACCUMULATE ->
					aggregatedEntries[nextKey] = aggregatedEntries[currentKey]!!.clone()
				TrackableFilter.AggregationFilter.PERIODIC ->
					aggregatedEntries.remove(nextKey)
			}

			currentKey = nextKey
		}

		if (aggregatedEntries.containsKey(currentKey)) {
			aggregatedEntries[currentKey]!!.aggregateWithStatistic(entry.value)
		} else {
			aggregatedEntries[currentKey] = entry.value
		}
	}

	return aggregatedEntries.toSortedMap(compareBy { it.index })
}

fun <Key: ChartEntryKey>buildChartWithEntries(
	entries: Map<Key, Statistic>,
	statistic: Statistic,
	aggregation: TrackableFilter.AggregationFilter,
): StatisticChartContent {
	if (entries.isEmpty() || entries.values.all { it.isEmpty }) {
		return StatisticChartContent.DataMissing(statistic.id)
	}

	@Suppress("UNCHECKED_CAST") val aggregatedEntries = when (entries.keys.first()) {
		is ChartEntryKey.Date -> aggregateEntriesByDate(entries as Map<ChartEntryKey.Date, Statistic>, aggregation)
		is ChartEntryKey.Game -> aggregateEntriesByGame(entries as Map<ChartEntryKey.Game, Statistic>, aggregation)
		else -> throw IllegalStateException("Unsupported chart entry key type: ${entries.keys.first()}")
	}

	when (statistic) {
		is CountingStatistic -> {
			return StatisticChartContent.CountableChart(
				data = CountableChartData(
					id = statistic.id,
					entries = aggregatedEntries.map { (key, entryStatistic) ->
						CountableChartEntry(
							key = key,
							value = (entryStatistic as? CountingStatistic)?.count ?: 0,
						)
					},
					isAccumulating = false,
				),
			)
		}

		is HighestOfStatistic -> {
			return StatisticChartContent.CountableChart(
				data = CountableChartData(
					id = statistic.id,
					entries = aggregatedEntries.map { (key, entryStatistic) ->
						CountableChartEntry(
							key = key,
							value = (entryStatistic as? HighestOfStatistic)?.highest ?: 0,
						)
					},
					isAccumulating = aggregation == TrackableFilter.AggregationFilter.ACCUMULATE,
				),
			)
		}

		is AveragingStatistic -> {
			return StatisticChartContent.AveragingChart(
				data = AveragingChartData(
					id = statistic.id,
					entries = aggregatedEntries.map { (key, entryStatistic) ->
						AveragingChartEntry(
							key = key,
							value = (entryStatistic as? AveragingStatistic)?.average ?: 0.0,
						)
					},
					preferredTrendDirection = statistic.preferredTrendDirection,
				),
			)
		}

		is PercentageStatistic -> {
			return StatisticChartContent.PercentageChart(
				data = PercentageChartData(
					id = statistic.id,
					isAccumulating = aggregation == TrackableFilter.AggregationFilter.ACCUMULATE,
					preferredTrendDirection = statistic.preferredTrendDirection,
					entries = aggregatedEntries
						.map { (key, entryStatistic) -> PercentageChartEntry(
							key = key,
							numerator = (entryStatistic as? PercentageStatistic)?.numerator ?: 0,
							denominator = (entryStatistic as? PercentageStatistic)?.denominator ?: 0,
							percentage = (entryStatistic as? PercentageStatistic)?.percentage ?: 0.0,
						)
					},
				),
			)
		}
	}

	return StatisticChartContent.DataMissing(statistic.id)
}