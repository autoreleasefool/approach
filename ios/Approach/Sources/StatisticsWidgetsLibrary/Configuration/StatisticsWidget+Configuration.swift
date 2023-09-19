import ModelsLibrary
import StringsLibrary

extension StatisticsWidget {
	public struct Sources: Equatable {
		public let bowler: Bowler.Summary
		public let league: League.Summary?

		public init(bowler: Bowler.Summary, league: League.Summary?) {
			self.bowler = bowler
			self.league = league
		}
	}
}

extension StatisticsWidget.Timeline: CustomStringConvertible {
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
