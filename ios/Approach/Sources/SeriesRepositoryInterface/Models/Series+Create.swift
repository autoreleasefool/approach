import Foundation
import IdentifiedCollections
import ModelsLibrary

extension Series {
	public struct Create: Identifiable, Equatable, Codable, CanPreBowl {
		public let leagueId: League.ID
		public let id: Series.ID

		public var date: Date
		public var appliedDate: Date?
		public var preBowl: PreBowl
		public var excludeFromStatistics: ExcludeFromStatistics
		public var numberOfGames: Int
		public var location: Alley.Summary?

		public static func `default`(withId: UUID, onDate: Date, inLeague: League.SeriesHost) -> Self {
			.init(
				leagueId: inLeague.id,
				id: withId,
				date: onDate,
				appliedDate: nil,
				preBowl: .regular,
				excludeFromStatistics: .include,
				numberOfGames: inLeague.defaultNumberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES,
				location: inLeague.alley
			)
		}

		public var asSummary: Summary {
			.init(id: id, date: date)
		}

		public var asGameHost: GameHost {
			.init(id: id, date: date, appliedDate: appliedDate, preBowl: preBowl)
		}
	}
}
