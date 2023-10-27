package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDao<T> {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(entity: T): Long

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(vararg entity: T)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(entities: Collection<T>)

	@Delete
	suspend fun delete(entity: T): Int
}