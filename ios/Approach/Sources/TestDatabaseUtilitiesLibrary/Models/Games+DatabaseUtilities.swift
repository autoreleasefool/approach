import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
@testable import ModelsLibrary

extension Game.Database {
	public static func mock(
		seriesId: Series.ID = UUID(0),
		id: ID,
		index: Int,
		score: Int = 0,
		locked: Game.Lock = .open,
		scoringMethod: Game.ScoringMethod = .byFrame,
		excludeFromStatistics: Game.ExcludeFromStatistics = .include
	) -> Self {
		.init(
			seriesId: seriesId,
			id: id,
			index: index,
			score: score,
			locked: locked,
			scoringMethod: scoringMethod,
			excludeFromStatistics: excludeFromStatistics
		)
	}
}

extension Game.Summary {
	public init(_ from: Game.Database) {
		self.init(
			id: from.id,
			index: from.index,
			score: from.score,
			scoringMethod: from.scoringMethod
		)
	}
}

func insert(
	games initial: InitialValue<Game.Database>?,
	into db: Database
) throws {
	let games: [Game.Database]
	switch initial {
	case .none, .zero:
		games = []
	case .default:
		games = [
			.mock(seriesId: UUID(0), id: UUID(0), index: 0),
			.mock(seriesId: UUID(1), id: UUID(1), index: 0),
			.mock(seriesId: UUID(1), id: UUID(2), index: 1),
			.mock(seriesId: UUID(2), id: UUID(3), index: 0),
			.mock(seriesId: UUID(3), id: UUID(4), index: 0),
			.mock(seriesId: UUID(3), id: UUID(5), index: 1),
		]
	case let .custom(custom):
		games = custom
	}

	for game in games {
		try game.insert(db)
	}
}
