import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GamesRepositoryInterface
import GRDB
import ModelsLibrary
import RepositoryLibrary

extension GamesRepository: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			list: { series, _ in
				@Dependency(\.database) var database
				return database.reader().observe {
					try Game.Summary
						.all()
						.orderByOrdinal()
						.filter(bySeries: series)
						.fetchAll($0)
				}
			},
			edit: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try Game.Edit.fetchOne($0, id: id)
				}
			},
			update: { game in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try game.update($0)
				}
			},
			delete: { id in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try Game.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
