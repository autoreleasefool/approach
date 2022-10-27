import Foundation

public struct League: Sendable, Identifiable, Hashable, Codable {
	public static let DEFAULT_NUMBER_OF_GAMES = 4

	public let bowlerId: Bowler.ID
	public let id: UUID
	public let name: String
	public let recurrence: Recurrence
	public let numberOfGames: Int?
	public let additionalPinfall: Int?
	public let additionalGames: Int?
	public let createdAt: Date
	public let lastModifiedAt: Date

	public init(
		bowlerId: Bowler.ID,
		id: UUID,
		name: String,
		recurrence: Recurrence,
		numberOfGames: Int?,
		additionalPinfall: Int?,
		additionalGames: Int?,
		createdAt: Date,
		lastModifiedAt: Date
	) {
		self.bowlerId = bowlerId
		self.id = id
		self.name = name
		self.recurrence = recurrence
		self.numberOfGames = numberOfGames
		self.additionalGames = additionalGames
		self.additionalPinfall = additionalPinfall
		self.createdAt = createdAt
		self.lastModifiedAt = lastModifiedAt
	}
}

extension League {
	public enum Recurrence: String, Sendable, Identifiable, CaseIterable, Codable {
		case repeating = "Repeats"
		case oneTimeEvent = "Never"

		public var id: String { rawValue }
	}
}
