package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetCreateBowler
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetCreateLeague
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetSource
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetTimeline
import java.util.UUID

@Entity(
	tableName = "statistics_widget",
	foreignKeys = [
		ForeignKey(
			entity = BowlerEntity::class,
			parentColumns = ["id"],
			childColumns = ["bowler_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = LeagueEntity::class,
			parentColumns = ["id"],
			childColumns = ["league_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.CASCADE,
		),
	]
)
data class StatisticsWidgetEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "bowler_id") val bowlerId: UUID?,
	@ColumnInfo(name = "league_id") val leagueId: UUID?,
	@ColumnInfo(name = "timeline") val timeline: StatisticsWidgetTimeline,
	@ColumnInfo(name = "statistic") val statistic: Int,
	@ColumnInfo(name = "context") val context: String,
	@ColumnInfo(name = "priority") val priority: Int,
)

fun StatisticsWidgetEntity.asModel(): StatisticsWidget? = (leagueId ?: bowlerId)?.let {
	StatisticsWidget(
		source = leagueId?.let {
			StatisticsWidgetSource.League(leagueId = it)
		} ?: bowlerId?.let {
			StatisticsWidgetSource.Bowler(bowlerId = it)
		}!!,
		id = id,
		timeline = timeline,
		statistic = statistic,
		context = context,
		priority = priority,
	)
}

fun StatisticsWidget.asEntity(): StatisticsWidgetEntity = StatisticsWidgetEntity(
	id = id,
	bowlerId = (source as? StatisticsWidgetSource.Bowler)?.bowlerId,
	leagueId = (source as? StatisticsWidgetSource.League)?.leagueId,
	timeline = timeline,
	statistic = statistic,
	context = context,
	priority = priority,
)

data class StatisticsWidgetPriorityUpdateEntity(
	val id: UUID,
	val priority: Int,
)

data class StatisticsWidgetCreateEntity(
	@ColumnInfo(name = "bowler_id") val bowlerId: UUID?,
	@ColumnInfo(name = "league_id") val leagueId: UUID?,
	val id: UUID,
	val timeline: StatisticsWidgetTimeline,
	val statistic: Int,
	val context: String,
	val priority: Int,
)

fun StatisticsWidgetCreateBowler.asEntity(): StatisticsWidgetCreateEntity = StatisticsWidgetCreateEntity(
	bowlerId = bowlerId,
	leagueId = null,
	id = id,
	timeline = timeline,
	statistic = statistic,
	context = context,
	priority = priority,
)

fun StatisticsWidgetCreateLeague.asEntity(): StatisticsWidgetCreateEntity = StatisticsWidgetCreateEntity(
	bowlerId = null,
	leagueId = leagueId,
	id = id,
	timeline = timeline,
	statistic = statistic,
	context = context,
	priority = priority,
)