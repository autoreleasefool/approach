import Foundation
import IdentifiedCollections
import ModelsLibrary
import StringsLibrary

public enum Statistics {}

public protocol Statistic: Identifiable, Equatable {
	static var title: String { get }
	static var category: StatisticCategory { get }

	var value: String { get }
	var category: StatisticCategory { get }
	var staticValue: StaticValue { get }
	var isEmpty: Bool { get }

	init()

	static func supports(trackableSource: TrackableFilter.Source) -> Bool
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

// MARK: - StaticValue

public struct StaticValueGroup: Identifiable, Equatable {
	public let category: StatisticCategory
	public let values: IdentifiedArrayOf<StaticValue>

	public var id: StatisticCategory { category }
}

public struct StaticValue: Identifiable, Equatable {
	public let title: String
	public let value: String
	public let isGraphable: Bool

	public var id: String { title }
}

extension Statistic {
	public var staticValue: StaticValue {
		.init(title: Self.title, value: value, isGraphable: self is (any GraphableStatistic))
	}
}

extension Collection where Element == any Statistic {
	public func staticValueGroups() -> [StaticValueGroup] {
		StatisticCategory.allCases.compactMap { category in
			let matchingStatistics = self.filter { $0.category == category }
			guard !matchingStatistics.isEmpty else { return nil }
			return .init(category: category, values: .init(uniqueElements: matchingStatistics.map(\.staticValue)))
		}
	}
}

extension Collection where Element == StaticValueGroup {
	public func firstGraphableStatistic() -> (any GraphableStatistic.Type)? {
		for element in self {
			for value in element.values {
				guard let graphable = Statistics.type(fromId: value.id) as? (any GraphableStatistic.Type) else { continue }
				return graphable
			}
		}

		return nil
	}
}

// MARK: - Graphable

public struct TrackedValue: Equatable {
	public let value: Int

	public init(_ value: Int) {
		self.value = value
	}
}

public struct ChartEntry: Identifiable, Equatable {
	public let id: UUID
	public let value: TrackedValue
	public let date: Date

	public init(id: UUID, value: TrackedValue, date: Date) {
		self.id = id
		self.value = value
		self.date = date
	}
}

public protocol GraphableStatistic: Statistic {
	var trackedValue: TrackedValue { get }
	mutating func accumulate(by: any GraphableStatistic)
}

extension GraphableStatistic {
	public var trackedValue: TrackedValue {
		.init(Int(value) ?? 0)
	}
}

extension Collection where Element == any GraphableStatistic {
	public func trackedValues() -> [TrackedValue] {
		self.map(\.trackedValue)
	}
}

// MARK: - Trackable

public protocol TrackablePerFrame: Statistic {
	mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration)
}

public protocol GraphablePerFrame: TrackablePerFrame, GraphableStatistic {}

public struct TrackablePerFrameConfiguration {
	public let countHeadPin2AsHeadPin: Bool

	public init(countHeadPin2AsHeadPin: Bool) {
		self.countHeadPin2AsHeadPin = countHeadPin2AsHeadPin
	}
}

public protocol TrackablePerGame: Statistic {
	mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration)
}

public protocol GraphablePerGame: TrackablePerGame, GraphableStatistic {}

public struct TrackablePerGameConfiguration {
	public init() {}
}

public protocol TrackablePerSeries: Statistic {
	mutating func adjust(bySeries: Series.TrackableEntry, configuration: TrackablePerSeriesConfiguration)
}

public protocol GraphablePerSeries: TrackablePerSeries, GraphableStatistic {}

public struct TrackablePerSeriesConfiguration {
	public init() {}
}
