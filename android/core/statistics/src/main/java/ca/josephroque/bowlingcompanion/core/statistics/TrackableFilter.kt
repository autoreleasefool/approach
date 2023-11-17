package ca.josephroque.bowlingcompanion.core.statistics

import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import kotlinx.datetime.LocalDate
import java.util.UUID

data class TrackableFilter(
	val source: Source,
	val leagues: LeagueFilter = LeagueFilter(),
	val series: SeriesFilter = SeriesFilter(),
	val games: GameFilter = GameFilter(),
	val frames: FrameFilter = FrameFilter(),
	val aggregation: AggregationFilter = AggregationFilter.ACCUMULATE,
) {
	sealed interface Source {
		data class Bowler(val id: UUID): Source
		data class League(val id: UUID): Source
		data class Series(val id: UUID): Source
		data class Game(val id: UUID): Source
	}

	data class LeagueFilter(
		val recurrence: LeagueRecurrence? = null,
	)

	data class SeriesFilter(
		val startDate: LocalDate? = null,
		val endDate: LocalDate? = null,
		val alleys: AlleyFilter? = null,
	)

	sealed interface AlleyFilter {
		data class Alley(val id: UUID): AlleyFilter
		data class Properties(
			val material: AlleyMaterial? = null,
			val mechanism: AlleyMechanism? = null,
			val pinFall: AlleyPinFall? = null,
			val pinBase: AlleyPinBase? = null,
		)
	}

	data class GameFilter(
		val lanes: LaneFilter? = null,
		val gearUsed: Set<UUID> = emptySet(),
		val opponent: UUID? = null,
	)

	sealed interface LaneFilter {
		data class Lanes(val lanes: Set<UUID>): LaneFilter
		data class Positions(val positions: Set<LanePosition>): LaneFilter
	}

	data class FrameFilter(
		val bowlingBallsUsed: Set<UUID> = emptySet(),
	)

	enum class AggregationFilter {
		ACCUMULATE,
		PERIODIC,
	}
}