package ca.josephroque.bowlingcompanion.common.interfaces

import android.content.Context
import kotlinx.coroutines.experimental.Deferred

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Indicate an item is deletable.
 */
interface IDeletable {
    val isDeleted: Boolean

    fun markForDeletion(): IDeletable
    fun cleanDeletion(): IDeletable
    fun delete(context: Context): Deferred<Unit>
}
