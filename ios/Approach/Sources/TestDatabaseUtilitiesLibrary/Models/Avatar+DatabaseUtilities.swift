import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import ModelsLibrary

extension Avatar.Database {
	public static func mock(
		id: ID,
		value: Avatar.Value = .mock
	) -> Self {
		.init(
			id: id,
			value: value
		)
	}
}

extension Avatar.Value {
	public static var mock: Self {
		.text("YB", .rgb(134 / 255.0, 128 / 255.0, 223 / 255.0))
	}
}

func insert(
	avatars initial: InitialValue<Avatar.Database>?,
	into db: Database
) throws {
	let avatars: [Avatar.Database]
	switch initial {
	case .none, .zero:
		avatars = []
	case .default:
		avatars = [
			.mock(id: UUID(0)),
		]
	case let .custom(custom):
		avatars = custom
	}

	for avatar in avatars {
		try avatar.insert(db)
	}
}
