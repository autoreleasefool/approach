import Foundation
import ModelsLibrary
import StringsLibrary

public enum Statistics {}

public protocol Statistic {
	var title: String { get }
	var category: StatisticCategory { get }

	var formattedValue: String { get }
	var isEmpty: Bool { get }

	init()

	static func supports(trackableSource: TrackableFilter.Source) -> Bool
}

// MARK: - Category

public enum StatisticCategory: CaseIterable, CustomStringConvertible {
	case overall
	case onFirstRoll
	case fouls
	case pinsLeftOnDeck
	case matchPlayResults
	case average
	case series

	public var description: String {
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
