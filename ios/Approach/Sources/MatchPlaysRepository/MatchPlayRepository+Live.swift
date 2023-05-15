import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import ModelsLibrary
import MatchPlaysRepositoryInterface

extension MatchPlaysRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.database) var database

		return Self(
			create: { matchPlay in
				try await database.writer().write {
					try matchPlay.insert($0)
				}
			},
			update: { matchPlay in
				try await database.writer().write {
					try matchPlay.update($0)
				}
			},
			delete: { id in
				return try await database.writer().write {
					try MatchPlay.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
