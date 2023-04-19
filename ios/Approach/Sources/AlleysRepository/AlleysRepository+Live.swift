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
			list: { material, pinFall, mechanism, pinBase, ordering in
				@Dependency(\.database) var database

				let alleys = database.reader().observe {
					try Alley.Database
						.all()
						.orderByName()
						.filter(material, pinFall, mechanism, pinBase)
						.asRequest(of: Alley.Summary.self)
						.fetchAll($0)
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
					try Alley.Summary.fetchOne($0, id: id)
				}
			},
			edit: { id in
				@Dependency(\.database) var database
				return try await database.reader().read {
					try Alley.Database
						.filter(Alley.Database.Columns.id == id)
						.asRequest(of: Alley.Edit.self)
						.fetchOne($0)
				}
			},
			create: { alley in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try alley.insert($0)
				}
			},
			update: { alley in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try alley.update($0)
				}
			},
			delete: { id in
				@Dependency(\.database) var database
				return try await database.writer().write {
					try Alley.Database.deleteOne($0, id: id)
				}
			}
		)
	}()
}
