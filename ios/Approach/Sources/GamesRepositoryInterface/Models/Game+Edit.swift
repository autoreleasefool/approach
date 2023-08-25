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

		public var score: Int
		public var locked: Game.Lock
		public var scoringMethod: Game.ScoringMethod
		public var excludeFromStatistics: Game.ExcludeFromStatistics
		public var matchPlay: MatchPlay.Edit?
		public var gear: IdentifiedArrayOf<Gear.Summary>
		public var lanes: IdentifiedArrayOf<LaneInfo>

		public init(
			id: Game.ID,
			index: Int,
			score: Int,
			locked: Game.Lock,
			scoringMethod: Game.ScoringMethod,
			excludeFromStatistics: Game.ExcludeFromStatistics,
			matchPlay: MatchPlay.Edit?,
			gear: IdentifiedArrayOf<Gear.Summary>,
			lanes: IdentifiedArrayOf<LaneInfo>,
			bowler: BowlerInfo,
			league: LeagueInfo,
			series: SeriesInfo
		) {
			self.id = id
			self.index = index
			self.score = score
			self.locked = locked
			self.scoringMethod = scoringMethod
			self.excludeFromStatistics = excludeFromStatistics
			self.matchPlay = matchPlay
			self.gear = gear
			self.lanes = lanes
			self.series = series
			self.bowler = bowler
			self.league = league
		}
	}
}

extension Game.Edit {
	public struct SeriesInfo: Codable, Equatable {
		public let date: Date
		public let preBowl: Series.PreBowl
		public let excludeFromStatistics: Series.ExcludeFromStatistics
		public let alley: Game.Edit.AlleyInfo?
	}
}

extension Game.Edit {
	public struct AlleyInfo: Identifiable, Codable, Equatable {
		public let id: Alley.ID
		public let name: String
	}
}

extension Game.Edit {
	public struct LaneInfo: Identifiable, Codable, Equatable {
		public let id: Lane.ID
		public let label: String
	}
}

extension Lane.Summary {
	public var asLaneInfo: Game.Edit.LaneInfo {
		.init(id: id, label: label)
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
