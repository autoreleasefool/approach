import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import LocationsRepositoryInterface
import ModelsLibrary

extension LocationsRepository: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			insertOrUpdate: { location in
				@Dependency(DatabaseService.self) var database

				_ = try await database.writer().write {
					let exists = try location.exists($0)
					if exists {
						try location.update($0)
					} else {
						try location.insert($0)
					}
				}
			}
		)
	}()
}
