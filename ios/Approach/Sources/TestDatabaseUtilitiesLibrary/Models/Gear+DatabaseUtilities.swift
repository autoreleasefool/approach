import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

func insert(
	gear initial: InitialValue<Gear.Database>?,
	into db: Database
) throws {
	let gear: [Gear.Database]
	switch initial {
	case .none:
		gear = []
	case .default:
		gear = [
			.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0)),
			.init(id: UUID(1), name: "Blue", kind: .bowlingBall, bowlerId: UUID(0)),
			.init(id: UUID(2), name: "Red", kind: .towel, bowlerId: UUID(0)),
			.init(id: UUID(3), name: "Green", kind: .bowlingBall, bowlerId: UUID(1)),
			.init(id: UUID(4), name: "Orange", kind: .bowlingBall, bowlerId: UUID(1)),
			.init(id: UUID(5), name: "Pink", kind: .towel, bowlerId: UUID(1)),
		]
	case let .custom(custom):
		gear = custom
	}

	for gear in gear {
		try gear.insert(db)
	}
}
