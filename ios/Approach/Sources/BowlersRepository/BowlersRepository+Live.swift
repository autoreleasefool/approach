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
			ordering: Bowler.Ordering
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
			playable: { ordering in
				@Dependency(\.database) var database

				let bowlers = database.reader().observe {
					try Bowler.DatabaseModel
						.all()
						.orderByName()
						.filter(byStatus: .playable)
						.fetchAll($0)
						.map(Bowler.Summary.init)
				}

				return sortBowlers(bowlers, ordering: ordering)
			},
			opponents: { ordering in
				@Dependency(\.database) var database

				let opponents = database.reader().observe {
					try Bowler.DatabaseModel
						.all()
						.orderByName()
						.fetchAll($0)
						.map(Bowler.Summary.init)
				}

				return sortBowlers(opponents, ordering: ordering)
			},
			edit: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try .init(Bowler.DatabaseModel.fetchOne($0, id: id))
				}
			},
			save: { bowler in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try bowler.databaseModel.save($0)
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
