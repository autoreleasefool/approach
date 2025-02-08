package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticListEntryGroup

interface StatisticsRepository {
	suspend fun getSourceDetails(source: TrackableFilter.Source): TrackableFilter.SourceSummaries?
	suspend fun getDefaultSource(): TrackableFilter.SourceSummaries?

	suspend fun getStatisticsList(filter: TrackableFilter): List<StatisticListEntryGroup>
	suspend fun getStatisticsChart(statistic: Statistic, filter: TrackableFilter): StatisticChartContent
}
