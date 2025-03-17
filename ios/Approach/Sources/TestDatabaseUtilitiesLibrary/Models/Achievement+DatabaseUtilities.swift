import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import GRDBDatabaseTestUtilitiesPackageLibrary
import ModelsLibrary

extension Achievement.Database {
	public static func mock(
		id: Achievement.ID,
		title: String,
		earnedAt: Date
	) -> Achievement.Database {
		Achievement.Database(
			id: id,
			title: title,
			earnedAt: earnedAt
		)
	}
}

extension Achievement.Summary {
	public init(_ from: Achievement.Database) {
		self.init(id: from.id, title: from.title, earnedAt: from.earnedAt)
	}
}

func insert(
	achievements initial: InitialValue<Achievement.Database>?,
	into db: Database
) throws {
	let achievements: [Achievement.Database]
	switch initial {
	case .none, .zero:
		achievements = []
	case .default:
		achievements = [
			.init(id: UUID(0), title: "Iconista", earnedAt: Date(timeIntervalSince1970: 123_456_000)),
			.init(id: UUID(1), title: "TenYears", earnedAt: Date(timeIntervalSince1970: 123_457_000)),
		]
	case let .custom(custom):
		achievements = custom
	}

	try achievements.forEach { try $0.insert(db) }
}
