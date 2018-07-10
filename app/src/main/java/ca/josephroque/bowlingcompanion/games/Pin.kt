package ca.josephroque.bowlingcompanion.games

import java.util.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Pins with their values.
 */
enum class Pin {
    Two, Three, Five;

    /** Indicates if the pin is down or not. */
    var isDown: Boolean = false

    /** Value of the pin. */
    val value: Int
        get() {
            return when (this) {
                Two -> 2
                Three -> 3
                Five -> 5
            }
        }

    companion object {

        /**
         * Gets a new instance of a clean deck.
         *
         * @return a set of five pins, all standing
         */
        fun buildDeck(): Deck {
            return arrayOf(Two, Three, Five, Three, Two)
        }

        /**
         * Build a Deck from a BooleanArray
         *
         * @param array the source array
         * @return a new deck
         */
        fun deckFromBooleanArray(array: BooleanArray): Deck {
            return buildDeck().apply {
                forEachIndexed { index, pin ->
                    pin.isDown = array[index]
                }
            }
        }

        /**
         * Converts an int from the database to a deck.
         *
         * @param ball int from 0-31
         * @return deck representation of `ball`
         */
        fun deckFromInt(ball: Int): Deck {
            if (ball < 0 || ball > 31) {
                throw IllegalArgumentException("cannot convert value: $ball")
            }
            val pinState = Pin.buildDeck()
            val ballBinary = String.format(Locale.CANADA, "%5s", Integer.toBinaryString(ball)).replace(' ', '0')
            for (i in pinState.indices) {
                pinState[i].isDown = ballBinary[i] == '1'
            }
            return pinState
        }
    }
}

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