package ca.josephroque.bowlingcompanion.core.database.legacy

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Provides utility methods for scoring fouls.
 */
object LegacyFouls {

	/**
	 * Gets the String representation of a number of fouls.
	 * Historic implementation from pre-3.0.0, required for database format
	 *
	 * @param i integer representation of the number of fouls
	 * @return string representation of the number of fouls
	 */
	fun foulIntToString(i: Int): String {
		return when (i) {
			24 -> "3"
			25 -> "2"
			26 -> "23"
			27 -> "1"
			28 -> "13"
			29 -> "12"
			30 -> "123"
			else -> "0"
		}
	}

	/**
	 * Gets the int representation of a String of fouls in a frame.
	 * Historic implementation from pre-3.0.0, required for database format
	 *
	 * @param s string representation of the number of fouls
	 * @return integer representation of the number of fouls
	 */
	fun foulStringToInt(s: String): Int {
		return when (s) {
			"3" -> 24
			"2" -> 25
			"23" -> 26
			"1" -> 27
			"13" -> 28
			"12" -> 29
			"123" -> 30
			else -> 0
		}
	}
}
