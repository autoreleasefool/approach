import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import ModelsLibrary
import RepositoryLibrary
import SeriesRepositoryInterface

extension SeriesRepository: DependencyKey {
	public static var liveValue: Self {
		Self(
			list: { league, ordering in
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					var request = Series.Database
						.all()
						.isNotArchived()
						.bowled(inLeague: league)
						.annotated(
							with: Series.Database.games
								.isNotArchived()
								.sum(Game.Database.Columns.score)
								.forKey("total") ?? 0
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
						request = request.order(sql: "\(Series.Database.Columns.coalescedDate) ASC")
					case .newestFirst:
						request = request.order(sql: "\(Series.Database.Columns.coalescedDate) DESC")
					case .highestToLowest:
						request = request.order(
							sql: "total DESC, \(Series.Database.Columns.coalescedDate) ASC"
						)
					case .lowestToHighest:
						request = request.order(
							sql: "total ASC, \(Series.Database.Columns.coalescedDate) ASC"
						)
					}

					return try request
						.asRequest(of: Series.List.self)
						.fetchAll($0)
				}
			},
			summaries: { league in
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					try Series.Database
						.all()
						.isNotArchived()
						.orderByDate()
						.bowled(inLeague: league)
						.asRequest(of: Series.Summary.self)
						.fetchAll($0)
				}
			},
			unusedPreBowls: { league in
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					try Series.Database
						.all()
						.isNotArchived()
						.orderByDate()
						.bowled(inLeague: league)
						.filter(Series.Database.Columns.preBowl == Series.PreBowl.preBowl)
						.filter(Series.Database.Columns.appliedDate == nil)
						.asRequest(of: Series.Summary.self)
						.fetchAll($0)
				}
			},
			gameHost: { series in
				@Dependency(DatabaseService.self) var database

				return try await database.reader().read {
					try Series.Database
						.filter(Series.Database.Columns.id == series)
						.asRequest(of: Series.GameHost.self)
						.fetchOneGuaranteed($0)
				}
			},
			eventSeries: { league in
				@Dependency(DatabaseService.self) var database

				return try await database.reader().read {
					try Series.Database
						.all()
						.isNotArchived()
						.orderByDate()
						.bowled(inLeague: league)
						.asRequest(of: Series.GameHost.self)
						.fetchOneGuaranteed($0)
				}
			},
			shareable: { series in
				@Dependency(DatabaseService.self) var database

				return try await database.reader().read {
					try Series.Database
						.filter(Series.Database.Columns.id == series)
						.annotated(withRequired: Series.Database.bowler.select(Bowler.Database.Columns.name.forKey("bowlerName")))
						.annotated(withRequired: Series.Database.league.select(League.Database.Columns.name.forKey("leagueName")))
						.annotated(
							with: Series.Database.games
								.isNotArchived()
								.sum(Game.Database.Columns.score)
								.forKey("total") ?? 0
						)
						.including(
							all: Series.Database.games
								.isNotArchived()
								.order(Game.Database.Columns.index)
								.select(Game.Database.Columns.index, Game.Database.Columns.score)
								.forKey("scores")
						)
						.asRequest(of: Series.Shareable.self)
						.fetchOneGuaranteed($0)
				}
			},
			archived: {
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					try Series.Database
						.all()
						.isArchived()
						.orderByArchivedDate()
						.annotated(withRequired: Series.Database.bowler.select(Bowler.Database.Columns.name.forKey("bowlerName")))
						.annotated(withRequired: Series.Database.league.select(League.Database.Columns.name.forKey("leagueName")))
						.annotated(with: Series.Database.games.isNotArchived().count.forKey("totalNumberOfGames") ?? 0)
						.asRequest(of: Series.Archived.self)
						.fetchAll($0)
				}
			},
			edit: { id in
				@Dependency(DatabaseService.self) var database

				return try await database.reader().read {
					try Series.Database
						.filter(id: id)
						.including(
							optional: Series.Database
								.alley
								.including(optional: Alley.Database.location)
								.forKey("location")
						)
						.annotated(
							withRequired: Series.Database.league.select(League.Database.Columns.recurrence.forKey("leagueRecurrence"))
						)
						.withNumberOfGames()
						.asRequest(of: Series.Edit.self)
						.fetchOneGuaranteed($0)
				}
			},
			usePreBowl: { series, date in
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					var existing = try Series.Database.fetchOneGuaranteed($0, id: series)

					try Game.Database
						.filter(Game.Database.Columns.seriesId == series)
						.updateAll($0, Game.Database.Columns.excludeFromStatistics.set(to: Game.ExcludeFromStatistics.include))

					existing.excludeFromStatistics = .include
					existing.appliedDate = date
					try existing.update($0)
				}
			},
			create: { series in
				@Dependency(DatabaseService.self) var database

				try await withEscapedDependencies { dependencies in
					try await database.writer().write { db in
						let bowler = try Bowler.Database
							// GRDB does not expose a `contains` predicate for us in this case
							// swiftlint:disable:next contains_over_filter_is_empty
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
								manualScores: series.manualScores,
								db: db
							)
						}
					}
				}
			},
			update: { series in
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					if let existing = try Series.Database.fetchOne($0, id: series.id) {
						switch (existing.preBowl, series.preBowl, series.appliedDate) {
						case (.preBowl, .regular, _), (.regular, .preBowl, .some), (.preBowl, .preBowl, .some):
							try Game.Database
								.filter(Game.Database.Columns.seriesId == series.id)
								.updateAll($0, Game.Database.Columns.excludeFromStatistics.set(to: Game.ExcludeFromStatistics.include))
						case (.regular, .preBowl, .none), (.preBowl, .preBowl, .none):
							try Game.Database
								.filter(Game.Database.Columns.seriesId == series.id)
								.updateAll($0, Game.Database.Columns.excludeFromStatistics.set(to: Game.ExcludeFromStatistics.exclude))
						case (.regular, .regular, _):
							break
						}
					}

					try series.update($0)
				}
			},
			addGamesToSeries: { id, count in
				@Dependency(DatabaseService.self) var database

				try await withEscapedDependencies { dependencies in
					try await database.writer().write { db in
						let series = try Series.Database
							.filter(id: id)
							.annotated(with: Series.Database.games.max(Game.Database.Columns.index) ?? -1)
							.asRequest(of: Series.HighestIndex.self)
							.fetchOneGuaranteed(db)
						let bowler = try Bowler.Database
							// GRDB does not expose a `contains` predicate for us in this case
							// swiftlint:disable:next contains_over_filter_is_empty
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
								startIndex: series.maxGameIndex < 0 ? 0 : series.maxGameIndex + 1,
								count: count,
								manualScores: nil,
								db: db
							)
						}
					}
				}
			},
			archive: { id in
				@Dependency(DatabaseService.self) var database
				@Dependency(\.date) var date

				_ = try await database.writer().write {
					try Series.Database
						.filter(id: id)
						.updateAll($0, Series.Database.Columns.archivedOn.set(to: date()))
				}
			},
			unarchive: { id in
				@Dependency(DatabaseService.self) var database

				_ = try await database.writer().write {
					try Series.Database
						.filter(id: id)
						.updateAll($0, Series.Database.Columns.archivedOn.set(to: nil))
				}
			}
		)
	}
}
