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
					try League.Database
						.filter(League.Database.Columns.id == id)
						.including(optional: League.Database.alley.forKey("location"))
						.asRequest(of: League.Edit.self)
						.fetchOne($0)
				}
			},
			create: { league in
				@Dependency(\.database) var database
				@Dependency(\.uuid) var uuid
				@Dependency(\.date) var date

				return try await database.writer().write {
					try league.insert($0)

					if league.recurrence == .once, let numberOfGames = league.numberOfGames {
						let series = Series.Database(
							leagueId: league.id,
							id: uuid(),
							date: date(),
							numberOfGames: numberOfGames,
							preBowl: .regular,
							excludeFromStatistics: .init(from: league.excludeFromStatistics),
							alleyId: league.location?.id
						)
						try series.insert($0)
					}
				}
			},
			update: { league in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try league.update($0)
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
