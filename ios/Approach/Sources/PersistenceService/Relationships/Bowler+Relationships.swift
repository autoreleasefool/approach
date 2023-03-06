import GRDB
import SharedModelsLibrary

extension Bowler {
	static let memberships = hasMany(TeamMember.self)
	static let teams = hasMany(Team.self, through: memberships, using: TeamMember.team)
	static let leagues = hasMany(League.self)
	static let gear = hasMany(Gear.self)

	var leagues: QueryInterfaceRequest<League> {
		request(for: Bowler.leagues)
	}

	var gear: QueryInterfaceRequest<Gear> {
		request(for: Bowler.gear)
	}
}
