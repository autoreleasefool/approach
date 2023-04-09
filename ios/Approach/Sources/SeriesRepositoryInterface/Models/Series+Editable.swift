import Foundation
import ModelsLibrary

extension Series {
	public struct Editable: Identifiable, Equatable, Codable {
		public let league: League.ID
		public let id: Series.ID
		public var date: Date
		public var numberOfGames: Int
		public var preBowl: PreBowl
		public var excludeFromStatistics: ExcludeFromStatistics
		public var alley: Alley.ID?

		public init(
			league: League.ID,
			id: Series.ID,
			date: Date,
			numberOfGames: Int,
			preBowl: PreBowl,
			excludeFromStatistics: ExcludeFromStatistics,
			alley: Alley.ID?
		) {
			self.league = league
			self.id = id
			self.date = date
			self.numberOfGames = numberOfGames
			self.preBowl = preBowl
			self.excludeFromStatistics = excludeFromStatistics
			self.alley = alley
		}
	}
}
