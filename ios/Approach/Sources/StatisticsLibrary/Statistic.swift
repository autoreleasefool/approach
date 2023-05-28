import ModelsLibrary

public enum Statistics {}

public protocol Statistic: Identifiable, Equatable {
	static var title: String { get }
	static var category: StatisticCategory { get }

	var value: String { get }
	var category: StatisticCategory { get }

	init()
}

extension Statistic {
	public var id: String { Self.title }
	public var category: StatisticCategory { Self.category }
}

// MARK: - Category

public enum StatisticCategory: CaseIterable {
	case overall
	case onFirstRoll
	case fouls
	case pinsLeftOnDeck
	case matchPlayResults
	case average
	case series
}

public struct StatisticGroup {
	public let category: StatisticCategory
	public let statistics: [any Statistic]
}

extension Collection where Element == any Statistic {
	public func grouped() -> [StatisticGroup] {
		StatisticCategory.allCases.compactMap { category in
			let matchingStatistics = self.filter { $0.category == category }
			guard !matchingStatistics.isEmpty else { return nil }
			return .init(category: category, statistics: matchingStatistics)
		}
	}
}

// MARK: - Graphable

public protocol GraphableStatistic {}

// MARK: - Trackable

public protocol TrackablePerFrame: Statistic {
	mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration)
}

public struct TrackablePerFrameConfiguration {
	public let countHeadPin2AsHeadPin: Bool

	public init(countHeadPin2AsHeadPin: Bool) {
		self.countHeadPin2AsHeadPin = countHeadPin2AsHeadPin
	}
}

public protocol TrackablePerGame: Statistic {
	mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration)
}

public struct TrackablePerGameConfiguration {
	public init() {}
}

public protocol TrackablePerSeries: Statistic {
	mutating func adjust(bySeries: Series.TrackableEntry, configuration: TrackablePerSeriesConfiguration)
}

public struct TrackablePerSeriesConfiguration {
	public init() {}
}
