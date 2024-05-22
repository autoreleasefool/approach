import DatabaseModelsLibrary
import Foundation
import GRDB
import GRDBDatabaseTestUtilitiesPackageLibrary
import ModelsLibrary

func insert(
	bowlerPreferredGear initial: InitialValue<BowlerPreferredGear.Database>?,
	into db: Database
) throws {
	let bowlerPreferredGear: [BowlerPreferredGear.Database]
	switch initial {
	case .none, .zero:
		bowlerPreferredGear = []
	case .default:
		bowlerPreferredGear = [
			.init(bowlerId: UUID(0), gearId: UUID(0)),
			.init(bowlerId: UUID(0), gearId: UUID(1)),
			.init(bowlerId: UUID(1), gearId: UUID(3)),
			.init(bowlerId: UUID(1), gearId: UUID(4)),
		]
	case let .custom(custom):
		bowlerPreferredGear = custom
	}

	for gear in bowlerPreferredGear {
		try gear.insert(db)
	}
}
