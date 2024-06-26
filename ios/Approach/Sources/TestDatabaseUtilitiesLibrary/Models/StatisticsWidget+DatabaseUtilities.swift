import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import GRDBDatabaseTestUtilitiesPackageLibrary
import ModelsLibrary

extension StatisticsWidget.Database {
	public static func mock(
		id: Bowler.ID,
		created: Date,
		bowlerId: Bowler.ID? = UUID(0),
		leagueId: League.ID? = nil,
		timeline: StatisticsWidget.Timeline = .past3Months,
		statistic: String = "Average",
		context: String = "context",
		priority: Int
	) -> Self {
		.init(id: id, created: created, bowlerId: bowlerId, leagueId: leagueId, timeline: timeline, statistic: statistic, context: context, priority: priority)
	}
}

func insert(
	statisticsWidgets initial: InitialValue<StatisticsWidget.Database>?,
	into db: Database
) throws {
	let statisticsWidgets: [StatisticsWidget.Database]
	switch initial {
	case .none, .zero:
		statisticsWidgets = []
	case .default:
		statisticsWidgets = [
			.init(id: UUID(0), created: Date(timeIntervalSince1970: 123), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average", context: "bowlersList", priority: 1),
			.init(id: UUID(1), created: Date(timeIntervalSince1970: 123), bowlerId: UUID(0), leagueId: nil, timeline: .pastYear, statistic: "Average", context: "bowlersList", priority: 2),
		]
	case let .custom(custom):
		statisticsWidgets = custom
	}

	for statisticWidget in statisticsWidgets {
		try statisticWidget.insert(db)
	}
}
