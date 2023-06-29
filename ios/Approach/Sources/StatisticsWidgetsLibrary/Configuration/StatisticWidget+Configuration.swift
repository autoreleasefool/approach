import StringsLibrary

extension StatisticWidget {
	public struct Configuration: Equatable {
		public let timeline: Timeline

		public init(timeline: Timeline) {
			self.timeline = timeline
		}
	}
}

extension StatisticWidget.Configuration {
	public enum Timeline: CustomStringConvertible {
		case past1Month
		case past3Months
		case past6Months
		case pastYear
		case allTime

		public var description: String {
			switch self {
			case .past1Month: return Strings.Widget.Timeline.past1Month
			case .past3Months: return Strings.Widget.Timeline.past3Months
			case .past6Months: return Strings.Widget.Timeline.past6Months
			case .pastYear: return Strings.Widget.Timeline.pastYear
			case .allTime: return Strings.Widget.Timeline.allTime
			}
		}
	}
}
