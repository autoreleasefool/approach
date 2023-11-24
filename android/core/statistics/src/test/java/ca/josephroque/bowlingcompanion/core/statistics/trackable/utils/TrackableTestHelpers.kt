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