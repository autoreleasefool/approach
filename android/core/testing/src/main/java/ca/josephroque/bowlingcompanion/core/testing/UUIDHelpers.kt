package ca.josephroque.bowlingcompanion.core.testing

import java.util.UUID

fun id(value: Int): UUID =
		UUID.fromString("00000000-0000-0000-0000-${"%012x".format(value)}")