import ModelsLibrary
import StringsLibrary

extension StatisticsWidget {
	public struct Sources: Equatable, Sendable {
		public let bowler: Bowler.Summary
		public let league: League.Summary?

		public init(bowler: Bowler.Summary, league: League.Summary?) {
			self.bowler = bowler
			self.league = league
		}

		public static let placeholder = Sources(
			bowler: Bowler.Summary(id: Bowler.ID(), name: ""),
			league: nil
		)
	}
}

extension StatisticsWidget.Timeline: CustomStringConvertible {
	public var description: String {
		switch self {
		case .past1Month: Strings.Widget.Timeline.past1Month
		case .past3Months: Strings.Widget.Timeline.past3Months
		case .past6Months: Strings.Widget.Timeline.past6Months
		case .pastYear: Strings.Widget.Timeline.pastYear
		case .allTime: Strings.Widget.Timeline.allTime
		}
	}
}
