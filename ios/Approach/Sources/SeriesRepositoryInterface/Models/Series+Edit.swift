import Foundation
import IdentifiedCollections
import ModelsLibrary

extension Series {
	public struct Edit: Identifiable, Equatable, Codable, CanPreBowl {
		public let leagueId: League.ID
		public let id: Series.ID
		public let numberOfGames: Int
		public let leagueRecurrence: League.Recurrence

		public var date: Date
		public var appliedDate: Date?
		public var preBowl: PreBowl
		public var excludeFromStatistics: ExcludeFromStatistics
		public var location: Alley.Summary?

		public var asSummary: Summary {
			.init(id: id, date: date)
		}

		public var asGameHost: Series.GameHost {
			.init(id: id, date: date, appliedDate: appliedDate, preBowl: preBowl)
		}
	}
}
