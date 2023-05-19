import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

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
			.init(id: UUID(0), name: "Joseph", status: .playable),
			.init(id: UUID(1), name: "Sarah", status: .playable),
		]
	case let .custom(custom):
		bowlers = custom
	}

	for bowler in bowlers {
		try bowler.insert(db)
	}
}
