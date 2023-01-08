import Foundation

public struct TeamMember: Sendable, Hashable, Codable {
	public let teamId: Team.ID
	public let bowlerId: Bowler.ID

	public init(
		teamId: Team.ID,
		bowlerId: Bowler.ID
	) {
		self.teamId = teamId
		self.bowlerId = bowlerId
	}
}
