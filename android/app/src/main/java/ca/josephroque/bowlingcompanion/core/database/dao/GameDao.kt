package ca.josephroque.bowlingcompanion.core.database.dao

import androidx.room.Dao
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity

@Dao
abstract class GameDao: BaseDao<GameEntity>