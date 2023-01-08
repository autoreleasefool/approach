import GRDB
import SharedModelsLibrary

extension Bowler {
	static let memberships = hasMany(TeamMember.self)
	static let teams = hasMany(Team.self, through: memberships, using: TeamMember.team)
}
