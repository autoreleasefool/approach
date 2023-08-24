import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import LocationsRepositoryInterface
import ModelsLibrary

extension LocationsRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.database) var database

		return Self(
			insertOrUpdate: { location in
				try await database.writer().write {
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
