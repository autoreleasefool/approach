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
			create: { location in
				try await database.writer().write {
					try location.insert($0)
				}
			},
			update: { location in
				try await database.writer().write {
					try location.update($0)
				}
			}
		)
	}()
}
