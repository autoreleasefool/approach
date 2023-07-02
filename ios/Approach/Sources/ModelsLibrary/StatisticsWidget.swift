import Foundation

public enum StatisticsWidget {}

extension StatisticsWidget {
	public typealias ID = UUID
}

extension StatisticsWidget {
	public struct Configuration: Equatable, Codable, Identifiable {
		public let id: StatisticsWidget.ID
		public let source: StatisticsWidget.Source
		public let timeline: StatisticsWidget.Timeline
		public let statistic: StatisticsWidget.Statistic

		public init(
			id: StatisticsWidget.ID,
			source: StatisticsWidget.Source,
			timeline: StatisticsWidget.Timeline,
			statistic: StatisticsWidget.Statistic
		) {
			self.id = id
			self.source = source
			self.timeline = timeline
			self.statistic = statistic
		}
	}
}

// MARK: - Timeline

extension StatisticsWidget {
	public enum Timeline: String, Codable, Sendable, Identifiable, CaseIterable {
		case past1Month
		case past3Months
		case past6Months
		case pastYear
		case allTime

		public var id: String { rawValue }
	}
}


// MARK: - Source

extension StatisticsWidget {
	public enum Source: Equatable, Codable, Sendable {
		case bowler(Bowler.ID)
		case league(League.ID)
	}
}

// MARK: - Statistic

extension StatisticsWidget {
	public enum Statistic: String, Codable, Sendable, Identifiable, CaseIterable {
		case average
		case middleHits
		case averagePinsLeftOnDeck

		public var id: String { rawValue }
	}
}
