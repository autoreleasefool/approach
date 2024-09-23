import ScoreKeeperModelsLibrary

extension Frame {
	public static func buildId(game: Game.ID, index: Int) -> String {
		"\(game)-\(index)"
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
			let bools = [didFoul] + Pin.allCases.map { pinsDowned.contains($0) }
			let bitString = bools.map { $0 ? "1" : "0" }.joined()
			var container = encoder.singleValueContainer()
			try container.encode(bitString)
		}

		public static let `default` = Self(pinsDowned: [], didFoul: false)
	}

	public struct OrderedRoll: Sendable, Identifiable, Equatable {
		public let index: Int
		public var roll: Roll
		public var bowlingBall: Gear.Summary?

		public var id: Int { index }

		public init(index: Int, roll: Roll, bowlingBall: Gear.Summary?) {
			self.index = index
			self.roll = roll
			self.bowlingBall = bowlingBall
		}

		public var displayValue: String {
			roll.pinsDowned.displayValue(rollIndex: index)
		}
	}
}

// MARK: - InspectableFrame

public protocol InspectableFrame {
	var index: Int { get }
	var rolls: [Frame.OrderedRoll] { get }

	var firstRolls: [Frame.OrderedRoll] { get }
	var secondRolls: [Frame.OrderedRoll] { get }
	var pinsLeftOnDeck: Set<Pin> { get }
}

public struct RollPair: Equatable, Sendable {
	public let firstRoll: Frame.OrderedRoll
	public let secondRoll: Frame.OrderedRoll
}

extension InspectableFrame {
	public var firstRolls: [Frame.OrderedRoll] {
		guard let firstRoll = rolls.first else { return [] }

		if Frame.isLast(index) {
			var firstRolls = [firstRoll]
			var pinsDowned: Set<Pin> = []
			for (index, roll) in rolls.enumerated() {
				pinsDowned.formUnion(roll.roll.pinsDowned)
				if pinsDowned.arePinsCleared && index < rolls.endIndex - 1 {
					firstRolls.append(rolls[index + 1])
					pinsDowned = []
				}
			}
			return firstRolls
		} else {
			return [firstRoll]
		}
	}

	public var secondRolls: [Frame.OrderedRoll] {
		guard let secondRoll = rolls.dropFirst().first else { return [] }

		if Frame.isLast(index) {
			var secondRolls: [Frame.OrderedRoll] = []
			var pinsDowned: Set<Pin> = []
			var pinsJustCleared = true
			for (index, roll) in rolls.enumerated() {
				pinsDowned.formUnion(roll.roll.pinsDowned)
				if pinsDowned.arePinsCleared {
					pinsJustCleared = true
					pinsDowned = []
				} else {
					if pinsJustCleared && index < rolls.endIndex - 1 {
						secondRolls.append(rolls[index + 1])
					}
					pinsJustCleared = false
				}
			}
			return secondRolls
		} else {
			return [secondRoll]
		}
	}

	public var rollPairs: [RollPair] {
		let firstRolls = self.firstRolls
		let secondRolls = self.secondRolls
		return secondRolls.compactMap { secondRoll in
			guard
				let matchingFirstRoll = firstRolls.first(where: { $0.index == secondRoll.index - 1 }),
				!matchingFirstRoll.roll.pinsDowned.arePinsCleared
			else {
				return nil
			}
			return RollPair(firstRoll: matchingFirstRoll, secondRoll: secondRoll)
		}
	}

	public var pinsLeftOnDeck: Set<Pin> {
		if Frame.isLast(index) {
			var pinsStanding = Pin.fullDeck
			for (index, roll) in rolls.enumerated() {
				pinsStanding.subtract(roll.roll.pinsDowned)
				if pinsStanding.isEmpty && index < Frame.NUMBER_OF_ROLLS - 1 {
					pinsStanding = Pin.fullDeck
				}
			}

			return pinsStanding
		} else {
			return rolls.reduce(into: Pin.fullDeck) { standing, roll in standing.subtract(roll.roll.pinsDowned) }
		}
	}
}

// MARK: - Summary

extension Frame {
	public struct Summary: Identifiable, Decodable, Sendable, Equatable, InspectableFrame {
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
			let ball0 = try container.decodeIfPresent(Gear.Summary.self, forKey: CodingKeys.bowlingBall0)
			let ball1 = try container.decodeIfPresent(Gear.Summary.self, forKey: CodingKeys.bowlingBall1)
			let ball2 = try container.decodeIfPresent(Gear.Summary.self, forKey: CodingKeys.bowlingBall2)
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
