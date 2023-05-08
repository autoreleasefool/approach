import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GamesRepositoryInterface
import GRDB
import ModelsLibrary
import RepositoryLibrary

extension GamesRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.database) var database

		return Self(
			list: { series, _ in
				database.reader().observe {
					try Game.Database
						.all()
						.orderByIndex()
						.filter(bySeries: series)
						.asRequest(of: Game.Summary.self)
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
							required: Game.Database.series
								.including(optional: Series.Database.alley)
								.including(
									all: Series.Database.lanes
										.orderByLabel()
								)
						)
						.asRequest(of: Game.Edit.self)
						.fetchOne($0)
				}
			},
			update: { game in
				try await database.writer().write {
					try game.update($0)
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
