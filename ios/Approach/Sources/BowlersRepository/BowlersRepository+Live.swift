import BowlersRepositoryInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import ModelsLibrary
import RecentlyUsedServiceInterface
import RepositoryLibrary
import SortingLibrary
import StatisticsModelsLibrary

typealias BowlerStream = AsyncThrowingStream<[Bowler.Summary], Error>

extension BowlersRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.database) var database
		@Dependency(\.recentlyUsed) var recentlyUsed

		return Self(
			list: { ordering in
				let bowlers = database.reader().observe {
					let leagues = Bowler.Database.trackableLeagues(filter: nil)
					let series = Bowler.Database.trackableSeries(through: leagues, filter: nil)
					let games = Bowler.Database.trackableGames(through: series, filter: nil)
					let averageScore = games
						.average(Game.Database.Columns.score)
						.forKey("average")

					return try Bowler.Database
						.all()
						.orderByName()
						.filter(byKind: .playable)
						.annotated(with: averageScore)
						.asRequest(of: Bowler.List.self)
						.fetchAll($0)
				}

				switch ordering {
				case .byName:
					return bowlers
				case .byRecentlyUsed:
					return sort(bowlers, byIds: recentlyUsed.observeRecentlyUsedIds(.bowlers))
				}
			},
			summaries: { kind, ordering in
				let bowlers = database.reader().observe {
					try Bowler.Database
						.all()
						.orderByName()
						.filter(byKind: kind)
						.asRequest(of: Bowler.Summary.self)
						.fetchAll($0)
				}

				switch ordering {
				case .byName:
					return bowlers
				case .byRecentlyUsed:
					return sort(bowlers, byIds: recentlyUsed.observeRecentlyUsedIds(.bowlers))
				}
			},
			opponents: { ordering in
				let bowlers = database.reader().observe {
					try Bowler.Database
						.all()
						.orderByName()
						.asRequest(of: Bowler.Opponent.self)
						.fetchAll($0)
				}

				switch ordering {
				case .byName:
					return bowlers
				case .byRecentlyUsed:
					return sort(bowlers, byIds: recentlyUsed.observeRecentlyUsedIds(.bowlers))
				}
			},
			fetchSummaries: { ids in
				let bowlers = try await database.reader().read {
					try Bowler.Database
						.all()
						.filter(ids: ids)
						.asRequest(of: Bowler.Summary.self)
						.fetchAll($0)
				}

				return bowlers.sortBy(ids: ids)
			},
			opponentRecord: { opponent in
				try await database.reader().read {
					let seriesAlias = TableAlias()

					// FIXME: filter out games that have leagues/series/games with excludeFromStatistics
					let allMatches = Bowler.Database.matchesAsOpponent
					let allGames = Bowler.Database.gamesAsOpponent
						.filter(Game.Database.Columns.excludeFromStatistics == Game.ExcludeFromStatistics.include)
						.joining(
							required: Game.Database.series
								.filter(Series.Database.Columns.excludeFromStatistics == Series.ExcludeFromStatistics.include)
						)
						.joining(
							required: Game.Database.league
								.filter(League.Database.Columns.excludeFromStatistics == League.ExcludeFromStatistics.include)
						)

					let matchesAgainst = allGames
						.annotated(withRequired: Game.Database.matchPlay.select(
							MatchPlay.Database.Columns.opponentScore,
							MatchPlay.Database.Columns.result
						))
						.joining(required: Game.Database.series.aliased(seriesAlias))
						.order(seriesAlias[Series.Database.Columns.date.desc])
						.forKey("matchesAgainst")

					let gamesPlayed = allMatches
						.forKey("gamesPlayed")
						.count
						.forKey("gamesPlayed")
					let gamesWon = allMatches
						.filter(MatchPlay.Database.Columns.result == MatchPlay.Result.won)
						.forKey("gamesWon")
						.count
						.forKey("gamesWon")
					let gamesLost = allMatches
						.filter(MatchPlay.Database.Columns.result == MatchPlay.Result.lost)
						.forKey("gamesLost")
						.count
						.forKey("gamesLost")
					let gamesTied = allMatches
						.filter(MatchPlay.Database.Columns.result == MatchPlay.Result.tied)
						.forKey("gamesTied")
						.count
						.forKey("gamesTied")

					return try Bowler.Database
						.filter(id: opponent)
						.including(all: matchesAgainst)
						.annotated(with: gamesPlayed, gamesWon, gamesTied, gamesLost)
						.asRequest(of: Bowler.OpponentDetails.self)
						.fetchOneGuaranteed($0)
				}
			},
			edit: { id in
				try await database.reader().read {
					try Bowler.Edit.fetchOneGuaranteed($0, id: id)
				}
			},
			create: { bowler in
				try await database.writer().write {
					try bowler.insert($0)
				}
			},
			update: { bowler in
				try await database.writer().write {
					try bowler.update($0)
				}
			},
			delete: { id in
				return try await database.writer().write {
					try Bowler.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
