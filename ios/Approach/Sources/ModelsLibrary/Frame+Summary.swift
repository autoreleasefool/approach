extension Frame {
	public struct Summary: Identifiable, Decodable, Sendable, Equatable {
		public let gameId: Game.ID
		public let index: Int
		public let rolls: [OrderedRoll]

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
			self.rolls = [roll0, roll1, roll2].enumerated().compactMap {
				guard let roll = $0.element else { return nil }
				return .init(index: $0.offset, roll: roll)
			}
		}

		enum CodingKeys: CodingKey {
			case gameId
			case index
			case roll0
			case roll1
			case roll2
		}
	}
}
