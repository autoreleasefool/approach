import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension Alley.FetchRequest: ManyQueryable {
	@Sendable func fetchValues(_ db: Database) throws -> [Alley] {
		var query = Alley.all()

		switch filter {
		case let .id(id):
			query = query.filter(id: id)
		case let .properties(material, pinFall, pinBase, mechanism):
			if let material {
				query = query.filter(Column("material") == material.rawValue)
			}
			if let pinFall {
				query = query.filter(Column("pinFall") == pinFall.rawValue)
			}
			if let pinBase {
				query = query.filter(Column("pinBase") == pinBase.rawValue)
			}
			if let mechanism {
				query = query.filter(Column("mechanism") == mechanism.rawValue)
			}
		case let .name(name):
			query = query.filter(Column("name").like(name))
		case .none:
			break
		}

		switch ordering {
		case .byName, .byRecentlyUsed:
			query = query.order(Column("name").collating(.localizedCaseInsensitiveCompare))
		}

		return try query.fetchAll(db)
	}
}
