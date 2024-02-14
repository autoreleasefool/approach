package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface CheckpointDao {
	@RawQuery
	fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Long

	fun recordCheckpoint() {
		checkpoint(SimpleSQLiteQuery("PRAGMA wal_checkpoint(full)"))
	}
}
