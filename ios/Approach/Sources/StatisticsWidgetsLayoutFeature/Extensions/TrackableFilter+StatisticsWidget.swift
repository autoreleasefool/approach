import Foundation
import ModelsLibrary
import StatisticsLibrary

extension TrackableFilter {
	init?(
		widget: StatisticsWidget.Configuration,
		relativeToDate: Date,
		inCalendar calendar: Calendar
	) {
		guard let source = widget.source else { return nil }
		self.init(
			source: .init(from: source),
			seriesFilter: .init(from: widget.timeline, relativeTo: relativeToDate, in: calendar)
		)
	}
}

extension TrackableFilter.Source {
	init(from: StatisticsWidget.Source) {
		switch from {
		case let .bowler(id):
			self = .bowler(id)
		case let .league(id):
			self = .league(id)
		}
	}
}

extension TrackableFilter.SeriesFilter {
	init(from: StatisticsWidget.Timeline, relativeTo: Date, in calendar: Calendar) {
		self.init(
			startDate: from.startDate(relativeTo: relativeTo, in: calendar),
			endDate: nil,
			alley: nil
		)
	}
}
