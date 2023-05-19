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
		@Dependency(\.database) var database
		@Dependency(\.recentlyUsedService) var recentlyUsed
		@Dependency(\.uuid) var uuid
		@Dependency(\.date) var date

		return Self(
			list: { bowler, recurrence, ordering in
				let leagues = database.reader().observe {
					let averageScore = League.Database.gamesForStatistics.average(Game.Database.Columns.score).forKey("average")
					return try League.Database
						.all()
						.orderByName()
						.bowled(byBowler: bowler)
						.filter(byRecurrence: recurrence)
						.annotated(with: averageScore)
						.asRequest(of: League.List.self)
						.fetchAll($0)
				}

				switch ordering {
				case .byName:
					return leagues
				case .byRecentlyUsed:
					return sort(leagues, byIds: recentlyUsed.observeRecentlyUsedIds(.leagues))
				}
			},
			seriesHost: { id in
				try await database.reader().read {
					try League.Database
						.filter(League.Database.Columns.id == id)
						.including(optional: League.Database.alley)
						.asRequest(of: League.SeriesHost.self)
						.fetchOne($0)
				}
			},
			edit: { id in
				try await database.reader().read {
					try League.Database
						.filter(League.Database.Columns.id == id)
						.including(optional: League.Database.alley.forKey("location"))
						.asRequest(of: League.Edit.self)
						.fetchOne($0)
				}
			},
			create: { league in
				try await withEscapedDependencies { dependencies in
					try await database.writer().write { db in
						try league.insert(db)

						try dependencies.yield {
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
								try series.insert(db)
							}
						}
					}
				}
			},
			update: { league in
				try await database.writer().write {
					try league.update($0)
				}
			},
			delete: { id in
				_ = try await database.writer().write {
					try League.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
