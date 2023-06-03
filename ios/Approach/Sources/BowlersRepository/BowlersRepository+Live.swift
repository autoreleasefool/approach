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
						.filter(byStatus: .playable)
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
			summaries: { status, ordering in
				let bowlers = database.reader().observe {
					try Bowler.Database
						.all()
						.orderByName()
						.filter(byStatus: status)
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
			edit: { id in
				try await database.reader().read {
					try Bowler.Edit.fetchOne($0, id: id)
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
