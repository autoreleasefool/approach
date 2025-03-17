import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import GRDBDatabaseTestUtilitiesPackageLibrary
import ModelsLibrary

extension AchievementEvent.Database {
	public static func mock(
		id: AchievementEvent.ID,
		title: String,
		isConsumed: Bool = false
	) -> AchievementEvent.Database {
		AchievementEvent.Database(
			id: id,
			title: title,
			isConsumed: isConsumed
		)
	}
}

extension AchievementEvent.Summary {
	public init(_ from: AchievementEvent.Database) {
		self.init(id: from.id, title: from.title, isConsumed: from.isConsumed)
	}
}

func insert(
	achievementEvents initial: InitialValue<AchievementEvent.Database>?,
	into db: Database
) throws {
	let events: [AchievementEvent.Database]
	switch initial {
	case .none, .zero:
		events = []
	case .default:
		events = [
			.init(id: UUID(0), title: "AppIconsViewed", isConsumed: true),
			.init(id: UUID(1), title: "AppIconsViewed", isConsumed: false),
			.init(id: UUID(2), title: "TenYearsBadgeClaimed", isConsumed: true),
			.init(id: UUID(3), title: "TenYearsBadgeClaimed", isConsumed: false),
		]
	case let .custom(custom):
		events = custom
	}

	try events.forEach { try $0.insert(db) }
}
