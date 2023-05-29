import IdentifiedCollections
import ModelsLibrary
import StringsLibrary

public enum Statistics {}

public protocol Statistic: Identifiable, Equatable {
	static var title: String { get }
	static var category: StatisticCategory { get }

	var value: String { get }
	var category: StatisticCategory { get }
	var trackedValue: TrackedValue { get }

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

	public var title: String {
		switch self {
		case .overall: return Strings.Statistics.Categories.Overall.title
		case .onFirstRoll: return Strings.Statistics.Categories.OnFirstRoll.title
		case .fouls: return Strings.Statistics.Categories.Fouls.title
		case .pinsLeftOnDeck: return Strings.Statistics.Categories.PinsLeftOnDeck.title
		case .matchPlayResults: return Strings.Statistics.Categories.MatchPlay.title
		case .average: return Strings.Statistics.Categories.Average.title
		case .series: return Strings.Statistics.Categories.Series.title
		}
	}
}

// MARK: - TrackedValue

public struct TrackedGroup: Identifiable, Equatable {
	public let category: StatisticCategory
	public let values: IdentifiedArrayOf<TrackedValue>

	public var id: StatisticCategory { category }
}

public struct TrackedValue: Identifiable, Equatable {
	public let title: String
	public let value: String

	public var id: String { title }
}

extension Statistic {
	public var trackedValue: TrackedValue {
		.init(title: Self.title, value: value)
	}
}

extension Collection where Element == any Statistic {
	public func trackedValues() -> [TrackedGroup] {
		StatisticCategory.allCases.compactMap { category in
			let matchingStatistics = self.filter { $0.category == category }
			guard !matchingStatistics.isEmpty else { return nil }
			return .init(category: category, values: .init(uniqueElements: matchingStatistics.map(\.trackedValue)))
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
