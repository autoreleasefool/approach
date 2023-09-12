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
	public static var liveValue: Self = {
		@Dependency(\.database) var database
		@Dependency(\.recentlyUsed) var recentlyUsed

		@Sendable func sortGear(
			_ gear: GearStream,
			_ ordering: Gear.Ordering
		) -> GearStream {
			switch ordering {
			case .byName:
				return gear
			case .byRecentlyUsed:
				return sort(gear, byIds: recentlyUsed.observeRecentlyUsedIds(.gear))
			}
		}

		return Self(
			list: { owner, kind, ordering in
				let gear = database.reader().observe {
					let ownerName = Bowler.Database.Columns.name.forKey("ownerName")
					return try Gear.Database
						.all()
						.orderByName()
						.filter(byKind: kind)
						.owned(byBowler: owner)
						.annotated(withOptional: Gear.Database.bowler.select(ownerName))
						.asRequest(of: Gear.Summary.self)
						.fetchAll($0)
				}
				return sortGear(gear, ordering)
			},
			preferred: { bowler in
				database.reader().observe {
					let ownerName = Bowler.Database.Columns.name.forKey("ownerName")
					return try Gear.Database
						.having(
							Gear.Database.bowlerPreferredGear.filter(BowlerPreferredGear.Database.Columns.bowlerId == bowler).isEmpty == false
						)
						.orderByName()
						.annotated(withOptional: Gear.Database.bowler.select(ownerName))
						.asRequest(of: Gear.Summary.self)
						.fetchAll($0)
				}
			},
			overview: {
				let gear = database.reader().observe {
					let ownerName = Bowler.Database.Columns.name.forKey("ownerName")
					return try Gear.Database
						.all()
						.orderByName()
						.annotated(withOptional: Gear.Database.bowler.select(ownerName))
						.asRequest(of: Gear.Summary.self)
						.fetchAll($0)
				}
				return prefix(sortGear(gear, .byRecentlyUsed), ofSize: 3)
			},
			edit: { id in
				try await database.reader().read {
					try Gear.Database
						.filter(Gear.Database.Columns.id == id)
						.including(optional: Gear.Database.bowler.forKey("owner"))
						.asRequest(of: Gear.Edit.self)
						.fetchOneGuaranteed($0)
				}
			},
			create: { gear in
				try await database.writer().write {
					try gear.insert($0)
				}
			},
			update: { gear in
				try await database.writer().write {
					try gear.update($0)
				}
			},
			delete: { id in
				_ = try await database.writer().write {
					try Gear.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
