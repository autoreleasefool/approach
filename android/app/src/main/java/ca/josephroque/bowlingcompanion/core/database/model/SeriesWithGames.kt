package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class SeriesWithGames(
	@Embedded
	val series: SeriesEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "seriesId",
	)
	val games: List<GameEntity>
)