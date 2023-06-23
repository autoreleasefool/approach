import Foundation

public enum Pin: Int, Equatable, Sendable, Identifiable, Codable, CaseIterable {
	case leftTwoPin = 0
	case leftThreePin = 1
	case headPin = 2
	case rightThreePin = 3
	case rightTwoPin = 4

	public var id: Int { rawValue }

	public var value: Int {
		switch self {
		case .leftTwoPin, .rightTwoPin: return 2
		case .leftThreePin, .rightThreePin: return 3
		case .headPin: return 5
		}
	}

	public static let fullDeck: Set<Self> = [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin]
}

extension Set where Element == Pin {
	public var value: Int { reduce(0) { value, pin in value + pin.value } }
	public var isHeadPin: Bool { count == 1 && first == .headPin }
	public var isHeadPin2: Bool { value == 7 && contains(.headPin) }
	public var isLeft: Bool { count == 4 && !contains(.leftTwoPin) }
	public var isRight: Bool { count == 4 && !contains(.rightTwoPin) }
	public var isAce: Bool { value == 11 }
	public var isLeftChopOff: Bool {
		count == 3 && contains(.headPin) && contains(.leftTwoPin) && contains(.leftThreePin)
	}
	public var isRightChopOff: Bool {
		count == 3 && contains(.headPin) && contains(.rightTwoPin) && contains(.rightThreePin)
	}
	public var isChopOff: Bool { isLeftChopOff || isRightChopOff }
	public var isLeftSplit: Bool { count == 2 && contains(.headPin) && contains(.leftThreePin) }
	public var isLeftSplitWithBonus: Bool {
		count == 3 && contains(.headPin) && contains(.leftThreePin) && contains(.rightTwoPin)
	}
	public var isRightSplit: Bool { count == 2 && contains(.headPin) && contains(.rightThreePin) }
	public var isRightSplitWithBonus: Bool {
		count == 3 && contains(.headPin) && contains(.rightThreePin) && contains(.leftTwoPin)
	}
	public var isSplit: Bool { isLeftSplit || isRightSplit }
	public var isSplitWithBonus: Bool { isLeftSplitWithBonus || isRightSplitWithBonus }
	public var isHitLeftOfMiddle: Bool { !contains(.headPin) && (contains(.leftTwoPin) || contains(.leftThreePin)) }
	public var isHitRightOfMiddle: Bool { !contains(.headPin) && (contains(.rightTwoPin) || contains(.rightThreePin)) }
	public var isMiddleHit: Bool { contains(.headPin) }
	public var isLeftTwelve: Bool { count == 4 && !contains(.rightThreePin) }
	public var isRightTwelve: Bool { count == 4 && !contains(.leftThreePin) }
	public var isTwelve: Bool { isLeftTwelve || isRightTwelve }
	public var arePinsCleared: Bool { count == 5 }
}

extension Set where Element == Pin {
	public func displayValue(rollIndex: Int) -> String {
		let outcome: RollOutcome
		if isHeadPin {
			outcome = .headPin
		} else if isHeadPin2 {
			outcome = .headPin2
		} else if isSplit {
			outcome = .split
		} else if isSplitWithBonus {
			outcome = .splitWithBonus
		} else if isChopOff {
			outcome = .chopOff
		} else if isAce {
			outcome = .ace
		} else if isLeft {
			outcome = .left
		} else if isRight {
			outcome = .right
		} else if arePinsCleared {
			switch rollIndex {
			case 0: outcome = .strike
			case 1: outcome = .spare
			default: outcome = .cleared
			}
		} else {
			outcome = .none
		}

		if outcome == .none {
			let value = self.value
			return value == 0 ? outcome.rawValue : String(value)
		} else {
			return rollIndex == 0 ? outcome.rawValue : outcome.numeral
		}
	}
}

public enum RollOutcome: String {
	case strike = "X"
	case spare = "/"
	case left = "L"
	case right = "R"
	case ace = "A"
	case chopOff = "C/O"
	case split = "HS"
	case splitWithBonus = "10"
	case headPin = "HP"
	case headPin2 = "H2"
	case cleared = "15"
	case none = "-"

	public var numeral: String {
		switch self {
		case .strike, .spare: return rawValue
		case .cleared: return "15"
		case .left, .right: return "13"
		case .ace: return "11"
		case .chopOff, .splitWithBonus: return "10"
		case .split: return "9"
		case .headPin2: return "7"
		case .headPin: return "5"
		case .none: return "-"
		}
	}
}
