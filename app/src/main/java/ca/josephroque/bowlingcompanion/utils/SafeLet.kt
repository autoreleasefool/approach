package ca.josephroque.bowlingcompanion.utils

/**
 * Multiple variable `let`
 *
 * https://stackoverflow.com/a/35522422/4896787
 */
fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}
