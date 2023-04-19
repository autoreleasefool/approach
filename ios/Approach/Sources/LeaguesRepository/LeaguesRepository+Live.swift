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
			list: { bowler, recurrence, ordering in
				@Dependency(\.database) var database

				let leagues = database.reader().observe {
					try League.Database
						.all()
						.orderByName()
						.bowled(byBowler: bowler)
						.filter(byRecurrence: recurrence)
						.asRequest(of: League.Summary.self)
						.fetchAll($0)
				}

				switch ordering {
				case .byName:
					return leagues
				case .byRecentlyUsed:
					@Dependency(\.recentlyUsedService) var recentlyUsed
					return sort(leagues, byIds: recentlyUsed.observeRecentlyUsedIds(.leagues))
				}
			},
			seriesHost: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try League.Database
						.filter(League.Database.Columns.id == id)
						.including(optional: League.Database.alley)
						.asRequest(of: League.SeriesHost.self)
						.fetchOne($0)
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
