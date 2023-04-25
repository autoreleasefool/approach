import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

func insert(
	series initial: InitialValue<Series.Database>?,
	into db: Database
) throws {
	let series: [Series.Database]
	switch initial {
	case .none:
		series = []
	case .default:
		series = [
			.init(
				leagueId: UUID(0),
				id: UUID(0),
				date: Date(timeIntervalSince1970: 123_456_000),
				numberOfGames: 1,
				preBowl: .regular,
				excludeFromStatistics: .include,
				alleyId: UUID(0)
			),
			.init(
				leagueId: UUID(0),
				id: UUID(1),
				date: Date(timeIntervalSince1970: 123_457_000),
				numberOfGames: 2,
				preBowl: .regular,
				excludeFromStatistics: .include,
				alleyId: UUID(0)
			),
			.init(
				leagueId: UUID(1),
				id: UUID(2),
				date: Date(timeIntervalSince1970: 123_456_000),
				numberOfGames: 1,
				preBowl: .regular,
				excludeFromStatistics: .include,
				alleyId: UUID(1)
			),
			.init(
				leagueId: UUID(1),
				id: UUID(3),
				date: Date(timeIntervalSince1970: 123_457_000),
				numberOfGames: 2,
				preBowl: .regular,
				excludeFromStatistics: .include,
				alleyId: UUID(1)
			),
		]
	case let .custom(custom):
		series = custom
	}

	for series in series {
		try series.insert(db)
	}
}
