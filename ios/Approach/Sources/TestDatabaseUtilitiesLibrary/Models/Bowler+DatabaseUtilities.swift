import DatabaseModelsLibrary
import Foundation
import GRDB
import GRDBDatabaseTestUtilitiesPackageLibrary
import ModelsLibrary

extension Bowler.Database {
	public static func mock(
		id: Bowler.ID,
		name: String,
		kind: Bowler.Kind = .playable,
		archivedOn: Date? = nil
	) -> Self {
		.init(id: id, name: name, kind: kind, archivedOn: archivedOn)
	}
}

func insert(
	bowlers initial: InitialValue<Bowler.Database>?,
	into db: Database
) throws {
	let bowlers: [Bowler.Database]
	switch initial {
	case .none, .zero:
		bowlers = []
	case .default:
		bowlers = [
			.init(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil),
			.init(id: UUID(1), name: "Sarah", kind: .playable, archivedOn: nil),
		]
	case let .custom(custom):
		bowlers = custom
	}

	for bowler in bowlers {
		try bowler.insert(db)
	}
}
