import Foundation

public struct Series: Sendable, Identifiable, Hashable, Codable {
	public let leagueId: League.ID
	public let id: UUID
	public let date: Date
	public let numberOfGames: Int

	public init(leagueId: League.ID, id: UUID, date: Date, numberOfGames: Int) {
		self.leagueId = leagueId
		self.id = id
		self.date = date
		self.numberOfGames = numberOfGames
	}
}
