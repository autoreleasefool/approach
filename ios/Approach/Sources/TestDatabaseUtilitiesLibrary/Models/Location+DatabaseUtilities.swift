import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension Location.Database {
	public static func mock(
		id: ID,
		title: String = "123 Fake Street",
		subtitle: String = "Grandview",
		latitude: Double = 123,
		longitude: Double = 123
	) -> Self {
		.init(
			id: id,
			title: title,
			subtitle: subtitle,
			latitude: latitude,
			longitude: longitude
		)
	}
}


func insert(
	locations initial: InitialValue<Location.Database>?,
	into db: Database
) throws {
	let locations: [Location.Database]
	switch initial {
	case .none, .zero:
		locations = []
	case .default:
		locations = [
			.init(
				id: UUID(0),
				title: "123 Fake Street",
				subtitle: "Grandview",
				latitude: 123,
				longitude: 123
			),
			.init(
				id: UUID(1),
				title: "321 Real Street",
				subtitle: "Viewgrand",
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
