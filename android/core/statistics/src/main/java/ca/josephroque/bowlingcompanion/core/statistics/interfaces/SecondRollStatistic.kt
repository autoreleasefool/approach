package ca.josephroque.bowlingcompanion.core.statistics.interfaces

import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerSecondRoll

interface SecondRollStatistic : PercentageStatistic, TrackablePerSecondRoll {
	override val numeratorTitleResourceId: Int
		get() = id.titleResourceId

	override val includeNumeratorInFormattedValue: Boolean
		get() = true

	override val isEmpty: Boolean
		get() = numerator == 0
}
