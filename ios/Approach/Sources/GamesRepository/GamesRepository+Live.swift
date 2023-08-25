import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
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
			edit: { id in
				try await database.reader().read {
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
			}
		)
	}()
}
