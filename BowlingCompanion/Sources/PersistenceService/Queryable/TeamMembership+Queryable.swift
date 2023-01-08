import GRDB
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SharedModelsPersistableLibrary

extension TeamMembership.FetchRequest: SingleQueryableWithDefault {
	@Sendable func fetchValue(_ db: Database) throws -> TeamMembership? {
		var query: QueryInterfaceRequest<Bowler>
		let teamId: Team.ID

		switch filter {
		case let .team(team):
			teamId = team.id
			query = team.bowlers
		}

		switch ordering {
		case .byName:
			query = query.order(Column("name").collating(.localizedCaseInsensitiveCompare))
		}

		let bowlers = try query.fetchAll(db)
		return .init(team: teamId, members: bowlers)
	}

	@Sendable func makeDefault() -> TeamMembership {
		switch filter {
		case let .team(team):
			return .init(team: team.id, members: [])
		}
	}
}
