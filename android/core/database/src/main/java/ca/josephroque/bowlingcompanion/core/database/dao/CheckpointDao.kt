package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface CheckpointDao {
	@RawQuery fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Long
}