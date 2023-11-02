package ca.josephroque.bowlingcompanion.core.scoring

import ca.josephroque.bowlingcompanion.core.model.ScoringFrame

interface ScoreKeeper {
	suspend fun calculateScore(input: ScoreKeeperInput): List<ScoringFrame>
}