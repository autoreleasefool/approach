package ca.josephroque.bowlingcompanion.common.interfaces

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Indicate an object can handle a Floating Action Button event.
 */
interface IFloatingActionButtonHandler {
    fun onFabClick()
    fun getFabImage(): Int?
}
