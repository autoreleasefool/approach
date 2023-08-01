package ca.josephroque.bowlingcompanion.core.database.legacy.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
	tableName = "legacy_ids"
)
data class LegacyIDMappingEntity(
	@PrimaryKey val id: UUID,
	@ColumnInfo(index = true) val legacyId: Long,
	val key: LegacyIDMappingKey,
)

enum class LegacyIDMappingKey {
	TEAM,
}