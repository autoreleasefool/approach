import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension Bowler.Database {
	public static func mock(
		id: Bowler.ID,
		name: String,
		kind: Bowler.Kind = .playable,
		isArchived: Bool = false
	) -> Self {
		.init(id: id, name: name, kind: kind, isArchived: isArchived)
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
			.init(id: UUID(0), name: "Joseph", kind: .playable, isArchived: false),
			.init(id: UUID(1), name: "Sarah", kind: .playable, isArchived: false),
		]
	case let .custom(custom):
		bowlers = custom
	}

	for bowler in bowlers {
		try bowler.insert(db)
	}
}
