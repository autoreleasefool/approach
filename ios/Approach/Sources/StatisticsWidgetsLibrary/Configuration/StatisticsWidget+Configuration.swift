import Foundation
import ModelsLibrary
import StringsLibrary

extension StatisticsWidget {
	public struct Configuration: Equatable {
		public let source: Source
		public let timeline: Timeline
		public let statistic: Statistic

		public init(source: Source, timeline: Timeline, statistic: Statistic) {
			self.source = source
			self.timeline = timeline
			self.statistic = statistic
		}
	}
}

// MARK: - Timeline

extension StatisticsWidget.Configuration {
	public enum Timeline: String, Identifiable, CaseIterable, CustomStringConvertible {
		case past1Month
		case past3Months
		case past6Months
		case pastYear
		case allTime

		public var id: String { rawValue }

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

// MARK: - Source

extension StatisticsWidget.Configuration {
	public enum Source: Equatable {
		case bowler(Bowler.ID)
		case league(League.ID)
	}
}

extension StatisticsWidget.Configuration {
	public struct Sources: Equatable {
		public let bowler: Bowler.Summary
		public let league: League.Summary?

		public init(bowler: Bowler.Summary, league: League.Summary?) {
			self.bowler = bowler
			self.league = league
		}
	}
}

// MARK: - Statistic

extension StatisticsWidget.Configuration {
	public enum Statistic: String, Identifiable, CaseIterable, CustomStringConvertible {
		case average
		case middleHits
		case averagePinsLeftOnDeck

		public var id: String { rawValue }
		public var description: String {
			switch self {
			case .average: return Strings.Statistics.Title.gameAverage
			case .middleHits: return Strings.Statistics.Title.middleHits
			case .averagePinsLeftOnDeck: return Strings.Statistics.Title.averagePinsLeftOnDeck
			}
		}
	}
}
