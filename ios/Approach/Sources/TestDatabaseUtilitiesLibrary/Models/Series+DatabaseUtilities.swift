import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
@testable import ModelsLibrary

extension Series.Database {
	public static func mock(
		leagueId: League.ID = UUID(0),
		id: ID,
		date: Date,
		numberOfGames: Int = 3,
		preBowl: Series.PreBowl = .regular,
		excludeFromStatistics: Series.ExcludeFromStatistics = .include,
		alleyId: Alley.ID? = nil
	) -> Self {
		.init(
			leagueId: leagueId,
			id: id,
			date: date,
			numberOfGames: numberOfGames,
			preBowl: preBowl,
			excludeFromStatistics: excludeFromStatistics,
			alleyId: alleyId
		)
	}
}

extension Series.Summary {
	public init(_ from: Series.Database) {
		self.init(
			id: from.id,
			date: from.date
		)
	}
}

func insert(
	series initial: InitialValue<Series.Database>?,
	into db: Database
) throws {
	let series: [Series.Database]
	switch initial {
	case .none, .zero:
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

	try series.forEach { try $0.insert(db) }
}
