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

extension StatisticsWidget.Timeline {
	public func startDate(relativeTo: Date, in calendar: Calendar) -> Date? {
		var date: Date?
		switch self {
		case .allTime: date = nil
		case .past1Month: date = calendar.date(byAdding: .month, value: -1, to: relativeTo)
		case .past3Months: date = calendar.date(byAdding: .month, value: -3, to: relativeTo)
		case .past6Months: date = calendar.date(byAdding: .month, value: -6, to: relativeTo)
		case .pastYear: date = calendar.date(byAdding: .year, value: -1, to: relativeTo)
		}

		guard let date else { return nil }
		return calendar.startOfDay(for: date)
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
