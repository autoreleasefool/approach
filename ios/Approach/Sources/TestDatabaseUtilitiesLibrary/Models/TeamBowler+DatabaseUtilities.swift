import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import GRDBDatabaseTestUtilitiesPackageLibrary
import ModelsLibrary

extension TeamBowler.Database {
	public static func mock(
		teamId: Team.ID,
		bowlerId: Bowler.ID,
		position: Int
	) -> Self {
		Self(teamId: teamId, bowlerId: bowlerId, position: position)
	}
}

func insert(
	teamBowlers initial: InitialValue<TeamBowler.Database>?,
	into db: Database
) throws {
	let teamBowlers: [TeamBowler.Database]
	switch initial {
	case .none, .zero:
		teamBowlers = []
	case .default:
		teamBowlers = [
			.mock(teamId: UUID(0), bowlerId: UUID(0), position: 0),
			.mock(teamId: UUID(0), bowlerId: UUID(1), position: 1),
			.mock(teamId: UUID(1), bowlerId: UUID(1), position: 0),
			.mock(teamId: UUID(1), bowlerId: UUID(0), position: 1),
		]
	case let .custom(custom):
		teamBowlers = custom
	}

	for teamBowler in teamBowlers {
		try teamBowler.insert(db)
	}
}
