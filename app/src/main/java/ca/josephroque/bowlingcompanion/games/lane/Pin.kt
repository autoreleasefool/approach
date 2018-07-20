package ca.josephroque.bowlingcompanion.games.lane

import java.util.Locale

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Pins with their values.
 */
class Pin(val type: Type) {

    /** Type of the  pin. */
    enum class Type {
        Two, Three, Five;

        /** Value of the pin. */
        val value: Int
            get() {
                return when (this) {
                    Two -> 2
                    Three -> 3
                    Five -> 5
                }
            }
    }

    /** Indicates if the pin is down or not. */
    var isDown: Boolean = false

    /** Indicates if the pin is up or not. */
    val onDeck: Boolean
        get() = !isDown

    /** Value of the pin. */
    val value: Int
        get() = type.value

    /**
     * Create a deep copy of this pin.
     *
     * @return a new instance of [Pin]
     */
    fun deepCopy(): Pin {
        val pin = when (type) {
            Type.Two -> Pin(Type.Two)
            Type.Three -> Pin(Type.Three)
            Type.Five -> Pin(Type.Five)
        }
        pin.isDown = this.isDown
        return pin
    }

    companion object {
        /**
         * Gets a new instance of a clean deck.
         *
         * @return a set of five pins, all standing
         */
        private fun buildDeck(): Deck {
            return arrayOf(Pin(Type.Two), Pin(Type.Three), Pin(Type.Five), Pin(Type.Three), Pin(Type.Two))
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
            val pinState = buildDeck()
            val ballBinary = String.format(Locale.CANADA, "%5s", Integer.toBinaryString(ball)).replace(' ', '0')
            for (i in pinState.indices) {
                pinState[i].isDown = ballBinary[i] == '1'
            }
            return pinState
        }
    }
}
