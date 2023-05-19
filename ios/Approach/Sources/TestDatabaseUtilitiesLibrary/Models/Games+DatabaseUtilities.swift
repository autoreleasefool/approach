import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

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
			.init(
				seriesId: UUID(0),
				id: UUID(0),
				index: 0,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include
			),
			.init(
				seriesId: UUID(1),
				id: UUID(1),
				index: 0,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include
			),
			.init(
				seriesId: UUID(1),
				id: UUID(2),
				index: 1,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include
			),
			.init(
				seriesId: UUID(2),
				id: UUID(3),
				index: 0,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include
			),
			.init(
				seriesId: UUID(3),
				id: UUID(4),
				index: 0,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include
			),
			.init(
				seriesId: UUID(3),
				id: UUID(5),
				index: 1,
				score: 0,
				locked: .open,
				scoringMethod: .byFrame,
				excludeFromStatistics: .include
			),
		]
	case let .custom(custom):
		games = custom
	}

	for game in games {
		try game.insert(db)
	}
}
