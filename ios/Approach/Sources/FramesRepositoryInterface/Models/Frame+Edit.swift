import ModelsLibrary

extension Frame {
	public struct Edit: Identifiable, Equatable, Codable {
		public let gameId: Game.ID
		public let ordinal: Int
		public var rolls: [Roll]

		public var id: String { "\(gameId)-\(ordinal)" }

		init(gameId: Game.ID, ordinal: Int, rolls: [Roll]) {
			self.gameId = gameId
			self.ordinal = ordinal
			self.rolls = rolls
		}

		public init(from decoder: Decoder) throws {
			let container = try decoder.container(keyedBy: CodingKeys.self)
			self.gameId = try container.decode(Game.ID.self, forKey: CodingKeys.gameId)
			self.ordinal = try container.decode(Int.self, forKey: CodingKeys.ordinal)

			let roll0 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll0)
			let roll1 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll1)
			let roll2 = try container.decodeIfPresent(Roll.self, forKey: CodingKeys.roll2)
			self.rolls = [roll0, roll1, roll2].compactMap { $0 }
		}

		public func encode(to encoder: Encoder) throws {
			var container = encoder.container(keyedBy: CodingKeys.self)
			try container.encode(gameId, forKey: CodingKeys.gameId)
			try container.encode(ordinal, forKey: CodingKeys.ordinal)

			let roll0 = rolls.first
			let roll1 = rolls.dropFirst().first
			let roll2 = rolls.dropFirst(2).first
			try container.encodeIfPresent(roll0, forKey: CodingKeys.roll0)
			try container.encodeIfPresent(roll1, forKey: CodingKeys.roll1)
			try container.encodeIfPresent(roll2, forKey: CodingKeys.roll2)
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
