package ca.josephroque.bowlingcompanion.common.interfaces

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Enforces objects to have an ID.
 */
interface IIdentifiable {
    val id: Long

    fun indexInList(list: List<IIdentifiable>): Int = (0 until list.size).firstOrNull { list[it]::class == this::class && list[it].id == id } ?: -1
}
