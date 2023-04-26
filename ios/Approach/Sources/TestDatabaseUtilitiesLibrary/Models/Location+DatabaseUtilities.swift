import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

func insert(
	locations initial: InitialValue<Location.Database>?,
	into db: Database
) throws {
	let locations: [Location.Database]
	switch initial {
	case .none:
		locations = []
	case .default:
		locations = [
			.init(
				id: UUID(0),
				title: "123 Fake Street",
				latitude: 123,
				longitude: 123
			),
			.init(
				id: UUID(1),
				title: "321 Real Street",
				latitude: 321,
				longitude: 321
			),
		]
	case let .custom(custom):
		locations = custom
	}

	for location in locations {
		try location.insert(db)
	}
}
