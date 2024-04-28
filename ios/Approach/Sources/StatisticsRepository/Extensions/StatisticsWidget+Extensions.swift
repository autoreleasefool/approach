import Foundation
import ModelsLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary

extension StatisticsWidget.Configuration {
	func trackableFilter(relativeTo: Date, in calendar: Calendar) -> TrackableFilter? {
		guard let source else { return nil }

		return .init(
			source: source.trackableSource,
			seriesFilter: .init(
				startDate: timeline.startDate(relativeTo: relativeTo, in: calendar),
				endDate: relativeTo
			)
		)
	}
}
