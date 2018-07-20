package ca.josephroque.bowlingcompanion.games.lane

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Possible balls which can result from a frame.
 */
enum class Ball {
    Strike, Spare, Left, Right, Ace, ChopOff, Split, HeadPin, HeadPin2, None;

    /** @Override */
    override fun toString(): String {
        return when (this) {
            Strike -> "X"
            Spare -> "/"
            Left -> "L"
            Right -> "R"
            Ace -> "A"
            ChopOff -> "C/O"
            Split -> "HS"
            HeadPin -> "HP"
            HeadPin2 -> "H2"
            None -> "-"
        }
    }
}
