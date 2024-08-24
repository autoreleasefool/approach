package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.model.Team
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamCreate
import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem
import ca.josephroque.bowlingcompanion.core.model.TeamUpdate

@Entity(
	tableName = "teams",
)
data class TeamEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: TeamID,
	@ColumnInfo(name = "name") val name: String,
)

fun Team.asEntity() = TeamEntity(
	id = id,
	name = name,
)

data class TeamCreateEntity(val id: TeamID, val name: String)

fun TeamCreate.asEntity() = TeamCreateEntity(
	id = id,
	name = name,
)

data class TeamUpdateEntity(
	@Embedded
	val properties: TeamUpdate.Properties,
	@Relation(
		parentColumn = "id",
		entityColumn = "id",
		entity = BowlerEntity::class,
		associateBy = Junction(
			value = TeamBowlerCrossRef::class,
			parentColumn = "team_id",
			entityColumn = "bowler_id",
		),
	)
	val members: List<TeamMemberListItem>,
) {
	fun asModel() = TeamUpdate(
		id = properties.id,
		name = properties.name,
		members = members,
	)
}

data class TeamDetailsUpdateEntity(
	val id: TeamID,
	val name: String,
)

fun TeamUpdate.asEntity() = TeamDetailsUpdateEntity(
	id = id,
	name = name,
)