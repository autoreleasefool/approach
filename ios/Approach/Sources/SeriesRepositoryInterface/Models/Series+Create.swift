import Foundation
import IdentifiedCollections
import ModelsLibrary

extension Series {
	public struct Create: Identifiable, Equatable, Codable {
		public let leagueId: League.ID
		public let id: Series.ID

		public var date: Date
		public var preBowl: PreBowl
		public var excludeFromStatistics: ExcludeFromStatistics
		public var numberOfGames: Int
		public var location: Alley.Summary?

		public static func `default`(withId: UUID, onDate: Date, inLeague: League.SeriesHost) -> Self {
			.init(
				leagueId: inLeague.id,
				id: withId,
				date: onDate,
				preBowl: .regular,
				excludeFromStatistics: .include,
				numberOfGames: inLeague.numberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES,
				location: inLeague.alley
			)
		}
	}
}

extension Series {
	public struct CreateWithLanes: Equatable {
		public var series: Create
		public var lanes: IdentifiedArrayOf<Lane.Summary>

		public init(series: Create, lanes: IdentifiedArrayOf<Lane.Summary>) {
			self.series = series
			self.lanes = lanes
		}
	}
}
