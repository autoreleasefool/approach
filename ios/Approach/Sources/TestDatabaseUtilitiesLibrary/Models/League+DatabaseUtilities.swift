import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import ModelsLibrary

extension League.Database {
	public static func mock(
		bowlerId: Bowler.ID = UUID(0),
		id: ID,
		name: String,
		recurrence: League.Recurrence = .repeating,
		numberOfGames: Int? = League.DEFAULT_NUMBER_OF_GAMES,
		additionalPinfall: Int? = nil,
		additionalGames: Int? = nil,
		excludeFromStatistics: League.ExcludeFromStatistics = .include
	) -> Self {
		.init(
			bowlerId: bowlerId,
			id: id,
			name: name,
			recurrence: recurrence,
			numberOfGames: numberOfGames,
			additionalPinfall: additionalPinfall,
			additionalGames: additionalGames,
			excludeFromStatistics: excludeFromStatistics
		)
	}
}

extension League.Summary {
	public init(_ from: League.Database) {
		self.init(
			id: from.id,
			name: from.name
		)
	}
}

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
				excludeFromStatistics: .include
			),
			.init(
				bowlerId: UUID(1),
				id: UUID(1),
				name: "Minors",
				recurrence: .repeating,
				numberOfGames: nil,
				additionalPinfall: nil,
				additionalGames: nil,
				excludeFromStatistics: .include
			),
		]
	case let .custom(custom):
		leagues = custom
	}

	for league in leagues {
		try league.insert(db)
	}
}
