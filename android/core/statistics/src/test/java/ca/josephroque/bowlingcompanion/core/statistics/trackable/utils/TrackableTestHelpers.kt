package ca.josephroque.bowlingcompanion.core.statistics.trackable.utils

import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
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

fun matchPlay(result: MatchPlayResult? = null): TrackableGame.MatchPlay =
	TrackableGame.MatchPlay(
		id = id(0),
		result = result,
	)

fun game(index: Int, score: Int = 0): TrackableGame =
	TrackableGame(
		seriesId = id(0),
		id = id(index),
		index = index,
		date = LocalDate(2023, 1, 1),
		score = score,
		matchPlay = matchPlay(null),
	)

fun series(numberOfGames: Int = 0, total: Int = 0): TrackableSeries =
	TrackableSeries(
		id = id(0),
		date = LocalDate(2023, 1, 1),
		numberOfGames = numberOfGames,
		total = total,
	)

fun mockSeries(): List<TrackableSeries> = (0..100).map {
	TrackableSeries(
		id = id(it),
		numberOfGames = it,
		total = 123,
		date = LocalDate(2023, 1, 1),
	)
}

fun mockGames(): List<TrackableGame> = (0..20).map {
	TrackableGame(
		seriesId = id(0),
		id = id(it),
		index = 0,
		score = 123,
		date = LocalDate(2023, 1, 1),
		matchPlay = matchPlay(MatchPlayResult.LOST),
	)
}

fun mockFrames(): List<TrackableFrame> = (0..<10).map {
	TrackableFrame(
		seriesId = id(0),
		gameId = id(0),
		gameIndex = 0,
		index = it,
		date = LocalDate(2023, 1, 1),
		rolls = listOf(
			roll(0, setOf()),
			roll(1, setOf(Pin.LEFT_TWO_PIN, Pin.LEFT_THREE_PIN, Pin.HEAD_PIN, Pin.RIGHT_THREE_PIN, Pin.RIGHT_TWO_PIN), didFoul = true),
		)
	)
}