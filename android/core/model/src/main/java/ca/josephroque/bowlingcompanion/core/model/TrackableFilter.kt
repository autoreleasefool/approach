package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID
import kotlinx.datetime.LocalDate

data class TrackableFilter(
	val source: Source,
	val leagues: LeagueFilter = LeagueFilter(),
	val series: SeriesFilter = SeriesFilter(),
	val games: GameFilter = GameFilter(),
	val frames: FrameFilter = FrameFilter(),
	val aggregation: AggregationFilter = AggregationFilter.ACCUMULATE,
) {
	sealed interface Source {
		val id: UUID

		data class Bowler(val bowlerId: BowlerID) : Source {
			override val id: UUID
				get() = bowlerId.value
		}
		data class League(override val id: UUID) : Source
		data class Series(override val id: UUID) : Source
		data class Game(override val id: UUID) : Source
	}

	data class LeagueFilter(val recurrence: LeagueRecurrence? = null)

	data class SeriesFilter(
		val startDate: LocalDate? = null,
		val endDate: LocalDate? = null,
		val alleys: AlleyFilter? = null,
	)

	sealed interface AlleyFilter {
		data class Alley(val id: UUID) : AlleyFilter
		data class Properties(
			val material: AlleyMaterial? = null,
			val mechanism: AlleyMechanism? = null,
			val pinFall: AlleyPinFall? = null,
			val pinBase: AlleyPinBase? = null,
		) : AlleyFilter
	}

	data class GameFilter(
		val lanes: LaneFilter? = null,
		val gearUsed: Set<UUID> = emptySet(),
		val opponent: UUID? = null,
	)

	sealed interface LaneFilter {
		data class Lanes(val lanes: Set<UUID>) : LaneFilter
		data class Positions(val positions: Set<LanePosition>) : LaneFilter
	}

	data class FrameFilter(val bowlingBallsUsed: Set<UUID> = emptySet())

	enum class AggregationFilter {
		ACCUMULATE,
		PERIODIC,
		;

		val next: AggregationFilter
			get() = when (this) {
				ACCUMULATE -> PERIODIC
				PERIODIC -> ACCUMULATE
			}
	}

	data class SourceSummaries(
		val bowler: BowlerSummary,
		val league: LeagueSummary? = null,
		val series: SeriesSummary? = null,
		val game: GameSummary? = null,
	)
}
