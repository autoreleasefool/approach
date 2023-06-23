import Foundation
import ModelsLibrary

extension Frame {
	public struct TrackableEntry: Identifiable, Decodable, InspectableFrame {
		public let seriesId: Series.ID
		public let gameId: Game.ID
		public let index: Int
		public let rolls: [OrderedRoll]
		public let date: Date

		public var id: String { Frame.buildId(game: gameId, index: index) }

		init(seriesId: Series.ID, gameId: Game.ID, index: Int, rolls: [OrderedRoll], date: Date) {
			self.seriesId = seriesId
			self.gameId = gameId
			self.index = index
			self.rolls = rolls
			self.date = date
		}

		public init(from decoder: Decoder) throws {
			let container = try decoder.container(keyedBy: CodingKeys.self)
			self.seriesId = try container.decode(Series.ID.self, forKey: CodingKeys.seriesId)
			self.gameId = try container.decode(Game.ID.self, forKey: CodingKeys.gameId)
			self.index = try container.decode(Int.self, forKey: CodingKeys.index)
			self.date = try container.decode(Date.self, forKey: CodingKeys.date)

			let roll0 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll0)
			let roll1 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll1)
			let roll2 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll2)
			let ball0 = try container.decodeIfPresent(Gear.Named.self, forKey: CodingKeys.bowlingBall0)
			let ball1 = try container.decodeIfPresent(Gear.Named.self, forKey: CodingKeys.bowlingBall1)
			let ball2 = try container.decodeIfPresent(Gear.Named.self, forKey: CodingKeys.bowlingBall2)
			let rolls = [roll0, roll1, roll2]
			let bowlingBalls = [ball0, ball1, ball2]
			self.rolls = zip(rolls, bowlingBalls).enumerated().compactMap {
				guard let roll = $0.element.0 else { return nil }
				return .init(index: $0.offset, roll: roll, bowlingBall: $0.element.1)
			}
		}

		enum CodingKeys: CodingKey {
			case seriesId
			case gameId
			case index
			case date
			case roll0
			case roll1
			case roll2
			case ball0
			case ball1
			case ball2
			case bowlingBall0
			case bowlingBall1
			case bowlingBall2
		}
	}
}
