import Foundation
import StringsLibrary

public struct League: Sendable, Identifiable, Hashable, Codable {
	public static let DEFAULT_NUMBER_OF_GAMES = 4
	public static let NUMBER_OF_GAMES_RANGE = 1...40

	public let bowler: Bowler.ID
	public let id: UUID
	public let name: String
	public let recurrence: Recurrence
	public let numberOfGames: Int?
	public let additionalPinfall: Int?
	public let additionalGames: Int?
	public let excludeFromStatistics: ExcludeFromStatistics
	public let alley: Alley.ID?

	public init(
		bowler: Bowler.ID,
		id: UUID,
		name: String,
		recurrence: Recurrence,
		numberOfGames: Int?,
		additionalPinfall: Int?,
		additionalGames: Int?,
		excludeFromStatistics: ExcludeFromStatistics,
		alley: Alley.ID?
	) {
		self.bowler = bowler
		self.id = id
		self.name = name
		self.recurrence = recurrence
		self.numberOfGames = numberOfGames
		self.additionalGames = additionalGames
		self.additionalPinfall = additionalPinfall
		self.excludeFromStatistics = excludeFromStatistics
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
			case .repeating: return Strings.League.Properties.Recurrence.repeats
			case .oneTimeEvent: return Strings.League.Properties.Recurrence.never
			}
		}
	}
}

extension League {
	public enum ExcludeFromStatistics: Int, Sendable, Identifiable, CaseIterable, Codable, CustomStringConvertible {
		case include = 0
		case exclude = 1

		public var id: Int { rawValue }
		public var description: String {
			switch self {
			case .include: return Strings.League.Properties.ExcludeFromStatistics.include
			case .exclude: return Strings.League.Properties.ExcludeFromStatistics.exclude
			}
		}
	}
}
