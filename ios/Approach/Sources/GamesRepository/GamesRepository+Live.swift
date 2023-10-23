import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import ExtensionsLibrary
import GamesRepositoryInterface
import GRDB
import MatchPlaysRepositoryInterface
import ModelsLibrary
import RepositoryLibrary

extension GamesRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.database) var database
		@Dependency(\.matchPlays) var matchPlays

		@Sendable func requestList(forSeries: Series.ID, ordering: Game.Ordering) -> QueryInterfaceRequest<Game.Database> {
			switch ordering {
			case .byIndex:
				return Game.Database
					.all()
					.filter(bySeries: forSeries)
					.order(Game.Database.Columns.index.asc)
			}
		}

		@Sendable func share(request: QueryInterfaceRequest<Game.Database>) -> QueryInterfaceRequest<Game.Shareable> {
			request
				.including(required: Game.Database.bowler)
				.including(required: Game.Database.league)
				.including(
					required: Game.Database.series
						.including(optional: Series.Database.alley)
				)
				.including(
					all: Game.Database.frames
						.order(Frame.Database.Columns.index)
						.including(
							optional: Frame.Database.bowlingBall0
								.includingSummaryProperties()
								.forKey("bowlingBall0")
						)
						.including(
							optional: Frame.Database
								.bowlingBall1
								.includingSummaryProperties()
								.forKey("bowlingBall1")
						)
						.including(
							optional: Frame.Database
								.bowlingBall2
								.includingSummaryProperties()
								.forKey("bowlingBall2")
						)
				)
				.asRequest(of: Game.Shareable.self)
		}

		return Self(
			list: { series, ordering in
				database.reader().observe {
					try requestList(forSeries: series, ordering: ordering)
						.annotated(withRequired: Game.Database.bowler.select(Bowler.Database.Columns.id.forKey("bowlerId")))
						.asRequest(of: Game.List.self)
						.fetchAll($0)
				}
			},
			summariesList: { series, ordering in
				database.reader().observe {
					try requestList(forSeries: series, ordering: ordering)
						.asRequest(of: Game.Summary.self)
						.fetchAll($0)
				}
			},
			matchesAgainstOpponent: { opponent in
				database.reader().observe {
					let seriesAlias = TableAlias()
					return try Game.Database
						.all()
						.annotated(withRequired: Game.Database.matchPlay
							.filter(MatchPlay.Database.Columns.opponentId == opponent)
							.select(MatchPlay.Database.Columns.opponentScore, MatchPlay.Database.Columns.result)
						)
						.joining(required: Game.Database.series.aliased(seriesAlias))
						.order(seriesAlias[Series.Database.Columns.date.desc])
						.asRequest(of: Game.ListMatch.self)
						.fetchAll($0)
				}
			},
			shareGames: { gameIds in
				let games = try await database.reader().read {
					try share(
						request: Game.Database
							.filter(ids: gameIds)
					)
					.fetchAll($0)
				}

				guard games.count == gameIds.count else {
					throw FetchableError.allRecordsNotFound(type: Game.Shareable.self, allIds: gameIds, foundIds: games.map(\.id))
				}

				return games.sortBy(ids: gameIds)
			},
			shareSeries: { seriesId in
				try await database.reader().read {
					guard try Series.Database.exists($0, id: seriesId) else {
						throw FetchableError.recordNotFound(type: Series.self, id: seriesId)
					}

					return try share(
						request: Game.Database
							.filter(Game.Database.Columns.seriesId == seriesId)
							.orderByIndex()
					)
					.fetchAll($0)
				}
			},
			observe: { id in
				database.reader().observeOne {
					try Game.Database
						.filter(id: id)
						.including(required: Game.Database.bowler)
						.including(required: Game.Database.league)
						.including(
							optional: Game.Database.matchPlay
								.including(optional: MatchPlay.Database.opponent.forKey("opponent"))
						)
						.including(
							required: Game.Database.series
								.including(optional: Series.Database.alley)
						)
						.including(
							all: Game.Database.gear
								.order(Gear.Database.Columns.kind)
								.order(Gear.Database.Columns.name)
								.includingAvatar()
								.forKey("gear")
						)
						.including(
							all: Game.Database.lanes
								.orderByLabel()
						)
						.asRequest(of: Game.Edit.self)
						.fetchOne($0)
				}
			},
			findIndex: { id in
				try await database.reader().read {
					try Game.Database
						.filter(id: id)
						.asRequest(of: Game.Indexed.self)
						.fetchOne($0)
				}
			},
			update: { game in
				try await database.writer().write {
					try game.update($0)

					// FIXME: Rather than deleting all associations, should only add new/remove old
					try GameGear.Database
						.filter(GameGear.Database.Columns.gameId == game.id)
						.deleteAll($0)
					for gear in game.gear {
						let gameGear = GameGear.Database(gameId: game.id, gearId: gear.id)
						try gameGear.insert($0)
					}

					// FIXME: Rather than deleting all associations, should only add new/remove old
					try GameLane.Database
						.filter(GameLane.Database.Columns.gameId == game.id)
						.deleteAll($0)
					for lane in game.lanes {
						let gameLane = GameLane.Database(gameId: game.id, laneId: lane.id)
						try gameLane.insert($0)
					}
				}

				if let matchPlay = game.matchPlay {
					try await matchPlays.update(matchPlay)
				}
			},
			delete: { id in
				_ = try await database.writer().write {
					try Game.Database.deleteOne($0, id: id)
				}
			},
			duplicateLanes: { source, destinations in
				try await database.writer().write {
					let lanesForGame = try GameLane.Database
						.filter(GameLane.Database.Columns.gameId == source)
						.fetchAll($0)

					for game in destinations {
						try GameLane.Database
							.filter(GameLane.Database.Columns.gameId == game)
							.deleteAll($0)

						for lane in lanesForGame {
							let gameLane = GameLane.Database(gameId: game, laneId: lane.laneId)
							try gameLane.upsert($0)
						}
					}
				}
			},
			reorderGames: { series, games in
				try await database.writer().write {
					let gameIds = Set(games)
					let gamesForSeries =
						try Set(
							Game.Database
								.filter(Game.Database.Columns.seriesId == series)
								.fetchAll($0)
								.map(\.id)
						)

					guard gameIds.count == games.count else {
						throw GamesRepositoryError.reorderingDuplicateGames(series: series, duplicateGames: games.findDuplicates())
					}

					guard gameIds == gamesForSeries else {
						let missingGames = gamesForSeries.subtracting(gameIds)
						let extraGames = gameIds.subtracting(gamesForSeries)
						if !missingGames.isEmpty {
							throw GamesRepositoryError.missingGamesToReorder(series: series, gamesInSeriesMissing: missingGames)
						} else {
							throw GamesRepositoryError.tooManyGamesToReorder(series: series, gamesNotInSeries: extraGames)
						}
					}

					let newGameIndices: [(index: Int, id: Game.ID)] = games.enumerated().map { ($0, $1) }
					for newGameIndex in newGameIndices {
						try Game.Database
							.filter(id: newGameIndex.id)
							.updateAll($0, Game.Database.Columns.index.set(to: newGameIndex.index))
					}
				}
			}
		)
	}()
}
