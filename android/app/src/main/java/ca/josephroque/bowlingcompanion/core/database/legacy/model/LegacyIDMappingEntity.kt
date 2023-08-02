package ca.josephroque.bowlingcompanion.core.database.legacy.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
	tableName = "legacy_ids",
)
@Immutable
data class LegacyIDMappingEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "legacy_id", index = true) val legacyId: Long,
	@ColumnInfo(name = "mapping_key")  val key: LegacyIDMappingKey,
)

enum class LegacyIDMappingKey {
	TEAM,
	BOWLER,
	LEAGUE,
}