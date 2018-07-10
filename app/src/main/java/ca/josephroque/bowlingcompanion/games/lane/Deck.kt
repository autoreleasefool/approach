package ca.josephroque.bowlingcompanion.games.lane

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
fun Deck.difference(other: Deck): Int {
    return this.filterIndexed { index, pin -> pin.isDown && !other[index].isDown }.sumBy { it.value }
}