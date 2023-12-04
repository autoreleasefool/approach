package ca.josephroque.bowlingcompanion.core.statistics.models

import kotlinx.datetime.LocalDate

sealed interface ChartEntryKey {
	data class Date(val date: LocalDate, val days: Int): ChartEntryKey {
		override val value: Float
			get() = date.toEpochDays().toFloat()
	}

	data class Game(val index: Int): ChartEntryKey {
		override val value: Float
			get() = index.toFloat()
	}

	val value: Float
}