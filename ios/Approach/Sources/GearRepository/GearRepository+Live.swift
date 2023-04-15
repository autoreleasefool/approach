import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GearRepositoryInterface
import GRDB
import ModelsLibrary
import RecentlyUsedServiceInterface
import RepositoryLibrary

public typealias GearStream = AsyncThrowingStream<[Gear.Summary], Error>

extension Gear {
	struct SummaryFetch: Codable, TableRecord, FetchableRecord {
		static let databaseTableName = Gear.Database.databaseTableName

		var gear: Gear.Summary
		var owner: String?
	}
}

extension GearRepository: DependencyKey {
	public static var liveValue: Self = {
		@Sendable func sortGear(
			_ gear: GearStream,
			_ ordering: Gear.Ordering
		) -> GearStream {
			switch ordering {
			case .byName:
				return gear
			case .byRecentlyUsed:
				@Dependency(\.recentlyUsedService) var recentlyUsed
				return sort(gear, byIds: recentlyUsed.observeRecentlyUsedIds(.gear))
			}
		}

		return Self(
			list: { owner, kind, ordering in
				@Dependency(\.database) var database

				let gear = database.reader().observe {
					try Gear.Summary
						.allAnnotated()
						.orderByName()
						.filter(byKind: kind)
						.owned(byBowler: owner)
						.fetchAll($0)
				}

				return sortGear(gear, ordering)
			},
			edit: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try Gear.Edit.fetchOne($0, id: id)
				}
			},
			create: { gear in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try gear.insert($0)
				}
			},
			update: { gear in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try gear.update($0)
				}
			},
			delete: { id in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try Gear.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
