public struct TeamMembership: Sendable, Hashable {
	let team: Team.ID
	let members: [Bowler]

	public init(team: Team.ID, members: [Bowler]) {
		self.team = team
		self.members = members
	}
}
