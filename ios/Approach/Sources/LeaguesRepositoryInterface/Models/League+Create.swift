import Foundation
import ModelsLibrary

extension League {
	public struct Create: Identifiable, Codable, Equatable {
		public let bowlerId: Bowler.ID
		public let id: League.ID

		public var name: String
		public var recurrence: Recurrence
		public var defaultNumberOfGames: Int?
		public var additionalPinfall: Int?
		public var additionalGames: Int?
		public var excludeFromStatistics: ExcludeFromStatistics
		public var location: Alley.Summary?

		public static func `default`(withId: League.ID, forBowler: Bowler.ID) -> Self {
			.init(
				bowlerId: forBowler,
				id: withId,
				name: "",
				recurrence: .repeating,
				defaultNumberOfGames: League.DEFAULT_NUMBER_OF_GAMES,
				excludeFromStatistics: .include
			)
		}
	}
}
