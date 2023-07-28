package ca.josephroque.bowlingcompanion.core.database.relationship

import androidx.room.Embedded
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity

data class SeriesWithGames(
	@Embedded
	val series: SeriesEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "seriesId",
	)
	val games: List<GameEntity>
)