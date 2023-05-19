import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

func insert(
	alleys initial: InitialValue<Alley.Database>?,
	into db: Database
) throws {
	let alleys: [Alley.Database]
	switch initial {
	case .none, .zero:
		alleys = []
	case .default:
		alleys = [
			.init(
				id: UUID(0),
				name: "Skyview",
				material: .wood,
				pinFall: .strings,
				mechanism: .dedicated,
				pinBase: nil,
				locationId: UUID(0)
			),
			.init(
				id: UUID(1),
				name: "Grandview",
				material: .synthetic,
				pinFall: .strings,
				mechanism: .interchangeable,
				pinBase: .black,
				locationId: UUID(1)
			),
		]
	case let .custom(custom):
		alleys = custom
	}

	for alley in alleys {
		try alley.insert(db)
	}
}
