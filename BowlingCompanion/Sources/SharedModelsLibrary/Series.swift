import Foundation

public struct Series: Sendable, Identifiable, Hashable, Codable {
	public let league: League.ID
	public let id: UUID
	public let date: Date
	public let numberOfGames: Int
	public let alley: Alley.ID?

	public init(
		league: League.ID,
		id: UUID,
		date: Date,
		numberOfGames: Int,
		alley: Alley.ID?
	) {
		self.league = league
		self.id = id
		self.date = date
		self.numberOfGames = numberOfGames
		self.alley = alley
	}
}
