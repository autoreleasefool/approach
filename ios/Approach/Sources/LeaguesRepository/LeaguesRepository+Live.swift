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
				.bowled(byBowler: bowledBy)
				.filter(byRecurrence: withRecurrence)
				.isNotArchived()
				.orderByName()
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
			archived: {
				database.reader().observe {
					try League.Database
						.all()
						.isArchived()
						.orderByArchivedDate()
						.annotated(withRequired: League.Database.bowler.select(Bowler.Database.Columns.name.forKey("bowlerName")))
						.annotated(with: League.Database.series.isNotArchived().count.forKey("totalNumberOfSeries") ?? 0)
						.annotated(with: League.Database.games.isNotArchived().count.forKey("totalNumberOfGames") ?? 0)
						.asRequest(of: League.Archived.self)
						.fetchAll($0)
				}
			},
			seriesHost: { id in
				try await database.reader().read {
					try League.Database
						.filter(League.Database.Columns.id == id)
						.asRequest(of: League.SeriesHost.self)
						.fetchOneGuaranteed($0)
				}
			},
			edit: { id in
				try await database.reader().read {
					try League.Database
						.filter(League.Database.Columns.id == id)
						.including(optional: League.Database.alleys.forKey("location"))
						.asRequest(of: League.Edit.self)
						.fetchOneGuaranteed($0)
				}
			},
			create: { league in
				try await withEscapedDependencies { dependencies in
					try await database.writer().write { db in
						let bowler = try Bowler.Database
							.filter(id: league.bowlerId)
							.fetchOneGuaranteed(db)
						let preferredGear = try bowler
							.request(for: Bowler.Database.preferredGear)
							.fetchAll(db)

						try league.insert(db)

						try dependencies.yield {
							if league.recurrence == .once, let numberOfGames = league.numberOfGames {
								let series = Series.Database(
									leagueId: league.id,
									id: uuid(),
									date: date(),
									preBowl: .regular,
									excludeFromStatistics: .init(from: league.excludeFromStatistics),
									alleyId: league.location?.id,
									archivedOn: nil
								)
								try series.insert(db)

								try Series.insertGames(
									forSeries: series.id,
									excludeFromStatistics: series.excludeFromStatistics,
									withPreferredGear: preferredGear,
									startIndex: 0,
									count: numberOfGames,
									db: db
								)
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
			archive: { id in
				@Dependency(\.date) var date
				return try await database.writer().write {
					try League.Database
						.filter(id: id)
						.updateAll($0, League.Database.Columns.archivedOn.set(to: date()))
				}
			},
			unarchive: { id in
				return try await database.writer().write {
					try League.Database
						.filter(id: id)
						.updateAll($0, League.Database.Columns.archivedOn.set(to: nil))
				}
			}
		)
	}()
}
