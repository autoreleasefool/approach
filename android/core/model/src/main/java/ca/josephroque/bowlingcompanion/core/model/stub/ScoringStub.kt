package ca.josephroque.bowlingcompanion.core.model.stub

import ca.josephroque.bowlingcompanion.core.model.ScoringFrame
import ca.josephroque.bowlingcompanion.core.model.ScoringGame
import ca.josephroque.bowlingcompanion.core.model.ScoringRoll
import java.util.UUID

object ScoringStub {
	fun stub(): ScoringGame = ScoringGame(
		id = UUID.randomUUID(),
		index = 0,
		frames = listOf(
			ScoringFrame(
				index = 0,
				rolls = listOf(
					ScoringRoll(index = 0, didFoul = true, display = "A", isSecondaryValue = false),
					ScoringRoll(index = 1, didFoul = false, display = "2", isSecondaryValue = false),
					ScoringRoll(index = 2, didFoul = false, display = "2", isSecondaryValue = false),
				),
				score = 0,
			),
			ScoringFrame(
				index = 1,
				rolls = listOf(
					ScoringRoll(index = 0, didFoul = true, display = "HS", isSecondaryValue = false),
					ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
					ScoringRoll(index = 2, didFoul = false, display = "2", isSecondaryValue = false),
				),
				score = 0,
			),
			ScoringFrame(
				index = 2,
				rolls = listOf(
					ScoringRoll(index = 0, didFoul = true, display = "12", isSecondaryValue = false),
					ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
					ScoringRoll(index = 2, didFoul = false, display = "12", isSecondaryValue = true),
				),
				score = 12,
			),
			ScoringFrame(
				index = 3,
				rolls = listOf(
					ScoringRoll(index = 0, didFoul = true, display = "12", isSecondaryValue = false),
					ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
					ScoringRoll(index = 2, didFoul = false, display = "10", isSecondaryValue = true),
				),
				score = 22,
			),
			ScoringFrame(
				index = 4,
				rolls = listOf(
					ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
					ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
					ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = false),
				),
				score = 37,
			),
			ScoringFrame(
				index = 5,
				rolls = listOf(
					ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
					ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
					ScoringRoll(index = 2, didFoul = false, display = "10", isSecondaryValue = true),
				),
				score = 62,
			),
			ScoringFrame(
				index = 6,
				rolls = listOf(
					ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
					ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
					ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = false),
				),
				score = 77,
			),
			ScoringFrame(
				index = 7,
				rolls = listOf(
					ScoringRoll(index = 0, didFoul = false, display = "HS", isSecondaryValue = false),
					ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
					ScoringRoll(index = 2, didFoul = false, display = "11", isSecondaryValue = true),
				),
				score = 103,
			),
			ScoringFrame(
				index = 8,
				rolls = listOf(
					ScoringRoll(index = 0, didFoul = false, display = "A", isSecondaryValue = false),
					ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
					ScoringRoll(index = 2, didFoul = false, display = "15", isSecondaryValue = true),
				),
				score = 133,
			),
			ScoringFrame(
				index = 9,
				rolls = listOf(
					ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
					ScoringRoll(index = 1, didFoul = false, display = "HS", isSecondaryValue = false),
					ScoringRoll(index = 2, didFoul = false, display = "/", isSecondaryValue = false),
				),
				score = 163,
			),
		),
	)
}
