import AssetsLibrary
import StatisticsLibrary
import StringsLibrary
import UIKit

protocol DescribableStatistic {
	static var pinDescription: String { get }
}

extension Statistics.Fouls: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.fouls }
}

extension Statistics.GameAverage: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.gameAverage }
}

extension Statistics.HighSeriesOf3: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.highSeriesOf3 }
}

extension Statistics.HighSingle: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.highSingle }
}

extension Statistics.MatchesPlayed: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.matchesPlayed }
}

extension Statistics.MatchesWon: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.matchesWon }
}

extension Statistics.MatchesLost: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.matchesLost }
}

extension Statistics.MatchesTied: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.matchesTied }
}

extension Statistics.StrikeMiddleHits: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.strikeMiddleHits }
}

extension Statistics.SpareConversions: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.spareChances }
}

extension Statistics.TotalRolls: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.totalRolls }
}

extension Statistics.NumberOfGames: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.numberOfGames }
}

extension Statistics.TotalPinfall: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.totalPinfall }
}

extension Statistics.TotalPinsLeftOnDeck: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.totalPinsLeftOnDeck }
}

extension Statistics.AveragePinsLeftOnDeck: DescribableStatistic {
	static var pinDescription: String { Strings.Statistics.Description.averagePinsLeftOnDeck }
}

extension Statistics.FirstRollAverage: DescribableStatistic {
	static var pinDescription: String {
		Strings.Statistics.Description.firstRollAverage
	}
}
