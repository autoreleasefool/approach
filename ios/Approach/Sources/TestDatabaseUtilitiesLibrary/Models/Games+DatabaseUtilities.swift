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
	case .none:
		games = []
	case .default:
		games = [
			.init(
				seriesId: UUID(0),
				id: UUID(0),
				ordinal: 1,
				locked: .open,
				manualScore: nil,
				excludeFromStatistics: .include
			),
			.init(
				seriesId: UUID(1),
				id: UUID(1),
				ordinal: 1,
				locked: .open,
				manualScore: nil,
				excludeFromStatistics: .include
			),
			.init(
				seriesId: UUID(1),
				id: UUID(2),
				ordinal: 2,
				locked: .open,
				manualScore: nil,
				excludeFromStatistics: .include
			),
			.init(
				seriesId: UUID(2),
				id: UUID(3),
				ordinal: 1,
				locked: .open,
				manualScore: nil,
				excludeFromStatistics: .include
			),
			.init(
				seriesId: UUID(3),
				id: UUID(4),
				ordinal: 1,
				locked: .open,
				manualScore: nil,
				excludeFromStatistics: .include
			),
			.init(
				seriesId: UUID(3),
				id: UUID(5),
				ordinal: 2,
				locked: .open,
				manualScore: nil,
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
