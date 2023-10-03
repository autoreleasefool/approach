import Dependencies
import Foundation
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface

struct ChartBuilder {
	let uuid: UUIDGenerator
	let aggregation: TrackableFilter.Aggregation

	func buildChart<Key: ChartEntryKey>(
		withEntries allEntries: [Key: Statistic]?,
		forStatistic statistic: Statistic.Type
	) -> Statistics.ChartContent? {
		guard let allEntries else { return nil }

		guard !allEntries.allSatisfy(\.value.isEmpty) else {
			return .dataMissing(statistic: statistic.title)
		}

		guard let (entries, timePeriod) = Key.accumulate(entries: allEntries, aggregation: aggregation) else {
			return .dataMissing(statistic: statistic.title)
		}

		return buildRelevantChartData(from: entries, timePeriod: timePeriod)
	}

	private func buildRelevantChartData<Key: ChartEntryKey>(
		from entries: [Key: Statistic],
		timePeriod: TimeInterval
	) -> Statistics.ChartContent? {
		guard let firstStatistic = entries.first?.value else { return nil }
		let statisticType = type(of: firstStatistic)
		if statisticType is AveragingStatistic.Type {
			return .averaging(.init(
				title: statisticType.title,
				entries: entries.sortedByKey()
					.compactMap(as: AveragingStatistic.self)
					.map { .init(id: uuid(), value: $1.average, xAxis: $0.toAveragingChartXAxis()) },
				preferredTrendDirection: statisticType.preferredTrendDirection
			))
		} else if statisticType is CountingStatistic.Type {
			return .counting(.init(
				title: statisticType.title,
				entries: entries.sortedByKey()
					.compactMap(as: CountingStatistic.self)
			 		.map { .init(id: uuid(), value: $1.count, xAxis: $0.toCountingChartXAxis(withTimeInterval: timePeriod)) },
				isAccumulating: aggregation == .accumulate
			))
		} else if statisticType is HighestOfStatistic.Type {
			return .counting(.init(
				title: statisticType.title,
				entries: entries.sortedByKey()
					.compactMap(as: HighestOfStatistic.self)
					.map { .init(id: uuid(), value: $1.highest, xAxis: $0.toCountingChartXAxis(withTimeInterval: timePeriod)) },
				isAccumulating: aggregation == .accumulate
			))
		} else if statisticType is PercentageStatistic.Type {
			return .percentage(.init(
				title: statisticType.title,
				entries: entries.sortedByKey()
					.compactMap(as: PercentageStatistic.self)
					.map {
						.init(
							id: uuid(),
							numerator: $1.numerator,
							denominator: $1.denominator,
							xAxis: $0.toPercentageChartXAxis(withTimeInterval: timePeriod)
						)
					},
				isAccumulating: aggregation == .accumulate,
				preferredTrendDirection: statisticType.preferredTrendDirection
			))
		}

		return nil
	}
}
