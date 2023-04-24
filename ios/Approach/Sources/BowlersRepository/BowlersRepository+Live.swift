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
		@Dependency(\.database) var database

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
				let bowlers = database.reader().observe {
					try Bowler.Database
						.all()
						.orderByName()
						.filter(byStatus: status)
						.asRequest(of: Bowler.Summary.self)
						.fetchAll($0)
				}

				return sortBowlers(bowlers, ordering)
			},
			edit: { id in
				try await database.reader().read {
					try Bowler.Edit.fetchOne($0, id: id)
				}
			},
			create: { bowler in
				try await database.writer().write {
					try bowler.insert($0)
				}
			},
			update: { bowler in
				try await database.writer().write {
					try bowler.update($0)
				}
			},
			delete: { id in
				return try await database.writer().write {
					try Bowler.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
