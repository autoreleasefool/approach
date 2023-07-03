import Foundation
import ModelsLibrary
import StatisticsWidgetsLibrary

extension StatisticsWidget {
	public struct Create: Identifiable, Codable, Equatable {
		public let id: StatisticsWidget.ID
		public var created: Date
		public var source: StatisticsWidget.Source
		public var timeline: StatisticsWidget.Timeline
		public var statistic: StatisticsWidget.Statistic
		public var context: String
		public var priority: Int
	}
}

extension StatisticsWidget.Configuration {
	public func make(on created: Date, context: String, priority: Int) -> StatisticsWidget.Create {
		.init(
			id: id,
			created: created,
			source: source,
			timeline: timeline,
			statistic: statistic,
			context: context,
			priority: priority
		)
	}
}
