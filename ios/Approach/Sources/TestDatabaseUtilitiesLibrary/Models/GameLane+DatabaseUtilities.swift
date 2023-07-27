import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

#if DEBUG
func insert(
	gameLanes initial: InitialValue<GameLane.Database>?,
	into db: Database
) throws {
	let gameLanes: [GameLane.Database]
	switch initial {
	case .none, .zero:
		gameLanes = []
	case .default:
		gameLanes = [
			.init(gameId: UUID(0), laneId: UUID(0)),
			.init(gameId: UUID(0), laneId: UUID(1)),
			.init(gameId: UUID(1), laneId: UUID(1)),
			.init(gameId: UUID(1), laneId: UUID(2)),
			.init(gameId: UUID(2), laneId: UUID(3)),
			.init(gameId: UUID(2), laneId: UUID(4)),
			.init(gameId: UUID(2), laneId: UUID(5)),
		]
	case let .custom(custom):
		gameLanes = custom
	}

	for lane in gameLanes {
		try lane.insert(db)
	}
}
#endif
