package ca.josephroque.bowlingcompanion.common

import android.content.Context
import kotlinx.coroutines.experimental.Deferred

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Indicate an item is deletable.
 */
interface IDeletable {

    /** Indicates if this item has been deleted or not. */
    var isDeleted: Boolean

    /**
     * Delete the instance from the database.
     *
     * @param context to get database instance
     */
    fun delete(context: Context): Deferred<Unit>
}