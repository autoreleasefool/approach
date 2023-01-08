import GRDB
import SharedModelsLibrary

extension Team {
	static let members = hasMany(TeamMember.self)
	static let bowlers = hasMany(Bowler.self, through: members, using: TeamMember.bowler)

	var bowlers: QueryInterfaceRequest<Bowler> {
		request(for: Team.bowlers)
	}
}
