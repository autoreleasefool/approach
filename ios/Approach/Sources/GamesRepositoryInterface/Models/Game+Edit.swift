import Foundation
import IdentifiedCollections
import MatchPlaysRepositoryInterface
import ModelsLibrary

extension Game {
	public struct Edit: Identifiable, Codable, Equatable {
		public let id: Game.ID
		public let index: Int
		public let bowler: BowlerInfo
		public let league: LeagueInfo
		public let series: SeriesInfo

		public var locked: Game.Lock
		public var manualScore: Int?
		public var excludeFromStatistics: Game.ExcludeFromStatistics
		public var matchPlay: MatchPlay.Edit?

		public init(
			id: Game.ID,
			index: Int,
			locked: Game.Lock,
			manualScore: Int?,
			excludeFromStatistics: Game.ExcludeFromStatistics,
			matchPlay: MatchPlay.Edit?,
			bowler: BowlerInfo,
			league: LeagueInfo,
			series: SeriesInfo
		) {
			self.id = id
			self.index = index
			self.locked = locked
			self.manualScore = manualScore
			self.excludeFromStatistics = excludeFromStatistics
			self.matchPlay = matchPlay
			self.series = series
			self.bowler = bowler
			self.league = league
		}
	}
}

extension Game.Edit {
	public struct SeriesInfo: Codable, Equatable {
		public let date: Date
		public let excludeFromStatistics: Series.ExcludeFromStatistics
		public let alley: Game.Edit.AlleyInfo?
		public let lanes: IdentifiedArrayOf<Game.Edit.LaneInfo>
	}
}

extension Game.Edit {
	public struct AlleyInfo: Codable, Equatable {
		public let name: String
	}
}

extension Game.Edit {
	public struct LaneInfo: Identifiable, Codable, Equatable {
		public let id: Lane.ID
		public let label: String
	}
}

extension Game.Edit {
	public struct BowlerInfo: Codable, Equatable {
		public let name: String
	}
}

extension Game.Edit {
	public struct LeagueInfo: Codable, Equatable {
		public let name: String
		public let excludeFromStatistics: League.ExcludeFromStatistics
	}
}
