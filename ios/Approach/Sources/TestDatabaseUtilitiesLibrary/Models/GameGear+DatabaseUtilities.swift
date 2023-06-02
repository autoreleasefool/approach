import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

func insert(
	gameGear initial: InitialValue<GameGear.Database>?,
	into db: Database
) throws {
	let gameGear: [GameGear.Database]
	switch initial {
	case .none, .zero:
		gameGear = []
	case .default:
		gameGear = [
			.init(gameId: UUID(0), gearId: UUID(2)),
			.init(gameId: UUID(0), gearId: UUID(5)),
			.init(gameId: UUID(1), gearId: UUID(2)),
			.init(gameId: UUID(1), gearId: UUID(5)),
			.init(gameId: UUID(2), gearId: UUID(2)),
			.init(gameId: UUID(3), gearId: UUID(2)),
			.init(gameId: UUID(3), gearId: UUID(5)),
			.init(gameId: UUID(4), gearId: UUID(2)),
			.init(gameId: UUID(4), gearId: UUID(5)),
			.init(gameId: UUID(5), gearId: UUID(5)),
		]
	case let .custom(custom):
		gameGear = custom
	}

	for gear in gameGear {
		try gear.insert(db)
	}
}
