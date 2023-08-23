import Foundation
import ModelsLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary

extension StatisticsWidget.Source {
	var trackableSource: TrackableFilter.Source {
		switch self {
		case let .bowler(id): return .bowler(id)
		case let .league(id): return .league(id)
		}
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
