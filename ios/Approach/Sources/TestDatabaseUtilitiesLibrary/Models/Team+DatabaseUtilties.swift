import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import GRDBDatabaseTestUtilitiesPackageLibrary
import ModelsLibrary

extension Team.Database {
	public static func mock(
		id: ID,
		name: String
	) -> Self {
		Self(id: id, name: name)
	}
}

func insert(
	teams initial: InitialValue<Team.Database>?,
	into db: Database
) throws {
	let teams: [Team.Database]
	switch initial {
	case .none, .zero:
		teams = []
	case .default:
		teams = [
			.mock(id: UUID(0), name: "Besties"),
			.mock(id: UUID(1), name: "Worsties"),
		]
	case let .custom(custom):
		teams = custom
	}

	for team in teams {
		try team.insert(db)
	}
}
