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
	case .none:
		alleys = []
	case .default:
		alleys = [
			.init(
				id: UUID(0),
				name: "Skyview",
				address: nil,
				material: .wood,
				pinFall: .strings,
				mechanism: .dedicated,
				pinBase: nil
			),
			.init(
				id: UUID(1),
				name: "Grandview",
				address: nil,
				material: .synthetic,
				pinFall: .strings,
				mechanism: .interchangeable,
				pinBase: .black
			)
		]
	case let .custom(custom):
		alleys = custom
	}

	for alley in alleys {
		try alley.insert(db)
	}
}
