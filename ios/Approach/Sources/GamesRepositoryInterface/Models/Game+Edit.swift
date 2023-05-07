import Foundation
import IdentifiedCollections
import ModelsLibrary

extension Game {
	public struct Edit: Identifiable, Codable, Equatable {
		public let id: Game.ID
		public let index: Int
		public let series: SeriesInfo

		public var locked: Game.Lock
		public var manualScore: Int?
		public var excludeFromStatistics: Game.ExcludeFromStatistics

		public init(
			id: Game.ID,
			index: Int,
			locked: Game.Lock,
			manualScore: Int?,
			excludeFromStatistics: Game.ExcludeFromStatistics,
			series: SeriesInfo
		) {
			self.id = id
			self.index = index
			self.locked = locked
			self.manualScore = manualScore
			self.excludeFromStatistics = excludeFromStatistics
			self.series = series
		}
	}
}

extension Game.Edit {
	public struct SeriesInfo: Codable, Equatable {
		public let date: Date
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
