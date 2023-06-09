import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import LeaguesRepositoryInterface
import ModelsLibrary
import RecentlyUsedServiceInterface
import RepositoryLibrary
import StatisticsModelsLibrary

extension LeaguesRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.database) var database
		@Dependency(\.recentlyUsed) var recentlyUsed
		@Dependency(\.uuid) var uuid
		@Dependency(\.date) var date

		@Sendable func requestList(
			bowledBy: Bowler.ID,
			withRecurrence: League.Recurrence?,
			ordered: League.Ordering
		) -> QueryInterfaceRequest<League.Database> {
			League.Database
				.all()
				.orderByName()
				.bowled(byBowler: bowledBy)
				.filter(byRecurrence: withRecurrence)
		}

		return Self(
			list: { bowler, recurrence, ordering in
				let leagues = database.reader().observe {
					let series = League.Database.trackableSeries(filter: nil)
					let games = League.Database.trackableGames(through: series, filter: nil)
					let averageScore = games
						.average(Game.Database.Columns.score)
						.forKey("average")
					return try requestList(bowledBy: bowler, withRecurrence: recurrence, ordered: ordering)
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
			pickable: { bowler, recurrence, ordering in
				let leagues = database.reader().observe {
					try requestList(bowledBy: bowler, withRecurrence: recurrence, ordered: ordering)
						.asRequest(of: League.Summary.self)
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
						.asRequest(of: League.SeriesHost.self)
						.fetchOne($0)
				}
			},
			edit: { id in
				try await database.reader().read {
					try League.Database
						.filter(League.Database.Columns.id == id)
						.including(optional: League.Database.alleys.forKey("location"))
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
