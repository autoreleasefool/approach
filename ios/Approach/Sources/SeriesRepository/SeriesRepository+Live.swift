import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import ModelsLibrary
import RepositoryLibrary
import SeriesRepositoryInterface

extension SeriesRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.database) var database
		@Dependency(\.uuid) var uuid

		return Self(
			list: { league, ordering in
				database.reader().observe {
					var request = Series.Database
						.all()
						.isNotArchived()
						.bowled(inLeague: league)
						.annotated(
							with: Series.Database.games
								.isNotArchived()
								.sum(Game.Database.Columns.score).forKey("total") ?? 0
						)
						.including(
							all: Series.Database.games
								.isNotArchived()
								.order(Game.Database.Columns.index)
								.select(Game.Database.Columns.index, Game.Database.Columns.score)
								.forKey("scores")
						)

					switch ordering {
					case .oldestFirst:
						request = request.order(Series.Database.Columns.date.asc)
					case .newestFirst:
						request = request.order(Series.Database.Columns.date.desc)
					case .highestToLowest:
						request = request.order(
							Column("total").detached.desc,
							Series.Database.Columns.date.asc
						)
					case .lowestToHighest:
						request = request.order(
							Column("total").detached.asc,
							Series.Database.Columns.date.asc
						)
					}

					return try request
						.asRequest(of: Series.List.self)
						.fetchAll($0)
				}
			},
			summaries: { league in
				database.reader().observe {
					try Series.Database
						.all()
						.isNotArchived()
						.orderByDate()
						.bowled(inLeague: league)
						.asRequest(of: Series.Summary.self)
						.fetchAll($0)
				}
			},
			archived: {
				database.reader().observe {
					try Series.Database
						.all()
						.isArchived()
						.orderByDate()
						.annotated(withRequired: Series.Database.bowler.select(Bowler.Database.Columns.name.forKey("bowlerName")))
						.annotated(withRequired: Series.Database.league.select(League.Database.Columns.name.forKey("leagueName")))
						.annotated(with: Series.Database.games.isNotArchived().count.forKey("totalNumberOfGames") ?? 0)
						.asRequest(of: Series.Archived.self)
						.fetchAll($0)
				}
			},
			edit: { id in
				try await database.reader().read {
					let lanesAlias = TableAlias(name: "lanes")
					return try Series.Database
						.filter(id: id)
						.including(
							optional: Series.Database
								.alley
								.including(optional: Alley.Database.location)
								.forKey("location")
						)
						.withNumberOfGames()
						.asRequest(of: Series.Edit.self)
						.fetchOneGuaranteed($0)
				}
			},
			create: { series in
				try await withEscapedDependencies { dependencies in
					try await database.writer().write { db in
						let bowler = try Bowler.Database
							.having(Bowler.Database.leagues.filter(League.Database.Columns.id == series.leagueId).isEmpty == false)
							.fetchOneGuaranteed(db)
						let preferredGear = try bowler
							.request(for: Bowler.Database.preferredGear)
							.fetchAll(db)

						try series.insert(db)

						try dependencies.yield {
							try Series.insertGames(
								forSeries: series.id,
								excludeFromStatistics: series.excludeFromStatistics,
								withPreferredGear: preferredGear,
								startIndex: 0,
								count: series.numberOfGames,
								db: db
							)
						}
					}
				}
			},
			update: { series in
				try await database.writer().write {
					if let existing = try Series.Database.fetchOne($0, id: series.id) {
						switch (existing.preBowl, series.preBowl) {
						case (.preBowl, .regular):
							try Game.Database
								.filter(Game.Database.Columns.seriesId == series.id)
								.updateAll($0, Game.Database.Columns.excludeFromStatistics.set(to: Game.ExcludeFromStatistics.include))
						case (.regular, .preBowl):
							try Game.Database
								.filter(Game.Database.Columns.seriesId == series.id)
								.updateAll($0, Game.Database.Columns.excludeFromStatistics.set(to: Game.ExcludeFromStatistics.exclude))
						case (.preBowl, .preBowl), (.regular, .regular):
							break
						}
					}

					try series.update($0)
				}
			},
			addGamesToSeries: { id, count in
				try await withEscapedDependencies { dependencies in
					try await database.writer().write { db in
						let series = try Series.Database
							.filter(id: id)
							.annotated(with: Series.Database.games.max(Game.Database.Columns.index) ?? 0)
							.asRequest(of: Series.HighestIndex.self)
							.fetchOneGuaranteed(db)
						let bowler = try Bowler.Database
							.having(Bowler.Database.leagues.filter(League.Database.Columns.id == series.leagueId).isEmpty == false)
							.fetchOneGuaranteed(db)
						let preferredGear = try bowler
							.request(for: Bowler.Database.preferredGear)
							.fetchAll(db)

						try dependencies.yield {
							try Series.insertGames(
								forSeries: id,
								excludeFromStatistics: series.excludeFromStatistics,
								withPreferredGear: preferredGear,
								startIndex: series.maxGameIndex <= 0 ? 0 : series.maxGameIndex + 1,
								count: count,
								db: db
							)
						}
					}
				}
			},
			archive: { id in
				return try await database.writer().write {
					try Series.Database
						.filter(id: id)
						.updateAll($0, Series.Database.Columns.isArchived.set(to: true))
				}
			},
			unarchive: { id in
				return try await database.writer().write {
					try Series.Database
						.filter(id: id)
						.updateAll($0, Series.Database.Columns.isArchived.set(to: false))
				}
			}
		)
	}()
}
