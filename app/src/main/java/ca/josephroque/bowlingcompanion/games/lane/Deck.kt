package ca.josephroque.bowlingcompanion.games.lane

import ca.josephroque.bowlingcompanion.games.Game

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Alias for an array of pins.
 */

/** Alias for an array of pins. */
typealias Deck = Array<Pin>

/**
 * Map a deck to a BooleanArray.
 *
 * @param pins the deck to map
 * @return a BooleanArray indicating which pins are down
 */
fun Deck.toBooleanArray(): BooleanArray {
    return this.map { it.isDown }.toBooleanArray()
}

/**
 * Creates an int from this deck.
 *
 * @return a integer representing the pins
 */
fun Deck.toInt(): Int {
    var ball = 0
    for (i in this.indices) {
        if (this[i].isDown) {
            ball += Math.pow(2.0, (-i + 4).toDouble()).toInt()
        }
    }
    return ball
}

/**
 * Gets the value of pins on deck, or knocked down.
 *
 * @param onDeck true to get the value of the pins still standing, false to get those knocked down
 * @return the value of the pins
 */
fun Deck.value(onDeck: Boolean): Int {
    return this.filter { it.onDeck == onDeck }.sumBy { it.value }
}

/**
 * Gets the ball value of the pins knocked down.
 *
 * @param ballIdx the ball to get the value of
 * @param returnSymbol indicates if a symbol should be returned instead of the numeric value
 * @param afterStrike indicates if the ball being counted was after a strike
 * @return the ball value
 */
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

/**
 * Gets the ball value of the pins which were knocked down in the deck, but not in `other`.
 *
 * @param other the other deck (i.e. a previous ball)
 * @param ballIdx the ball to get the value of
 * @param returnSymbol indicates if a symbol should be returned instead of the numeric value
 * @param afterStrike indicates if the ball being counted was after a strike
 */
fun Deck.ballValueDifference(other: Deck, ballIdx: Int, returnSymbol: Boolean, afterStrike: Boolean): String {
    val deck = this.copyOf()
    for (i in 0 until other.size) {
        if (other[i].isDown) { deck[i].isDown = false }
    }

    return deck.ballValue(ballIdx, returnSymbol, afterStrike)
}

/**
 * Checks if all pins have been knocked down.
 *
 * @return true if all pins are down, false if at least one is not down.
 */
fun Deck.arePinsCleared(): Boolean {
    return this.all { it.isDown }
}

/**
 * Get the value of only the pins on deck which were also on deck in `other`.
 *
 * @param other the other deck (i.e. a previous ball)
 * @return the value of the difference in pins knocked down
 */
fun Deck.valueDifference(other: Deck): Int {
    return this.filterIndexed { index, pin -> pin.isDown && !other[index].isDown }.sumBy { it.value }
}