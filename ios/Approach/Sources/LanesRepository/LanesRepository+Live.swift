import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import LanesRepositoryInterface
import ModelsLibrary

extension LanesRepository: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			edit: { alley in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try Lane.Edit
						.all()
						.orderByLabel()
						.filter(byAlley: alley)
						.fetchAll($0)
				}
			},
			create: { lanes in
				@Dependency(\.database) var database
				return try await database.writer().write {
					for lane in lanes {
						try lane.insert($0)
					}
				}
			},
			update: { lanes in
				@Dependency(\.database) var database
				return try await database.writer().write {
					for lane in lanes {
						try lane.update($0)
					}
				}
			},
			delete: { ids in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try Lane.Database.deleteAll($0, ids: ids)
				}
			}
		)
	}()
}
