package ca.josephroque.bowlingcompanion.core.data.models.sample

import ca.josephroque.bowlingcompanion.core.data.models.Bowler
import java.util.UUID

object SampleData {
	val sampleBowlers = listOf(
		Bowler(id = uuidFromInt(0), name = "Joseph"),
		Bowler(id = uuidFromInt(1), name = "Sarah"),
		Bowler(id = uuidFromInt(2), name = "Audriana")
	)
}

fun uuidFromInt(value: Int): UUID {
	val stringValue = String.format("%012x", value)
	return UUID.fromString("00000000-0000-0000-0000-$stringValue")
}