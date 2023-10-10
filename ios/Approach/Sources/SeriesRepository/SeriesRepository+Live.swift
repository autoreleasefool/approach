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
						.bowled(inLeague: league)
						.annotated(
							with: Series.Database.games.sum(Game.Database.Columns.score).forKey("total") ?? 0
						)
						.including(
							all: Series.Database.games
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
						.orderByDate()
						.bowled(inLeague: league)
						.asRequest(of: Series.Summary.self)
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
							for index in (0..<series.numberOfGames) {
								let game = Game.Database(
									seriesId: series.id,
									id: uuid(),
									index: index,
									score: 0,
									locked: .open,
									scoringMethod: .byFrame,
									excludeFromStatistics: .init(from: series.excludeFromStatistics)
								)
								try game.insert(db)

								for frameIndex in Game.FRAME_INDICES {
									let frame = Frame.Database(
										gameId: game.id,
										index: frameIndex,
										roll0: nil,
										roll1: nil,
										roll2: nil,
										ball0: nil,
										ball1: nil,
										ball2: nil
									)
									try frame.insert(db)
								}

								for gear in preferredGear {
									try GameGear.Database(gameId: game.id, gearId: gear.id).insert(db)
								}
							}
						}
					}
				}
			},
			update: { series in
				try await database.writer().write {
					try series.update($0)
				}
			},
			delete: { id in
				_ = try await database.writer().write {
					try Series.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
