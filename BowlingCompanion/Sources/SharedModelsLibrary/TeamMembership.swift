public struct TeamMembership: Sendable, Hashable, Codable {
	public let team: Team.ID
	public let members: [Bowler]

	public init(team: Team.ID, members: [Bowler]) {
		self.team = team
		self.members = members
	}
}
