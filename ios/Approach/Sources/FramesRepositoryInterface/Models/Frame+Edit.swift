import ExtensionsPackageLibrary
import ModelsLibrary

extension Frame {
	public struct Edit: Identifiable, Equatable, Codable, Sendable {
		public let gameId: Game.ID
		public let index: Int
		public internal(set) var rolls: [OrderedRoll]

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

extension Frame.Edit {
	public var hasUntouchedRoll: Bool {
		firstUntouchedRoll != nil
	}

	public var firstUntouchedRoll: Int? {
		guard rolls.count < Frame.NUMBER_OF_ROLLS else { return nil }
		if Frame.isLast(index) {
			return rolls.endIndex
		} else {
			return deck(forRoll: rolls.endIndex - 1).arePinsCleared ? nil : rolls.endIndex
		}
	}

	public mutating func setBowlingBall(_ bowlingBall: Gear.Summary?, forRoll rollIndex: Int) {
		rolls[rollIndex].bowlingBall = bowlingBall
	}

	public mutating func setDidFoul(_ didFoul: Bool, forRoll rollIndex: Int) {
		rolls[rollIndex].roll.didFoul = didFoul
	}

	public mutating func setDownedPins(rollIndex: Int, to downedPins: Set<Pin>) {
		rolls[rollIndex].roll.pinsDowned = downedPins
		let clearRolledBall = !(Frame.isLast(index) || Frame.isLastRoll(index)) && deck(forRoll: rollIndex).arePinsCleared
		for roll in (rollIndex + 1..<rolls.endIndex) {
			rolls[roll].roll.pinsDowned.subtract(downedPins)
			if clearRolledBall {
				rolls[roll].bowlingBall = nil
			}
		}
	}

	public mutating func guaranteeRollExists(upTo index: Int) {
		while rolls.count < index + 1 {
			rolls.append(.init(index: rolls.count, roll: .default, bowlingBall: nil))
		}
	}

	public func deck(forRoll index: Int) -> Set<Pin> {
		rolls.prefix(index + 1).reduce(into: Set(), { deck, roll in
			// For the last frame, if the deck is cleared in a previous roll,
			// we don't include those in the standing deck for the current roll
			if Frame.isLast(self.index) && deck.count == 5 {
				deck.removeAll()
			}
			deck.formUnion(roll.roll.pinsDowned)
		})
	}

	public var lastAccessibleRollIndex: Int {
		if !Frame.isLast(index) {
			var deck: Set<Pin> = []
			for roll in rolls {
				deck.formUnion(roll.roll.pinsDowned)
				if deck.arePinsCleared {
					return roll.index
				}
			}
		}

		return Frame.ROLL_INDICES.last ?? 0
	}
}

extension Array where Element == Frame.Edit {
	public func nextIndexToRecord() -> Index {
		let lastFrameWithRolls = self.lastIndex { !$0.rolls.isEmpty }
		guard let lastFrameWithRolls else { return startIndex }
		return self[lastFrameWithRolls].hasUntouchedRoll
			? lastFrameWithRolls
			: Swift.min(index(after: lastFrameWithRolls), endIndex - 1)
	}

	public func nextFrameToRecord() -> Frame.Edit {
		self[nextIndexToRecord()]
	}
}

extension Frame.OrderedRoll {
	mutating func toggle(_ pin: Pin, newValue: Bool?) {
		if let newValue {
			if roll.pinsDowned.contains(pin) != newValue {
				roll.pinsDowned.toggle(pin)
			}
		} else {
			roll.pinsDowned.toggle(pin)
		}
	}
}

extension Frame.Roll {
	public func isPinDown(_ pin: Pin) -> Bool {
		pinsDowned.contains(pin)
	}
}
