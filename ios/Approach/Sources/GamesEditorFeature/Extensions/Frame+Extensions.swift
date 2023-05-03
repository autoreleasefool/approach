import ExtensionsLibrary
import ModelsLibrary

extension Frame.Edit {
	mutating func toggle(_ pin: Pin, rollIndex: Int, newValue: Bool? = nil) {
		guaranteeRollExists(upTo: rollIndex)
		rolls[rollIndex].toggle(pin, newValue: newValue)
	}

	mutating func guaranteeRollExists(upTo index: Int) {
		while rolls.count < index + 1 {
			rolls.append(.init(index: rolls.count, roll: .default))
		}
	}

	mutating func roll(at index: Int) -> Frame.Roll {
		guaranteeRollExists(upTo: index)
		return rolls[index].roll
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
	func isPinDown(_ pin: Pin) -> Bool {
		pinsDowned.contains(pin)
	}
}
