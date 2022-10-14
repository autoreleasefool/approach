import Foundation

public struct League: Sendable, Identifiable, Hashable {
	public let id: UUID
	public let name: String
	public let recurrence: Recurrence
	public let numberOfGames: Int
	public let additionalPinfall: Int
	public let additionalGames: Int

	public init(
		id: UUID,
		name: String,
		recurrence: Recurrence,
		numberOfGames: Int,
		additionalPinfall: Int,
		additionalGames: Int
	) {
		self.id = id
		self.name = name
		self.recurrence = recurrence
		self.numberOfGames = numberOfGames
		self.additionalGames = additionalGames
		self.additionalPinfall = additionalPinfall
	}
}

extension League {
	public enum Recurrence: Sendable {
		case repeating
		case oneTimeEvent
	}
}
