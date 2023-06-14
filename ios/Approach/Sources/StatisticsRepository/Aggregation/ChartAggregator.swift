import Dependencies
import Foundation
import StatisticsLibrary

struct ChartAggregator {
	static let maxAggregateTimePeriods: Double = 25
	static let minPeriodTimeInterval: TimeInterval = 604800

	let uuid: UUIDGenerator
	let aggregation: TrackableFilter.Aggregation

	func accumulate(entries allEntries: [Date: any GraphableStatistic]) -> [ChartEntry] {
		let entries: [Date: any GraphableStatistic]
		switch aggregation {
		case .accumulate:
			let sortedEntries = allEntries.sorted { $0.key < $1.key }
			guard let firstEntry = sortedEntries.first, let lastEntry = sortedEntries.last else { return [] }

			let timePeriod =
				(lastEntry.key.timeIntervalSince1970 - firstEntry.key.timeIntervalSince1970) / Self.maxAggregateTimePeriods
			var aggregateEntries: [Date: any GraphableStatistic] = [:]

			// If the length of a period equates to less than 1 week, just use all entries instead
			if timePeriod < Self.minPeriodTimeInterval {
				var accumulator = firstEntry.value
				aggregateEntries[firstEntry.key] = accumulator
				for (date, entry) in sortedEntries.dropFirst() {
					accumulator.accumulate(by: entry)
					aggregateEntries[date] = accumulator
				}
			} else {
				var aggregateDate = firstEntry.key.addingTimeInterval(timePeriod)
				for (date, entry) in sortedEntries {
					if date > aggregateDate {
						let nextAggregateDate = aggregateDate.addingTimeInterval(timePeriod)
						aggregateEntries[nextAggregateDate] = aggregateEntries[aggregateDate]
						aggregateDate = nextAggregateDate
					}

					if aggregateEntries[aggregateDate] == nil {
						aggregateEntries[aggregateDate] = entry
					} else {
						aggregateEntries[aggregateDate]?.accumulate(by: entry)
					}
				}
			}

			entries = aggregateEntries
		case .periodic:
			entries = allEntries
		}

		return entries.sorted(by: { $0.key < $1.key })
			.map { .init(id: uuid(), value: $0.value.trackedValue, date: $0.key) }
	}
}
