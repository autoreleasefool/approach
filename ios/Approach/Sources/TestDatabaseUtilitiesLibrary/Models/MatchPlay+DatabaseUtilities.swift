import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
#if DEBUG
import ModelsLibrary

extension MatchPlay.Database {
	public static func mock(
		gameId: Game.ID = UUID(0),
		id: MatchPlay.ID,
		opponentId: Bowler.ID? = nil,
		opponentScore: Int = 123,
		result: MatchPlay.Result? = nil
	) -> Self {
		.init(gameId: gameId, id: id, opponentId: opponentId, opponentScore: opponentScore, result: result)
	}
}

func insert(
	matchPlays initial: InitialValue<MatchPlay.Database>?,
	into db: Database
) throws {
	let matchPlays: [MatchPlay.Database]
	switch initial {
	case .none, .zero:
		matchPlays = []
	case .default:
		matchPlays = [
			.init(gameId: UUID(0), id: UUID(0), opponentId: UUID(1), opponentScore: 300, result: .lost),
			.init(gameId: UUID(1), id: UUID(1), opponentId: UUID(1), opponentScore: 255, result: nil),
			.init(gameId: UUID(2), id: UUID(2), opponentId: UUID(2), opponentScore: 123, result: .won),
		]
	case let .custom(custom):
		matchPlays = custom
	}

	for matchPlay in matchPlays {
		try matchPlay.insert(db)
	}
}
#endif
