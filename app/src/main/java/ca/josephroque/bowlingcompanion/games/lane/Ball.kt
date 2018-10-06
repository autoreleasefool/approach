package ca.josephroque.bowlingcompanion.games.lane

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Possible balls which can result from a frame.
 */
enum class Ball {
    Strike, Spare, Left, Right, Ace, ChopOff, Split, Split2, HeadPin, HeadPin2, Cleared, None;

    override fun toString(): String {
        return when (this) {
            Strike -> "X"
            Spare -> "/"
            Left -> "L"
            Right -> "R"
            Ace -> "A"
            ChopOff -> "C/O"
            Split -> "HS"
            Split2 -> "10"
            HeadPin -> "HP"
            HeadPin2 -> "H2"
            Cleared -> "15"
            None -> "-"
        }
    }

    val numeral: String
        get() {
            return when (this) {
                Strike, Spare, Cleared -> "15"
                Left, Right -> "13"
                Ace -> "11"
                ChopOff, Split2 -> "10"
                Split -> "8"
                HeadPin2 -> "7"
                HeadPin -> "5"
                None -> "-"
            }
        }
}
