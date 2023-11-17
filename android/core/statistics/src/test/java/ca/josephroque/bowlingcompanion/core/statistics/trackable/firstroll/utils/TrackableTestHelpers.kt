package ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.utils

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.testing.id
import kotlinx.datetime.LocalDate

fun roll(index: Int, pinsDowned: Set<Pin>, didFoul: Boolean = false): TrackableFrame.Roll =
	TrackableFrame.Roll(index, pinsDowned, didFoul)

fun frame(index: Int, rolls: List<TrackableFrame.Roll>): TrackableFrame =
	TrackableFrame(
		seriesId = id(0),
		date = LocalDate(2023, 1, 1),
		gameId = id(0),
		gameIndex = 0,
		index = index,
		rolls = rolls,
	)