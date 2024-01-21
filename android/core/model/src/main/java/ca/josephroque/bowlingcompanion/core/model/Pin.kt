package ca.josephroque.bowlingcompanion.core.model

enum class Pin(val pinCount: Int) {
	LEFT_TWO_PIN(2),
	LEFT_THREE_PIN(3),
	HEAD_PIN(5),
	RIGHT_THREE_PIN(3),
	RIGHT_TWO_PIN(2);

	companion object {
		fun fullDeck(): Set<Pin> = entries.toSet()
	}
}

fun Set<Pin>.pinCount(): Int = sumOf(Pin::pinCount)

fun Set<Pin>.isHeadPin(): Boolean = size == 1 && contains(Pin.HEAD_PIN)
fun Set<Pin>.isHeadPin2(): Boolean = pinCount() == 7 && contains(Pin.HEAD_PIN)

fun Set<Pin>.isAce(): Boolean = pinCount() == 11

fun Set<Pin>.isLeftTap(): Boolean = size == 4 && !contains(Pin.LEFT_TWO_PIN)
fun Set<Pin>.isRightTap(): Boolean = size == 4 && !contains(Pin.RIGHT_TWO_PIN)
fun Set<Pin>.isTap(): Boolean = isLeftTap() || isRightTap()

fun Set<Pin>.isLeftChop(): Boolean = size == 3 && contains(Pin.HEAD_PIN) && contains(Pin.LEFT_TWO_PIN) && contains(Pin.LEFT_THREE_PIN)
fun Set<Pin>.isRightChop(): Boolean = size == 3 && contains(Pin.HEAD_PIN) && contains(Pin.RIGHT_TWO_PIN) && contains(Pin.RIGHT_THREE_PIN)
fun Set<Pin>.isChop(): Boolean = isLeftChop() || isRightChop()

fun Set<Pin>.isLeftSplit(): Boolean = size == 2 && contains(Pin.HEAD_PIN) && contains(Pin.LEFT_THREE_PIN)
fun Set<Pin>.isLeftSplitWithBonus(): Boolean = size == 3 && contains(Pin.HEAD_PIN) && contains(Pin.LEFT_THREE_PIN) && contains(Pin.RIGHT_TWO_PIN)
fun Set<Pin>.isRightSplit(): Boolean = size == 2 && contains(Pin.HEAD_PIN) && contains(Pin.RIGHT_THREE_PIN)
fun Set<Pin>.isRightSplitWithBonus(): Boolean = size == 3 && contains(Pin.HEAD_PIN) && contains(Pin.RIGHT_THREE_PIN) && contains(Pin.LEFT_TWO_PIN)
fun Set<Pin>.isSplit(): Boolean = isLeftSplit() || isRightSplit()
fun Set<Pin>.isSplitWithBonus(): Boolean = isLeftSplitWithBonus() || isRightSplitWithBonus()

fun Set<Pin>.isHitLeftOfMiddle(): Boolean = !contains(Pin.HEAD_PIN) && !contains(Pin.RIGHT_THREE_PIN) && (contains(Pin.LEFT_TWO_PIN) || contains(Pin.LEFT_THREE_PIN))
fun Set<Pin>.isHitRightOfMiddle(): Boolean = !contains(Pin.HEAD_PIN) && !contains(Pin.LEFT_THREE_PIN) && (contains(Pin.RIGHT_TWO_PIN) || contains(Pin.RIGHT_THREE_PIN))
fun Set<Pin>.isMiddleHit(): Boolean = contains(Pin.HEAD_PIN)

fun Set<Pin>.isLeftTwelve(): Boolean = size == 4 && !contains(Pin.RIGHT_THREE_PIN)
fun Set<Pin>.isRightTwelve(): Boolean = size == 4 && !contains(Pin.LEFT_THREE_PIN)
fun Set<Pin>.isTwelve(): Boolean = isLeftTwelve() || isRightTwelve()

fun Set<Pin>.isLeftFive(): Boolean = size == 2 && contains(Pin.LEFT_TWO_PIN) && contains(Pin.LEFT_THREE_PIN)
fun Set<Pin>.isRightFive(): Boolean = size == 2 && contains(Pin.RIGHT_TWO_PIN) && contains(Pin.RIGHT_THREE_PIN)
fun Set<Pin>.isFive(): Boolean = isLeftFive() || isRightFive()

fun Set<Pin>.isLeftThree(): Boolean = size == 1 && contains(Pin.LEFT_THREE_PIN)
fun Set<Pin>.isRightThree(): Boolean = size == 1 && contains(Pin.RIGHT_THREE_PIN)
fun Set<Pin>.isThree(): Boolean = isLeftThree() || isRightThree()

fun Set<Pin>.arePinsCleared(): Boolean = size == 5

fun Set<Pin>.displayAt(rollIndex: Int): String {
	val outcome: RollOutcome
	when {
		isHeadPin() -> outcome = RollOutcome.HEAD_PIN
		isHeadPin2() -> outcome = RollOutcome.HEAD_PIN_2
		isSplit() -> outcome = RollOutcome.SPLIT
		isSplitWithBonus() -> outcome = RollOutcome.SPLIT_WITH_BONUS
		isChop() -> outcome = RollOutcome.CHOP_OFF
		isAce() -> outcome = RollOutcome.ACE
		isLeftTap() -> outcome = RollOutcome.LEFT
		isRightTap() -> outcome = RollOutcome.RIGHT
		arePinsCleared() -> outcome = when (rollIndex) {
			0 -> RollOutcome.STRIKE
			1 -> RollOutcome.SPARE
			else -> RollOutcome.CLEARED
		}
		else -> outcome = RollOutcome.NONE
	}

	return if (outcome == RollOutcome.NONE) {
		val pinCount = this.pinCount()
		if (pinCount == 0) outcome.display else pinCount.toString()
	} else {
		if (rollIndex == 0) outcome.display else outcome.numericDisplay
	}
}

private enum class RollOutcome(val display: String, val numericDisplay: String) {
	STRIKE("X", "X"),
	SPARE("/", "/"),
	LEFT("L", "13"),
	RIGHT("R", "13"),
	ACE("A", "11"),
	CHOP_OFF("C/O", "10"),
	SPLIT("HS", "8"),
	SPLIT_WITH_BONUS("10", "10"),
	HEAD_PIN("HP", "5"),
	HEAD_PIN_2("H2", "7"),
	CLEARED("15", "15"),
	NONE("-", "-"),
}