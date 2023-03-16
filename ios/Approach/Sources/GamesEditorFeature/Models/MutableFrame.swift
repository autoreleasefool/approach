import ExtensionsLibrary
import SharedModelsLibrary

public struct MutableFrame: Equatable {
	public let ordinal: Int
	public var rolls: [MutableRoll]

	public init(from: Frame) {
		self.ordinal = from.ordinal
		self.rolls = from.rolls.map { .init(from: $0) }
	}

	public mutating func toggle(_ pin: Pin, rollIndex: Int, newValue: Bool? = nil) {
		guaranteeRollExists(upTo: rollIndex)
		rolls[rollIndex].toggle(pin, newValue: newValue)
	}

	public mutating func guaranteeRollExists(upTo index: Int) {
		while rolls.count < index + 1 {
			rolls.append(.init())
		}
	}

	public mutating func roll(at index: Int) -> MutableRoll {
		guaranteeRollExists(upTo: index)
		return rolls[index]
	}
}

public struct MutableRoll: Equatable {
	public var pinsDowned: [Pin]
	public var didFoul: Bool

	public init() {
		self.didFoul = false
		self.pinsDowned = []
	}

	public init(from: Frame.Roll) {
		self.didFoul = from.didFoul
		self.pinsDowned = from.pinsDowned
	}

	public mutating func toggle(_ pin: Pin, newValue: Bool?) {
		if let newValue {
			if pinsDowned.contains(pin) != newValue {
				pinsDowned.toggle(pin)
			}
		} else {
			pinsDowned.toggle(pin)
		}
	}

	public func isPinDown(_ pin: Pin) -> Bool {
		pinsDowned.contains(pin)
	}
}
