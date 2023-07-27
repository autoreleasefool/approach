import DatabaseModelsLibrary
import Foundation
import GRDB
#if DEBUG
import ModelsLibrary

func insert(
	lanes initial: InitialValue<Lane.Database>?,
	into db: Database
) throws {
	let lanes: [Lane.Database]
	switch initial {
	case .none, .zero:
		lanes = []
	case .default:
		lanes = [
			.init(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall),
			.init(alleyId: UUID(0), id: UUID(1), label: "2", position: .noWall),
			.init(alleyId: UUID(0), id: UUID(2), label: "3", position: .rightWall),
			.init(alleyId: UUID(1), id: UUID(3), label: "1", position: .leftWall),
			.init(alleyId: UUID(1), id: UUID(4), label: "2", position: .noWall),
			.init(alleyId: UUID(1), id: UUID(5), label: "3", position: .rightWall),
		]
	case let .custom(custom):
		lanes = custom
	}

	for lane in lanes {
		try lane.insert(db)
	}
}
#endif
