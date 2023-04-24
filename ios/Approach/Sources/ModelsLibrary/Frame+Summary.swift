extension Frame {
	public struct Summary: Identifiable, Decodable, Sendable, Equatable {
		public let gameId: Game.ID
		public let ordinal: Int
		public let rolls: [Roll]

		public var id: String { "\(gameId)-\(ordinal)" }

		public init(from decoder: Decoder) throws {
			let container = try decoder.container(keyedBy: CodingKeys.self)
			self.gameId = try container.decode(Game.ID.self, forKey: CodingKeys.gameId)
			self.ordinal = try container.decode(Int.self, forKey: CodingKeys.ordinal)

			let roll0 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll0)
			let roll1 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll1)
			let roll2 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll2)
			self.rolls = [roll0, roll1, roll2].compactMap { $0 }
		}

		enum CodingKeys: CodingKey {
			case gameId
			case ordinal
			case roll0
			case roll1
			case roll2
		}
	}
}
