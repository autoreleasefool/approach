import Foundation
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary

struct EntryGameOrdinal: ChartEntryKey {
	let ordinal: Int

	static func < (lhs: EntryGameOrdinal, rhs: EntryGameOrdinal) -> Bool {
		lhs.ordinal < rhs.ordinal
	}

	func toAveragingChartXAxis() -> AveragingChart.Data.XAxis {
		.game(ordinal: ordinal)
	}

	func toCountingChartXAxis(withTimeInterval _: TimeInterval) -> CountingChart.Data.XAxis {
		.game(ordinal: ordinal)
	}

	func toPercentageChartXAxis(withTimeInterval _: TimeInterval) -> PercentageChart.Data.XAxis {
		.game(ordinal: ordinal)
	}

	static func extractKey(from _: Series.TrackableEntry) -> EntryGameOrdinal? {
		nil
	}

	static func extractKey(from game: Game.TrackableEntry) -> EntryGameOrdinal? {
		.init(ordinal: game.index + 1)
	}

	static func extractKey(from frame: Frame.TrackableEntry) -> EntryGameOrdinal? {
		.init(ordinal: frame.gameIndex + 1)
	}

	static func accumulate(
		entries allEntries: [EntryGameOrdinal: Statistic],
		aggregation: TrackableFilter.Aggregation
	) -> ([EntryGameOrdinal: Statistic], TimeInterval)? {
		let sortedEntries = allEntries.sorted { $0.key < $1.key }
		guard let firstEntry = sortedEntries.first else {
			return nil
		}

		var entries: [EntryGameOrdinal: Statistic] = [:]
		var currentOrdinal = EntryGameOrdinal(ordinal: firstEntry.key.ordinal)

		for (ordinal, entry) in sortedEntries {
			if ordinal > currentOrdinal {
				let nextOrdinal = EntryGameOrdinal(ordinal: ordinal.ordinal)

				switch aggregation {
				case .accumulate:
					entries[nextOrdinal] = entries[currentOrdinal]
				case .periodic:
					entries[nextOrdinal] = nil
				}

				currentOrdinal = nextOrdinal
			}

			if entries[currentOrdinal] == nil {
				entries[currentOrdinal] = entry
			} else {
				entries[currentOrdinal]?.aggregate(with: entry)
			}
		}

		return (entries, 0)
	}
}
