package ca.josephroque.bowlingcompanion.common.interfaces

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Indicate an object can handle a Floating Action Button event.
 */
interface IFloatingActionButtonHandler {

    /**
     * Invoked when the fab is clicked and the object should handle the event.
     */
    fun onFabClick()

    /**
     * Get a drawable for the floating action button to display.
     *
     * @return id of the drawable
     */
    fun getFabImage(): Int?
}
