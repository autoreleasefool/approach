import Foundation

public struct League: Sendable, Identifiable, Hashable, Codable {
	public static let DEFAULT_NUMBER_OF_GAMES = 4
	public static let NUMBER_OF_GAMES_RANGE = 1...40

	public let bowlerId: Bowler.ID
	public let id: UUID
	public let name: String
	public let recurrence: Recurrence
	public let numberOfGames: Int?
	public let additionalPinfall: Int?
	public let additionalGames: Int?
	public let alley: Alley.ID?

	public init(
		bowlerId: Bowler.ID,
		id: UUID,
		name: String,
		recurrence: Recurrence,
		numberOfGames: Int?,
		additionalPinfall: Int?,
		additionalGames: Int?,
		alley: Alley.ID?
	) {
		self.bowlerId = bowlerId
		self.id = id
		self.name = name
		self.recurrence = recurrence
		self.numberOfGames = numberOfGames
		self.additionalGames = additionalGames
		self.additionalPinfall = additionalPinfall
		self.alley = alley
	}
}

extension League {
	public enum Recurrence: Int, Sendable, Identifiable, CaseIterable, Codable, CustomStringConvertible {
		case repeating = 0
		case oneTimeEvent = 1

		public var id: Int { rawValue }
		public var description: String {
			switch self {
			case .repeating: return "Repeats"
			case .oneTimeEvent: return "Never"
			}
		}
	}
}
