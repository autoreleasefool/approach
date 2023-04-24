import ExtensionsLibrary
import ModelsLibrary

extension Frame.Edit {
	mutating func toggle(_ pin: Pin, rollIndex: Int, newValue: Bool? = nil) {
		guaranteeRollExists(upTo: rollIndex)
		rolls[rollIndex].toggle(pin, newValue: newValue)
	}

	mutating func guaranteeRollExists(upTo index: Int) {
		while rolls.count < index + 1 {
			rolls.append(.default)
		}
	}

	mutating func roll(at index: Int) -> Frame.Roll {
		guaranteeRollExists(upTo: index)
		return rolls[index]
	}
}

extension Frame.Roll {
	mutating func toggle(_ pin: Pin, newValue: Bool?) {
		if let newValue {
			if pinsDowned.contains(pin) != newValue {
				pinsDowned.toggle(pin)
			}
		} else {
			pinsDowned.toggle(pin)
		}
	}

	func isPinDown(_ pin: Pin) -> Bool {
		pinsDowned.contains(pin)
	}
}
