package ca.josephroque.bowlingcompanion.games.lane

import java.util.Locale

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Pins with their values.
 */
class Pin(val type: Type) {

    enum class Type {
        Two, Three, Five;

        val value: Int
            get() {
                return when (this) {
                    Two -> 2
                    Three -> 3
                    Five -> 5
                }
            }
    }

    var isDown: Boolean = false

    val onDeck: Boolean
        get() = !isDown

    val value: Int
        get() = type.value

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
        private fun buildDeck(): Deck {
            return arrayOf(Pin(Type.Two), Pin(Type.Three), Pin(Type.Five), Pin(Type.Three), Pin(Type.Two))
        }

        fun deckFromBooleanArray(array: BooleanArray): Deck {
            return buildDeck().apply {
                forEachIndexed { index, pin ->
                    pin.isDown = array[index]
                }
            }
        }

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
