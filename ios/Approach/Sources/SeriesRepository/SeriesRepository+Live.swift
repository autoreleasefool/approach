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
			list: { league, _ in
				database.reader().observe {
					try Series.Database
						.all()
						.orderByDate()
						.bowled(inLeague: league)
						.annotated(with: Series.Database.games.sum(Game.Database.Columns.score).forKey("total"))
						.including(
							all: Series.Database.games
								.order(Game.Database.Columns.index)
								.select(Game.Database.Columns.index, Game.Database.Columns.score)
								.forKey("scores")
						)
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
						.including(optional: Series.Database.alley.forKey("location"))
						.asRequest(of: Series.Edit.self)
						.fetchOne($0)
				}
			},
			create: { series in
				try await withEscapedDependencies { dependencies in
					try await database.writer().write { db in
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
