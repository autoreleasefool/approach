import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import MatchPlaysRepositoryInterface
import ModelsLibrary

extension MatchPlaysRepository: DependencyKey {
	public static var liveValue: Self {
		Self(
			create: { matchPlay in
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					try matchPlay.insert($0)
				}
			},
			update: { matchPlay in
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					try matchPlay.update($0)
				}
			},
			delete: { id in
				@Dependency(DatabaseService.self) var database

				_ = try await database.writer().write {
					try MatchPlay.Database.deleteOne($0, id: id)
				}
			}
		)
	}
}
