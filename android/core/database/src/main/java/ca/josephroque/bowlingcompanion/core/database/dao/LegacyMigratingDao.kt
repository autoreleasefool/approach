package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface LegacyMigratingDao<T> {
//	@Insert(onConflict = OnConflictStrategy.REPLACE)
//	suspend fun insert(entity: T): Long

//	@Insert(onConflict = OnConflictStrategy.REPLACE)
//	suspend fun insertAll(vararg entity: T)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun migrateAll(entities: Collection<T>)

//	@Delete
//	suspend fun delete(entity: T): Int
}