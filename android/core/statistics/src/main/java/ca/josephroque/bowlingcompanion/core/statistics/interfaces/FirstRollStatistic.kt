package ca.josephroque.bowlingcompanion.core.statistics.interfaces

import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import ca.josephroque.bowlingcompanion.core.statistics.R
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFirstRoll
import ca.josephroque.bowlingcompanion.core.statistics.TrackablePerFrameConfiguration

interface FirstRollStatistic : PercentageStatistic, TrackablePerFirstRoll {
	var totalRolls: Int

	fun tracksRoll(firstRoll: TrackableFrame.Roll, configuration: TrackablePerFrameConfiguration): Boolean

	override val numeratorTitleResourceId: Int
		get() = id.titleResourceId

	override val denominatorTitleResourceId: Int
		get() = R.string.statistic_title_total_rolls

	override val includeNumeratorInFormattedValue: Boolean
		get() = true

	override var denominator: Int
		get() = totalRolls
		set(value) {
			totalRolls = value
		}

	override fun adjustByFirstRoll(firstRoll: TrackableFrame.Roll, configuration: TrackablePerFrameConfiguration) {
		totalRolls++
		if (tracksRoll(firstRoll, configuration)) {
			numerator++
		}
	}
}
