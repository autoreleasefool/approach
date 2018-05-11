package ca.josephroque.bowlingcompanion.common.interfaces

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Enforces objects to have an ID.
 */
interface IIdentifiable {

    /** Unique ID of the object. */
    var id: Long

    /**
     * Check if this [IIdentifiable] exists in a list
     *
     * @param list the list of identifiable items to check
     * @return index of this item in the list if the [id] and type of this item matches the [id] of
     *         an item in the list
     */
    fun indexInList(list: List<IIdentifiable>): Int = (0 until list.size).firstOrNull { list[it]::class == this::class && list[it].id == id } ?: -1
}
