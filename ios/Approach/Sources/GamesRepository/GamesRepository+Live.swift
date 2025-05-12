import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import ExtensionsPackageLibrary
import Foundation
import GamesRepositoryInterface
import GRDB
import MatchPlaysRepositoryInterface
import ModelsLibrary
import RepositoryLibrary

extension GamesRepository: DependencyKey {
	public static var liveValue: Self {
		@Sendable
		func requestList(forSeries: Series.ID, ordering: Game.Ordering) -> QueryInterfaceRequest<Game.Database> {
			switch ordering {
			case .byIndex:
				return Game.Database
					.all()
					.filter { $0.seriesId == forSeries }
					.isNotArchived()
					.order(Game.Database.Columns.index.asc)
			}
		}

		@Sendable
		func share(request: QueryInterfaceRequest<Game.Database>) -> QueryInterfaceRequest<Game.Shareable> {
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

		@Sendable
		func reorderGames(series: Series.ID, games: [Game.ID], db: Database) throws {
			let gameIds = Set(games)
			let gamesForSeries =
				try Set(
					Game.Database
						.filter(Game.Database.Columns.seriesId == series)
						.isNotArchived()
						.fetchAll(db)
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
					.updateAll(db, Game.Database.Columns.index.set(to: newGameIndex.index))
			}
		}

		return Self(
			list: { series, ordering in
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					try requestList(forSeries: series, ordering: ordering)
						.annotated(withRequired: Game.Database.bowler.select(Bowler.Database.Columns.id.forKey("bowlerId")))
						.asRequest(of: Game.List.self)
						.fetchAll($0)
				}
			},
			archived: {
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					try Game.Database
						.all()
						.isArchived()
						.annotated(withRequired: Game.Database.bowler.select(Bowler.Database.Columns.name.forKey("bowlerName")))
						.annotated(withRequired: Game.Database.league.select(League.Database.Columns.name.forKey("leagueName")))
						.annotated(withRequired: Game.Database.series.select(Series.Database.Columns.date.forKey("seriesDate")))
						.orderByArchivedDate()
						.asRequest(of: Game.Archived.self)
						.fetchAll($0)
				}
			},
			summariesList: { series, ordering in
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					try requestList(forSeries: series, ordering: ordering)
						.asRequest(of: Game.Summary.self)
						.fetchAll($0)
				}
			},
			matchesAgainstOpponent: { opponent in
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					let seriesAlias = TableAlias<Series.Database>()
					return try Game.Database
						.all()
						.isNotArchived()
						.annotated(
							withRequired: Game.Database.matchPlay
								.filter(MatchPlay.Database.Columns.opponentId == opponent)
								.select(MatchPlay.Database.Columns.opponentScore, MatchPlay.Database.Columns.result)
						)
						.joining(required: Game.Database.series.aliased(seriesAlias))
						.order { _ in seriesAlias.date.desc }
						.asRequest(of: Game.ListMatch.self)
						.fetchAll($0)
				}
			},
			shareGames: { gameIds in
				@Dependency(DatabaseService.self) var database

				let games = try await database.reader().read {
					try share(
						request: Game.Database
							.filter(ids: gameIds)
					)
					.fetchAll($0)
				}

				guard games.count == gameIds.count else {
					let firstMissingId = gameIds.first(where: { id in !games.contains(where: { $0.id == id }) })
					throw FetchableError.recordNotFound(type: Game.Shareable.self, id: firstMissingId)
				}

				return games.sortBy(ids: gameIds)
			},
			shareSeries: { seriesId in
				@Dependency(DatabaseService.self) var database

				return try await database.reader().read {
					guard try Series.Database.exists($0, id: seriesId) else {
						throw FetchableError.recordNotFound(type: Series.self, id: seriesId)
					}

					return try share(
						request: Game.Database
							.filter { $0.seriesId == seriesId }
							.order(\.index)
					)
					.fetchAll($0)
				}
			},
			observe: { id in
				@Dependency(DatabaseService.self) var database

				return database.reader().observeOne {
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
				@Dependency(DatabaseService.self) var database

				return try await database.reader().read {
					try Game.Database
						.filter(id: id)
						.asRequest(of: Game.Indexed.self)
						.fetchOne($0)
				}
			},
			update: { game in
				@Dependency(DatabaseService.self) var database
				@Dependency(MatchPlaysRepository.self) var matchPlays

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
			archive: { id in
				@Dependency(DatabaseService.self) var database
				@Dependency(\.date) var date

				try await database.writer().write {
					let game = try Game.Database.fetchOneGuaranteed($0, id: id)

					try Game.Database
						.filter(id: game.id)
						.updateAll(
							$0,
							Game.Database.Columns.archivedOn.set(to: date()),
							Game.Database.Columns.index.set(to: -1)
						)

					let gamesForSeries = try Game.Database
						.filter { $0.seriesId == game.seriesId }
						.order(\.index)
						.isNotArchived()
						.fetchAll($0)
						.map(\.id)

					try reorderGames(series: game.seriesId, games: gamesForSeries, db: $0)
				}
			},
			unarchive: { id in
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					let game = try Game.Database.fetchOneGuaranteed($0, id: id)
					let series = try Series.Database
						.filter(id: game.seriesId)
						.annotated(with: Series.Database.games.max(Game.Database.Columns.index) ?? -1)
						.asRequest(of: Series.HighestIndex.self)
						.fetchOneGuaranteed($0)

					try Game.Database
						.filter(id: game.id)
						.updateAll(
							$0,
							Game.Database.Columns.archivedOn.set(to: nil),
							Game.Database.Columns.index.set(to: series.maxGameIndex < 0 ? 0 : series.maxGameIndex + 1)
						)
				}
			},
			duplicateLanes: { source, destinations in
				@Dependency(DatabaseService.self) var database

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
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					try reorderGames(series: series, games: games, db: $0)
				}
			},
			lockStaleGames: {
				@Dependency(DatabaseService.self) var database
				@Dependency(\.date) var date

				try await database.writer().write { db in
					let currentDate = date()
					let staleLengthOfTime: TimeInterval = 60 * 60 * 24 * 7
					let staleDate = currentDate.advanced(by: -staleLengthOfTime)

					try Game.Database
						.filter(Game.Database.Columns.locked == Game.Lock.open)
						.including(
							required: Game.Database.series
								.filter(Series.Database.Columns.date <= staleDate)
						)
						.updateAll(db, Game.Database.Columns.locked.set(to: Game.Lock.locked))
				}
			}
		)
	}
}
