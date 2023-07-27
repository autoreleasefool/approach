import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
#if DEBUG
import ModelsLibrary

extension StatisticsWidget.Database {
	public static func mock(
		id: Bowler.ID,
		created: Date,
		source: StatisticsWidget.Source = .bowler(UUID(0)),
		timeline: StatisticsWidget.Timeline = .past3Months,
		statistic: StatisticsWidget.Statistic = .average,
		context: String = "context",
		priority: Int
	) -> Self {
		.init(id: id, created: created, source: source, timeline: timeline, statistic: statistic, context: context, priority: priority)
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
			.init(id: UUID(0), created: Date(timeIntervalSince1970: 123), source: .bowler(UUID(0)), timeline: .past3Months, statistic: .average, context: "bowlersList", priority: 1),
			.init(id: UUID(1), created: Date(timeIntervalSince1970: 123), source: .bowler(UUID(0)), timeline: .pastYear, statistic: .average, context: "bowlersList", priority: 2),
		]
	case let .custom(custom):
		statisticsWidgets = custom
	}

	for statisticWidget in statisticsWidgets {
		try statisticWidget.insert(db)
	}
}
#endif
