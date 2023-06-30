import StatisticsLibrary
import StatisticsWidgetsLibrary

extension StatisticsWidget.Configuration.Statistic {
	var type: Statistic.Type {
		switch self {
		case .average:
			return Statistics.GameAverage.self
		case .middleHits:
			return Statistics.MiddleHits.self
		case .averagePinsLeftOnDeck:
			return Statistics.AveragePinsLeftOnDeck.self
		}
	}
}
