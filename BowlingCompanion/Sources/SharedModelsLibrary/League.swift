import Foundation

public struct League: Sendable, Identifiable, Hashable {
	public static let DEFAULT_NUMBER_OF_GAMES = 4

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
	public enum Recurrence: Sendable, Identifiable, CaseIterable, CustomStringConvertible {
		case repeating
		case oneTimeEvent

		public var id: String {
			self.description
		}

		public var description: String {
			switch self {
			case .repeating:
				return "Repeats"
			case .oneTimeEvent:
				return "Never"
			}
		}
	}
}
