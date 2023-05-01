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
					try Game.Edit.fetchOne($0, id: id)
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
