package ca.josephroque.bowlingcompanion.core.statistics.charts.stub

import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.models.ChartEntryKey
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartData
import ca.josephroque.bowlingcompanion.core.statistics.models.CountableChartEntry
import kotlinx.datetime.LocalDate

object CountableChartDataStub {
	fun stub(): CountableChartData = CountableChartData(
		id = StatisticID.TOTAL_ROLLS,
		isAccumulating = false,
		entries = listOf(
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2018-10-03"), days=24), value=655),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2018-10-27"), days=24), value=1258),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2018-11-20"), days=24), value=1922),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2018-12-14"), days=24), value=2605),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-01-07"), days=24), value=2876),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-01-31"), days=24), value=3600),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-02-24"), days=24), value=4164),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-03-20"), days=24), value=4805),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-04-13"), days=24), value=5333),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-05-07"), days=24), value=5461),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-05-31"), days=24), value=5522),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-06-24"), days=24), value=5584),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-07-18"), days=24), value=5735),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-08-11"), days=24), value=5799),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-09-04"), days=24), value=5862),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-09-28"), days=24), value=5931),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-10-22"), days=24), value=6562),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-11-15"), days=24), value=6935),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2019-12-09"), days=24), value=7413),
			CountableChartEntry(key= ChartEntryKey.Date(LocalDate.parse("2020-01-02"), days=24), value=7685),
		)
	)
}