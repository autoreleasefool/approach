import Foundation
import ModelsLibrary
import StringsLibrary

public enum Statistics {}

public protocol Statistic: Sendable {
	static var title: String { get }
	static var category: StatisticCategory { get }
	static var isEligibleForNewLabel: Bool { get }
	static var supportsAggregation: Bool { get }
	static var supportsWidgets: Bool { get }
	static var preferredTrendDirection: StatisticTrendDirection? { get }

	var formattedValue: String { get }
	var formattedValueDescription: String? { get }
	var isEmpty: Bool { get }

	init()

	static func supports(trackableSource: TrackableFilter.Source) -> Bool

	mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration)
	mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration)
	mutating func adjust(bySeries: Series.TrackableEntry, configuration: TrackablePerSeriesConfiguration)
	mutating func aggregate(with: Statistic)
}

// MARK: - Category

public enum StatisticCategory: CaseIterable, CustomStringConvertible {
	case overall
	case middleHits
	case strikesAndSpares
	case firstRoll
	case headPins
	case fives
	case threes
	case aces
	case chopOffs
	case splits
	case taps
	case twelves
	case fouls
	case pinsLeftOnDeck
	case matchPlayResults
	case series

	public var description: String {
		switch self {
		case .overall: Strings.Statistics.Categories.Overall.title
		case .middleHits: Strings.Statistics.Categories.MiddleHits.title
		case .strikesAndSpares: Strings.Statistics.Categories.StrikesAndSpares.title
		case .firstRoll: Strings.Statistics.Categories.FirstRoll.title
		case .headPins: Strings.Statistics.Categories.HeadPins.title
		case .fives: Strings.Statistics.Categories.Fives.title
		case .threes: Strings.Statistics.Categories.Threes.title
		case .aces: Strings.Statistics.Categories.Aces.title
		case .chopOffs: Strings.Statistics.Categories.ChopOffs.title
		case .splits: Strings.Statistics.Categories.Splits.title
		case .taps: Strings.Statistics.Categories.Taps.title
		case .twelves: Strings.Statistics.Categories.Twelves.title
		case .fouls: Strings.Statistics.Categories.Fouls.title
		case .pinsLeftOnDeck: Strings.Statistics.Categories.PinsLeftOnDeck.title
		case .matchPlayResults: Strings.Statistics.Categories.MatchPlay.title
		case .series: Strings.Statistics.Categories.Series.title
		}
	}
}

// MARK: - Trend

public enum StatisticTrendDirection: Sendable {
	case upwards
	case downwards
}

// MARK: - Trackable Per Frame

public struct TrackablePerFrameConfiguration {
	public let countHeadPin2AsHeadPin: Bool
	public let countSplitWithBonusAsSplit: Bool

	public init(countHeadPin2AsHeadPin: Bool, countSplitWithBonusAsSplit: Bool) {
		self.countHeadPin2AsHeadPin = countHeadPin2AsHeadPin
		self.countSplitWithBonusAsSplit = countSplitWithBonusAsSplit
	}
}

public protocol TrackablePerFrame: Statistic {}
extension TrackablePerFrame {
	public mutating func adjust(byGame _: Game.TrackableEntry, configuration _: TrackablePerGameConfiguration) {}
	public mutating func adjust(bySeries _: Series.TrackableEntry, configuration _: TrackablePerSeriesConfiguration) {}
}

// MARK: Trackable Per First Roll

public protocol TrackablePerFirstRoll: TrackablePerFrame {
	mutating func adjust(byFirstRoll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration)
}

extension TrackablePerFirstRoll {
	public mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration) {
		for roll in byFrame.firstRolls {
			adjust(byFirstRoll: roll, configuration: configuration)
		}
	}
}

// MARK: Trackable Per Second Roll

public protocol TrackablePerSecondRoll: TrackablePerFrame {
	mutating func adjust(
		bySecondRoll: Frame.OrderedRoll,
		afterFirstRoll: Frame.OrderedRoll,
		configuration: TrackablePerFrameConfiguration
	)
}

extension TrackablePerSecondRoll {
	public mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration) {
		for rollPair in byFrame.rollPairs {
			adjust(bySecondRoll: rollPair.secondRoll, afterFirstRoll: rollPair.firstRoll, configuration: configuration)
		}
	}
}

// MARK: - Trackable Per Game

public struct TrackablePerGameConfiguration {
	public init() {}
}

public protocol TrackablePerGame: Statistic {}
extension TrackablePerGame {
	public mutating func adjust(byFrame _: Frame.TrackableEntry, configuration _: TrackablePerFrameConfiguration) {}
	public mutating func adjust(bySeries _: Series.TrackableEntry, configuration _: TrackablePerSeriesConfiguration) {}
}

// MARK: - Trackable Per Series

public struct TrackablePerSeriesConfiguration {
	public init() {}
}

public protocol TrackablePerSeries: Statistic {}
extension TrackablePerSeries {
	public mutating func adjust(byFrame _: Frame.TrackableEntry, configuration _: TrackablePerFrameConfiguration) {}
	public mutating func adjust(byGame _: Game.TrackableEntry, configuration _: TrackablePerGameConfiguration) {}
}
