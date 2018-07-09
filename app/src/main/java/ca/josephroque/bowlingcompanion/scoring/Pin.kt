package ca.josephroque.bowlingcompanion.scoring

import ca.josephroque.bowlingcompanion.games.Game
import java.util.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Methods to handle pin scoring and calculations.
 */
object Pin {

    /**
     * Converts an int from the database to a boolean array.
     *
     * @param ball int from 0-31
     * @return boolean array binary representation of `ball`
     */
    fun ballIntToBoolean(ball: Int): BooleanArray {
        if (ball < 0 || ball > 31) {
            throw IllegalArgumentException("cannot convert value: $ball")
        }
        val pinState = BooleanArray(Game.NUMBER_OF_PINS)
        val ballBinary = String.format(Locale.CANADA, "%5s", Integer.toBinaryString(ball)).replace(' ', '0')
        for (i in pinState.indices) {
            pinState[i] = ballBinary[i] == '1'
        }
        return pinState
    }

    /**
     * Creates an int from an array of booleans.
     *
     * @param frame array to convert to int
     * @return a integer representing the booleans if they were binary
     */
    fun booleanFrameToInt(frame: BooleanArray): Int {
        var ball = 0
        for (i in frame.indices) {
            if (frame[i]) {
                ball += Math.pow(2.0, (-i + 4).toDouble()).toInt()
            }
        }
        return ball
    }
}