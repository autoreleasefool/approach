import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension TeamMembership.FetchRequest: SingleQueryableWithDefault {
	@Sendable func fetchValue(_ db: Database) throws -> TeamMembership? {
		// TODO: fetch members from database
		nil
	}

	@Sendable func makeDefault() -> TeamMembership {
		switch filter {
		case let .id(team):
			return .init(team: team, members: [])
		}
	}
}
