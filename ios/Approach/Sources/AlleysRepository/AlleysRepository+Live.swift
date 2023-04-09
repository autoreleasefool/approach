import AlleysRepositoryInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import ModelsLibrary
import RecentlyUsedServiceInterface
import RepositoryLibrary

extension AlleysRepository: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			all: { properties, ordering in
				@Dependency(\.database) var database

				let alleys = database.reader().observe {
					try Alley.DatabaseModel
						.all()
						.orderByName()
						.filter(byProperties: properties)
						.fetchAll($0)
						.map(Alley.Summary.init)
				}

				switch ordering {
				case .byName:
					return alleys
				case .byRecentlyUsed:
					@Dependency(\.recentlyUsedService) var recentlyUsed
					return sort(alleys, byIds: recentlyUsed.observeRecentlyUsedIds(.alleys))
				}
			},
			load: { id in
				@Dependency(\.database) var database
				return database.reader().observeOne {
					try Alley.DatabaseModel.fetchOne($0, id: id).map(Alley.Summary.init)
				}
			},
			edit: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try .init(Alley.DatabaseModel.fetchOne($0, id: id))
				}
			},
			save: { alley in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try alley.databaseModel.save($0)
				}
			},
			delete: { id in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try Alley.DatabaseModel.deleteOne($0, id: id)
				}
			}
		)
	}()
}
