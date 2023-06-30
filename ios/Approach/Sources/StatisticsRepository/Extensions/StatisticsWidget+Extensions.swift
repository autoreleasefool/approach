import Foundation
import StatisticsLibrary
import StatisticsWidgetsLibrary

extension StatisticsWidget.Configuration.Statistic {
	var type: Statistic.Type {
		switch self {
		case .average:
			return Statistics.GameAverage.self
		case .middleHits:
			return Statistics.MiddleHits.self
		case .averagePinsLeftOnDeck:
			return Statistics.AveragePinsLeftOnDeck.self
		}
	}
}

extension StatisticsWidget.Configuration.Source {
	var trackableSource: TrackableFilter.Source {
		switch self {
		case let .bowler(id): return .bowler(id)
		case let .league(id): return .league(id)
		}
	}
}

extension StatisticsWidget.Configuration.Timeline {
	func startDate(relativeTo: Date, in calendar: Calendar) -> Date? {
		var date: Date?
		switch self {
		case .allTime: date = nil
		case .past1Month: date = calendar.date(byAdding: .month, value: -1, to: relativeTo)
		case .past3Months: date = calendar.date(byAdding: .month, value: -3, to: relativeTo)
		case .past6Months: date = calendar.date(byAdding: .month, value: -6, to: relativeTo)
		case .pastYear: date = calendar.date(byAdding: .year, value: -1, to: relativeTo)
		}

		guard let date else { return nil }
		return calendar.startOfDay(for: date)
	}
}

extension StatisticsWidget.Configuration {
	func trackableFilter(relativeTo: Date, in calendar: Calendar) -> TrackableFilter {
		.init(
			source: source.trackableSource,
			seriesFilter: .init(
				startDate: timeline.startDate(relativeTo: relativeTo, in: calendar),
				endDate: relativeTo
			)
		)
	}
}
