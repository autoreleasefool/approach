import Foundation
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary

struct EntryDate: ChartEntryKey {
	static let maxTimePeriods: Double = 20
	static let weekTimeInterval: TimeInterval = 604800
	static let dayTimeInterval: TimeInterval = 86400

	let date: Date

	static func < (lhs: EntryDate, rhs: EntryDate) -> Bool {
		lhs.date < rhs.date
	}

	func toAveragingChartXAxis() -> AveragingChart.Data.XAxis {
		.date(date)
	}

	func toCountingChartXAxis(withTimeInterval: TimeInterval) -> CountingChart.Data.XAxis {
		.date(date, withTimeInterval)
	}

	func toPercentageChartXAxis(withTimeInterval: TimeInterval) -> PercentageChart.Data.XAxis {
		.date(date, withTimeInterval)
	}

	static func extractKey(from series: Series.TrackableEntry) -> EntryDate? {
		.init(date: series.date)
	}

	static func extractKey(from game: Game.TrackableEntry) -> EntryDate? {
		.init(date: game.date)
	}

	static func extractKey(from frame: Frame.TrackableEntry) -> EntryDate? {
		.init(date: frame.date)
	}

	static func accumulate(
		entries allEntries: [EntryDate: Statistic],
		aggregation: TrackableFilter.Aggregation
	) -> ([EntryDate: Statistic], TimeInterval)? {
		let sortedEntries = allEntries.sorted { $0.key < $1.key }
		guard let firstEntry = sortedEntries.first, let lastEntry = sortedEntries.last else {
			return nil
		}

		let timePeriod: TimeInterval
		let timeBetweenStartAndEnd = lastEntry.key.date.timeIntervalSince1970 - firstEntry.key.date.timeIntervalSince1970

		if timeBetweenStartAndEnd > Self.weekTimeInterval {
			timePeriod = max(
				timeBetweenStartAndEnd / Self.maxTimePeriods,
				Self.weekTimeInterval
			)
		} else {
			timePeriod = Self.dayTimeInterval
		}

		var entries: [EntryDate: Statistic] = [:]
		var period = EntryDate(date: firstEntry.key.date.addingTimeInterval(timePeriod))

		for (date, entry) in sortedEntries {
			if date > period {
				let nextPeriod = EntryDate(date: period.date.addingTimeInterval(timePeriod))

				switch aggregation {
				case .accumulate:
					entries[nextPeriod] = entries[period]
				case .periodic:
					entries[nextPeriod] = nil
				}

				period = nextPeriod
			}

			if entries[period] == nil {
				entries[period] = entry
			} else {
				entries[period]?.aggregate(with: entry)
			}
		}

		return (entries, timePeriod)
	}
}
