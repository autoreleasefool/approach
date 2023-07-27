import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension Alley.Database {
	public static func mock(
		id: Alley.ID,
		name: String,
		material: Alley.Material? = nil,
		pinFall: Alley.PinFall? = nil,
		mechanism: Alley.Mechanism? = nil,
		pinBase: Alley.PinBase? = nil,
		location: Location.ID? = nil
	) -> Self {
		.init(
			id: id,
			name: name,
			material: material,
			pinFall: pinFall,
			mechanism: mechanism,
			pinBase: pinBase,
			locationId: location
		)
	}
}

extension Alley.Summary {
	public init(_ from: Alley.Database) {
		self.init(
			id: from.id,
			name: from.name,
			material: from.material,
			pinFall: from.pinFall,
			mechanism: from.mechanism,
			pinBase: from.pinBase,
			location: nil
		)
	}
}

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
