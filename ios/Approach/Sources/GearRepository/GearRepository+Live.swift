import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GearRepositoryInterface
import GRDB
import ModelsLibrary
import RecentlyUsedServiceInterface
import RepositoryLibrary

public typealias GearStream = AsyncThrowingStream<[Gear.Summary], Error>

extension GearRepository: DependencyKey {
	public static var liveValue: Self {
		@Sendable
		func sortGear(
			_ gear: GearStream,
			_ ordering: Gear.Ordering
		) -> GearStream {
			@Dependency(RecentlyUsedService.self) var recentlyUsed

			switch ordering {
			case .byName:
				return gear
			case .byRecentlyUsed:
				return sort(gear, byIds: recentlyUsed.observeRecentlyUsedIds(.gear))
			}
		}

		return Self(
			list: { owner, kind, ordering in
				@Dependency(DatabaseService.self) var database

				let gear = database.reader().observe {
					try Gear.Database
						.all()
						.filter { kind == nil || $0.kind == kind }
						.filter { owner == nil || $0.bowlerId == owner }
						.order { $0.name.collating(.localizedCaseInsensitiveCompare) }
						.includingSummaryProperties()
						.asRequest(of: Gear.Summary.self)
						.fetchAll($0)
				}
				return sortGear(gear, ordering)
			},
			preferred: { bowler in
				@Dependency(DatabaseService.self) var database

				return try await database.reader().read {
					try Gear.Database
						.having(
							// GRDB does not expose a `contains` predicate for us in this case
							// swiftlint:disable:next contains_over_filter_is_empty
							Gear.Database.bowlerPreferredGear
								.filter(BowlerPreferredGear.Database.Columns.bowlerId == bowler).isEmpty == false
						)
						.order { $0.name.collating(.localizedCaseInsensitiveCompare) }
						.includingSummaryProperties()
						.asRequest(of: Gear.Summary.self)
						.fetchAll($0)
				}
			},
			mostRecentlyUsed: { kind, limit in
				@Dependency(DatabaseService.self) var database

				let gear = database.reader().observe {
					try Gear.Database
						.all()
						.filter { kind == nil || $0.kind == kind }
						.order { $0.name.collating(.localizedCaseInsensitiveCompare) }
						.includingSummaryProperties()
						.asRequest(of: Gear.Summary.self)
						.fetchAll($0)
				}
				return prefix(sortGear(gear, .byRecentlyUsed), ofSize: limit)
			},
			edit: { id in
				@Dependency(DatabaseService.self) var database

				return try await database.reader().read {
					try Gear.Database
						.filter(Gear.Database.Columns.id == id)
						.including(optional: Gear.Database.bowler.forKey("owner"))
						.includingAvatar()
						.asRequest(of: Gear.Edit.self)
						.fetchOneGuaranteed($0)
				}
			},
			create: { gear in
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					try gear.avatar.databaseModel.insert($0)
					try gear.insert($0)
				}
			},
			update: { gear in
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					try gear.update($0)
					try gear.avatar.databaseModel.update($0)
				}
			},
			delete: { id in
				@Dependency(DatabaseService.self) var database

				_ = try await database.writer().write {
					try Gear.Database.deleteOne($0, id: id)
				}
			},
			updatePreferredGear: { bowler, gear in
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					try BowlerPreferredGear.Database
						.filter(BowlerPreferredGear.Database.Columns.bowlerId == bowler)
						.deleteAll($0)
					for gear in gear {
						let bowlerPreferredGear = BowlerPreferredGear.Database(bowlerId: bowler, gearId: gear)
						try bowlerPreferredGear.insert($0)
					}
				}
			}
		)
	}
}
