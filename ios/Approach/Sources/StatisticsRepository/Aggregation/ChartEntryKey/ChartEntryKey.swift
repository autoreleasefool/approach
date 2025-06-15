import Foundation
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary

protocol ChartEntryKey: Hashable, Comparable, Sendable {
	func toAveragingChartXAxis() -> AveragingChart.Data.XAxis
	func toCountingChartXAxis(withTimeInterval: TimeInterval) -> CountingChart.Data.XAxis
	func toPercentageChartXAxis(withTimeInterval: TimeInterval) -> PercentageChart.Data.XAxis

	static func extractKey(from series: Series.TrackableEntry) -> Self?
	static func extractKey(from game: Game.TrackableEntry) -> Self?
	static func extractKey(from frame: Frame.TrackableEntry) -> Self?

	static func accumulate(
		entries: [Self: Statistic],
		aggregation: TrackableFilter.Aggregation
	) -> ([Self: Statistic], TimeInterval)?
}

// MARK: - Extensions

extension Dictionary where Key: ChartEntryKey, Value == Statistic {
	func sortedByKey() -> [Element] {
		sorted(by: { $0.key < $1.key })
	}
}

extension Array {
	func compactMap<Key, Value, T>(as _: T.Type) -> [(Key, T)] where Element == Dictionary<Key, Value>.Element {
		self.compactMap { id, statistic in
			guard let t = statistic as? T else { return nil }
			return (id, t)
		}
	}
}
