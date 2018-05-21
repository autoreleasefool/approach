package ca.josephroque.bowlingcompanion.common.interfaces

import android.content.Context
import kotlinx.coroutines.experimental.Deferred

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Indicate an item is deletable.
 */
interface IDeletable {

    /** Indicates if this item has been deleted or not. */
    val isDeleted: Boolean

    /**
     * Mark this item to be deleted and return the instance to use
     *
     * @return a copy of this item, marked for deletion
     */
    fun markForDeletion(): IDeletable

    /**
     * Clean any marks for deletion and return the instance to use
     *
     * @return a copy of this item, no longer marked for deletion
     */
    fun cleanDeletion(): IDeletable

    /**
     * Delete the instance from the database.
     *
     * @param context to get database instance
     */
    fun delete(context: Context): Deferred<Unit>
}
