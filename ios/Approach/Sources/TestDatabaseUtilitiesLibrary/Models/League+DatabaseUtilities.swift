import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

func insert(
	leagues initial: InitialValue<League.Database>?,
	into db: Database
) throws {
	let leagues: [League.Database]
	switch initial {
	case .none, .zero:
		leagues = []
	case .default:
		leagues = [
			.init(
				bowlerId: UUID(0),
				id: UUID(0),
				name: "Majors",
				recurrence: .repeating,
				numberOfGames: nil,
				additionalPinfall: nil,
				additionalGames: nil,
				excludeFromStatistics: .include,
				alleyId: UUID(0)
			),
			.init(
				bowlerId: UUID(1),
				id: UUID(1),
				name: "Minors",
				recurrence: .repeating,
				numberOfGames: nil,
				additionalPinfall: nil,
				additionalGames: nil,
				excludeFromStatistics: .include,
				alleyId: UUID(0)
			),
		]
	case let .custom(custom):
		leagues = custom
	}

	for league in leagues {
		try league.insert(db)
	}
}
