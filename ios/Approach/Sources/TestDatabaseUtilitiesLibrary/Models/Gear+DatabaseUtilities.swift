import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import GRDBDatabaseTestUtilitiesPackageLibrary
import ModelsLibrary

extension Gear.Database {
	public static func mock(
		id: ID,
		name: String,
		kind: Gear.Kind = .bowlingBall,
		bowlerId: Bowler.ID? = UUID(0),
		avatarId: Avatar.ID = UUID(0)
	) -> Self {
		.init(
			id: id,
			name: name,
			kind: kind,
			bowlerId: bowlerId,
			avatarId: avatarId
		)
	}
}

func insert(
	gear initial: InitialValue<Gear.Database>?,
	into db: Database
) throws {
	let gear: [Gear.Database]
	switch initial {
	case .none, .zero:
		gear = []
	case .default:
		gear = [
			.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0)),
			.mock(id: UUID(1), name: "Blue", kind: .bowlingBall, bowlerId: UUID(0)),
			.mock(id: UUID(2), name: "Red", kind: .towel, bowlerId: UUID(0)),
			.mock(id: UUID(3), name: "Green", kind: .bowlingBall, bowlerId: UUID(1)),
			.mock(id: UUID(4), name: "Orange", kind: .bowlingBall, bowlerId: UUID(1)),
			.mock(id: UUID(5), name: "Pink", kind: .towel, bowlerId: UUID(1)),
		]
	case let .custom(custom):
		gear = custom
	}

	for gear in gear {
		try gear.insert(db)
	}
}
