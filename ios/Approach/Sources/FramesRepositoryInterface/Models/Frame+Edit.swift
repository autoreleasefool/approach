import ModelsLibrary

extension Frame {
	public struct Edit: Identifiable, Equatable, Codable {
		public let gameId: Game.ID
		public let index: Int
		public var rolls: [OrderedRoll]

		public var id: String { "\(gameId)-\(index)" }

		public init(gameId: Game.ID, index: Int, rolls: [OrderedRoll]) {
			self.gameId = gameId
			self.index = index
			self.rolls = rolls
		}

		public init(from decoder: Decoder) throws {
			let container = try decoder.container(keyedBy: CodingKeys.self)
			self.gameId = try container.decode(Game.ID.self, forKey: CodingKeys.gameId)
			self.index = try container.decode(Int.self, forKey: CodingKeys.index)

			let roll0 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll0)
			let roll1 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll1)
			let roll2 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll2)
			let ball0 = try container.decodeIfPresent(Gear.Rolled.self, forKey: CodingKeys.bowlingBall0)
			let ball1 = try container.decodeIfPresent(Gear.Rolled.self, forKey: CodingKeys.bowlingBall1)
			let ball2 = try container.decodeIfPresent(Gear.Rolled.self, forKey: CodingKeys.bowlingBall2)
			let rolls = [roll0, roll1, roll2]
			let bowlingBalls = [ball0, ball1, ball2]
			self.rolls = zip(rolls, bowlingBalls).enumerated().compactMap {
				guard let roll = $0.element.0 else { return nil }
				return .init(index: $0.offset, roll: roll, bowlingBall: $0.element.1)
			}
		}

		public func encode(to encoder: Encoder) throws {
			var container = encoder.container(keyedBy: CodingKeys.self)
			try container.encode(gameId, forKey: CodingKeys.gameId)
			try container.encode(index, forKey: CodingKeys.index)

			let roll0 = rolls.first
			let roll1 = rolls.dropFirst().first
			let roll2 = rolls.dropFirst(2).first
			try container.encodeIfPresent(roll0?.roll, forKey: CodingKeys.roll0)
			try container.encodeIfPresent(roll1?.roll, forKey: CodingKeys.roll1)
			try container.encodeIfPresent(roll2?.roll, forKey: CodingKeys.roll2)
			try container.encodeIfPresent(roll0?.bowlingBall?.id, forKey: CodingKeys.ball0)
			try container.encodeIfPresent(roll1?.bowlingBall?.id, forKey: CodingKeys.ball1)
			try container.encodeIfPresent(roll2?.bowlingBall?.id, forKey: CodingKeys.ball2)
		}

		enum CodingKeys: CodingKey {
			case gameId
			case index
			case roll0
			case roll1
			case roll2
			case bowlingBall0
			case bowlingBall1
			case bowlingBall2
			case ball0
			case ball1
			case ball2
		}
	}
}
