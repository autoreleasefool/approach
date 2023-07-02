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

		public static func defaultWidget(withId: StatisticsWidget.ID, date: Date, source: Source) -> Self {
			.init(
				id: withId,
				created: date,
				source: source,
				timeline: .past3Months,
				statistic: .average,
				context: "",
				priority: 1
			)
		}
	}
}
