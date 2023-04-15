import BowlersRepositoryInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import ModelsLibrary
import RecentlyUsedServiceInterface
import RepositoryLibrary

typealias BowlerStream = AsyncThrowingStream<[Bowler.Summary], Error>

extension BowlersRepository: DependencyKey {
	public static var liveValue: Self = {
		@Sendable func sortBowlers(
			_ bowlers: BowlerStream,
			_ ordering: Bowler.Ordering
		) -> BowlerStream {
			switch ordering {
			case .byName:
				return bowlers
			case .byRecentlyUsed:
				@Dependency(\.recentlyUsedService) var recentlyUsed
				return sort(bowlers, byIds: recentlyUsed.observeRecentlyUsedIds(.bowlers))
			}
		}

		return Self(
			list: { status, ordering in
				@Dependency(\.database) var database

				let bowlers = database.reader().observe {
					try Bowler.Summary
						.all()
						.orderByName()
						.filter(byStatus: status)
						.fetchAll($0)
				}

				return sortBowlers(bowlers, ordering)
			},
			edit: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try Bowler.Edit.fetchOne($0, id: id)
				}
			},
			create: { bowler in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try bowler.insert($0)
				}
			},
			update: { bowler in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try bowler.update($0)
				}
			},
			delete: { id in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try Bowler.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
