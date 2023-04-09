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
			ordering: Bowler.FetchRequest.Ordering
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
			playable: { request in
				@Dependency(\.database) var database

				let bowlers = database.reader().observe {
					try Bowler.Summary
						.all()
						.orderByName()
						.filter(byStatus: .playable)
						.fetchAll($0)
				}

				return sortBowlers(bowlers, ordering: request.ordering)
			},
			opponents: { request in
				@Dependency(\.database) var database

				let opponents = database.reader().observe {
					try Bowler.Summary
						.all()
						.orderByName()
						.fetchAll($0)
				}

				return sortBowlers(opponents, ordering: request.ordering)
			},
			edit: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try Bowler.Editable.fetchOne($0, id: id)
				}
			},
			save: { bowler in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try bowler.save($0)
				}
			},
			delete: { id in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try Bowler.DatabaseModel.deleteOne($0, id: id)
				}
			}
		)
	}()
}
