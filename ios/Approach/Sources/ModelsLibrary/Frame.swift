public enum Frame {}

extension Frame {
	public static let NUMBER_OF_ROLLS = 3
	public static let ROLL_INDICES = 0..<NUMBER_OF_ROLLS

	public static func isLast(_ index: Int) -> Bool {
		index == Game.NUMBER_OF_FRAMES - 1
	}

	public static func rollIndices(after: Int) -> Range<Int> {
		(after + 1)..<NUMBER_OF_ROLLS
	}

	public static func buildId(game: Game.ID, index: Int) -> String {
		"\(game)-\(index)"
	}
}

extension Frame.Roll {
	public static let FOUL_PENALTY = 15

	public static func isLast(_ index: Int) -> Bool {
		index == Frame.NUMBER_OF_ROLLS - 1
	}
}

extension Frame {
	public struct Roll: Sendable, Equatable, Codable {
		public var pinsDowned: Set<Pin>
		public var didFoul: Bool

		public init(pinsDowned: Set<Pin>, didFoul: Bool) {
			self.pinsDowned = pinsDowned
			self.didFoul = didFoul
		}

		public init(from bitString: String) {
			assert(bitString.count == 6)
			self.didFoul = bitString.first != "0"
			self.pinsDowned = Set(bitString.dropFirst().enumerated().compactMap { index, bit in
				bit == "0" ? nil : Pin(rawValue: index)
			})
		}

		public init(from decoder: Decoder) throws {
			let container = try decoder.singleValueContainer()
			let bitString = try container.decode(String.self)
			self.init(from: bitString)
		}

		public func encode(to encoder: Encoder) throws {
			let bools = [didFoul] + Pin.fullDeck.map { pinsDowned.contains($0) }
			let bitString = bools.map { $0 ? "1" : "0" }.joined()
			var container = encoder.singleValueContainer()
			try container.encode(bitString)
		}

		public static let `default` = Self(pinsDowned: [], didFoul: false)
	}

	public struct OrderedRoll: Sendable, Identifiable, Equatable {
		public let index: Int
		public var roll: Roll
		public var bowlingBall: Gear.Named?

		public var id: Int { index }

		public init(index: Int, roll: Roll, bowlingBall: Gear.Named?) {
			self.index = index
			self.roll = roll
			self.bowlingBall = bowlingBall
		}

		public var displayValue: String {
			roll.pinsDowned.displayValue(rollIndex: index)
		}
	}
}

extension Frame {
	public struct Summary: Identifiable, Decodable, Sendable, Equatable {
		public let gameId: Game.ID
		public let index: Int
		public let rolls: [OrderedRoll]

		public var id: String { Frame.buildId(game: gameId, index: index) }

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
			case gameId
			case index
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
