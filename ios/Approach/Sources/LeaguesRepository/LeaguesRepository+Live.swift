import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import LeaguesRepositoryInterface
import ModelsLibrary
import RecentlyUsedServiceInterface
import RepositoryLibrary

extension LeaguesRepository: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			list: { request in
				@Dependency(\.database) var database

				let leagues = database.reader().observe {
					try League.Summary
						.all()
						.orderByName()
						.bowled(byBowler: request.filter.bowler)
						.filter(byRecurrence: request.filter.recurrence)
						.fetchAll($0)
				}

				switch request.ordering {
				case .byName:
					return leagues
				case .byRecentlyUsed:
					@Dependency(\.recentlyUsedService) var recentlyUsed
					return sort(leagues, byIds: recentlyUsed.observeRecentlyUsedIds(.leagues))
				}
			},
			edit: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try League.Editable.fetchOne($0, id: id)
				}
			},
			save: { league in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try league.save($0)
				}
			},
			delete: { id in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try League.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
