import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

func insert(
	matchPlays initial: InitialValue<MatchPlay.Database>?,
	into db: Database
) throws {
	let matchPlays: [MatchPlay.Database]
	switch initial {
	case .none:
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
