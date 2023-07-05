import Dependencies
import Foundation
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface

struct ChartBuilder {
	static let maxTimePeriods: Double = 20
	static let minPeriodTimeInterval: TimeInterval = 604800

	let uuid: UUIDGenerator
	let aggregation: TrackableFilter.Aggregation

	func buildChart(
		withEntries allEntries: [Date: Statistic],
		forStatistic statistic: Statistic.Type
	) -> Statistics.ChartContent? {
		guard !allEntries.allSatisfy(\.value.isEmpty) else { return .dataMissing(statistic: statistic.title) }

		let sortedEntries = allEntries.sorted { $0.key < $1.key }
		guard let firstEntry = sortedEntries.first, let lastEntry = sortedEntries.last else {
			return .dataMissing(statistic: statistic.title)
		}

		let timePeriod = max(
			(lastEntry.key.timeIntervalSince1970 - firstEntry.key.timeIntervalSince1970) / Self.maxTimePeriods,
			Self.minPeriodTimeInterval
		)

		var entries: [Date: Statistic] = [:]
		var period = firstEntry.key.addingTimeInterval(timePeriod)

		for (date, entry) in sortedEntries {
			if date > period {
				let nextPeriod = period.addingTimeInterval(timePeriod)

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

		return buildRelevantChartData(from: entries, timePeriod: timePeriod)
	}

	private func buildRelevantChartData(
		from entries: [Date: Statistic],
		timePeriod: TimeInterval
	) -> Statistics.ChartContent? {
		guard let firstStatistic = entries.first?.value else { return nil }
		let statisticType = type(of: firstStatistic)
		if statisticType is AveragingStatistic.Type {
			return .averaging(.init(
				title: statisticType.title,
				entries: entries.sortedByDate()
					.compactMap(as: AveragingStatistic.self)
					.map { .init(id: uuid(), value: $1.average, date: $0) }
			))
		} else if statisticType is CountingStatistic.Type {
			return .counting(.init(
				title: statisticType.title,
				entries: entries.sortedByDate()
					.compactMap(as: CountingStatistic.self)
					.map { .init(id: uuid(), value: $1.count, date: $0, timeRange: timePeriod) },
				isAccumulating: aggregation == .accumulate
			))
		} else if statisticType is HighestOfStatistic.Type {
			return .counting(.init(
				title: statisticType.title,
				entries: entries.sortedByDate()
					.compactMap(as: HighestOfStatistic.self)
					.map { .init(id: uuid(), value: $1.highest, date: $0, timeRange: timePeriod) },
				isAccumulating: aggregation == .accumulate
			))
		} else if statisticType is PercentageStatistic.Type {
			return .percentage(.init(
				title: statisticType.title,
				entries: entries.sortedByDate()
					.compactMap(as: PercentageStatistic.self)
					.map { .init(id: uuid(), numerator: $1.numerator, denominator: $1.denominator, date: $0, timeRange: timePeriod) },
				isAccumulating: aggregation == .accumulate
			))
		}

		return nil
	}
}

extension Collection where Element == Dictionary<Date, Statistic>.Element {
	func sortedByDate() -> [Element] {
		sorted(by: { $0.key < $1.key })
	}

	func compactMap<T>(as: T.Type) -> [(Date, T)] {
		self.compactMap { date, statistic in
			guard let t = statistic as? T else { return nil }
			return (date, t)
		}
	}
}
