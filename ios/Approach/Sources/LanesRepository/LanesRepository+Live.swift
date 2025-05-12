import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import LanesRepositoryInterface
import ModelsLibrary

extension LanesRepository: DependencyKey {
	public static var liveValue: Self {
		Self(
			list: { alley in
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					try Lane.Database
						.all()
						.filter { alley == nil || $0.alleyId == alley }
						.orderByLabel()
						.asRequest(of: Lane.Summary.self)
						.fetchAll($0)
				}
			},
			edit: { alley in
				@Dependency(DatabaseService.self) var database

				return try await database.reader().read {
					try Lane.Database
						.all()
						.filter { $0.alleyId == alley }
						.orderByLabel()
						.asRequest(of: Lane.Edit.self)
						.fetchAll($0)
				}
			},
			create: { lanes in
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					for lane in lanes {
						try lane.insert($0)
					}
				}
			},
			update: { lanes in
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					for lane in lanes {
						try lane.update($0)
					}
				}
			},
			delete: { ids in
				@Dependency(DatabaseService.self) var database

				_ = try await database.writer().write {
					try Lane.Database.deleteAll($0, ids: ids)
				}
			}
		)
	}
}
