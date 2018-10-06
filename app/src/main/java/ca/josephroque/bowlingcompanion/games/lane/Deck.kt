package ca.josephroque.bowlingcompanion.games.lane

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Alias for an array of pins.
 */

typealias Deck = Array<Pin>

// MARK: Pins

val Deck.left2Pin: Pin
    get() = this[0]

val Deck.left3Pin: Pin
    get() = this[1]

val Deck.headPin: Pin
    get() = this[2]

val Deck.right3Pin: Pin
    get() = this[3]

val Deck.right2Pin: Pin
    get() = this[4]

// MARK: First Ball

fun Deck.isHeadPin(countH2asH: Boolean): Boolean {
    return this.isHeadPin || (countH2asH && this.value(false) == 7 && this.headPin.isDown)
}

val Deck.isHeadPin: Boolean
    get() = this.value(false) == 5 && this.headPin.isDown

val Deck.isLeft: Boolean
    get() = this.value(false) == 13 && this.left2Pin.onDeck

val Deck.isRight: Boolean
    get() = this.value(false) == 13 && this.right2Pin.onDeck

val Deck.isAce: Boolean
    get() = this.value(false) == 11

val Deck.isLeftChopOff: Boolean
    get() = this.value(false) == 10 && this.left2Pin.isDown && this.left3Pin.isDown && this.headPin.isDown

val Deck.isRightChopOff: Boolean
    get() = this.value(false) == 10 && this.right2Pin.isDown && this.right3Pin.isDown && this.headPin.isDown

val Deck.isChopOff: Boolean
    get() = this.isLeftChopOff || this.isRightChopOff

fun Deck.isLeftSplit(countS2asS: Boolean): Boolean {
    return this.isLeftSplit || (countS2asS && this.value(false) == 10 && this.headPin.isDown && this.left3Pin.isDown && this.right2Pin.isDown)
}

private val Deck.isLeftSplit: Boolean
    get() = this.value(false) == 8 && this.headPin.isDown && this.left3Pin.isDown

fun Deck.isRightSplit(countS2asS: Boolean): Boolean {
    return this.isRightSplit || (countS2asS && this.value(false) == 10 && this.headPin.isDown && this.left2Pin.isDown && this.right3Pin.isDown)
}

private val Deck.isRightSplit: Boolean
    get() = this.value(false) == 8 && this.headPin.isDown && this.right3Pin.isDown

fun Deck.isSplit(countS2asS: Boolean): Boolean {
    return isLeftSplit(countS2asS) || isRightSplit(countS2asS)
}

val Deck.isHitLeftOfMiddle: Boolean
    get() = this.headPin.onDeck && (this.left2Pin.isDown || this.left3Pin.isDown)

val Deck.isHitRightOfMiddle: Boolean
    get() = this.headPin.onDeck && (this.right2Pin.isDown || this.right3Pin.isDown)

val Deck.isMiddleHit: Boolean
    get() = this.headPin.isDown

val Deck.isLeftTwelve: Boolean
    get() = this.value(false) == 12 && this.left3Pin.isDown

val Deck.isRightTwelve: Boolean
    get() = this.value(false) == 12 && this.right3Pin.isDown

val Deck.isTwelve: Boolean
    get() = this.isLeftTwelve || this.isRightTwelve

val Deck.arePinsCleared: Boolean
    get() = this.all { it.isDown }

// Functions

fun Deck.toBooleanArray(): BooleanArray {
    return this.map { it.isDown }.toBooleanArray()
}

fun Deck.deepCopy(): Deck {
    return this.map { pin -> Pin(pin.type).apply { isDown = pin.isDown } }.toTypedArray()
}

fun Deck.toInt(): Int {
    var ball = 0
    for (i in this.indices) {
        if (this[i].isDown) {
            ball += Math.pow(2.0, (-i + 4).toDouble()).toInt()
        }
    }
    return ball
}

fun Deck.reset() {
    this.forEach { it.isDown = false }
}

fun Deck.value(onDeck: Boolean): Int {
    return this.filter { it.onDeck == onDeck }.sumBy { it.value }
}

fun Deck.ballValue(ballIdx: Int, returnSymbol: Boolean, afterStrike: Boolean): String {
    val value = this.value(false)
    return when (value) {
        0 -> Ball.None.toString()
        2, 3, 4, 6, 9, 12 -> value.toString()
        5 -> return if ((ballIdx == 0 || returnSymbol) && this[2].isDown) Ball.HeadPin.toString() else "5"
        7 -> return if ((ballIdx == 0 || returnSymbol) && this[2].isDown) Ball.HeadPin2.toString() else "7"
        8 -> return if ((ballIdx == 0 || returnSymbol) && this[2].isDown) Ball.Split.toString() else "8"
        10 -> return if ((ballIdx == 0 || returnSymbol) && this[2].isDown && ((this[0].isDown && this[1].isDown) || (this[3].isDown && this[4].isDown))) Ball.ChopOff.toString() else "10"
        11 -> return if ((ballIdx == 0 || returnSymbol) && this[2].isDown) Ball.Ace.toString() else "11"
        13 -> return when {
            (ballIdx == 0 || returnSymbol) && !this[0].isDown -> Ball.Left.toString()
            (ballIdx == 0 || returnSymbol) && !this[4].isDown -> Ball.Right.toString()
            else -> "13"
        }
        15 -> return when {
            ballIdx == 0 || returnSymbol -> Ball.Strike.toString()
            ballIdx == 1 && !afterStrike -> Ball.Spare.toString()
            else -> "15"
        }
        else -> throw IllegalStateException("Invalid value for ball: $value")
    }
}

fun Deck.ballValueDifference(other: Deck, ballIdx: Int, returnSymbol: Boolean, afterStrike: Boolean): String {
    val deck = this.deepCopy()
    for (i in 0 until other.size) {
        if (other[i].isDown) { deck[i].isDown = false }
    }

    return deck.ballValue(ballIdx, returnSymbol, afterStrike)
}

fun Deck.valueDifference(other: Deck): Int {
    return this.filterIndexed { index, pin -> pin.isDown && !other[index].isDown }.sumBy { it.value }
}
