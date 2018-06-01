package ca.josephroque.bowlingcompanion.common.interfaces

import android.support.annotation.IdRes

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Indicate an object can handle a Navigation Drawer event.
 */
interface INavigationDrawerHandler {

    /**
     * Invoked when an item in the Navigation Drawer is selected.
     *
     * @param itemId the id of the item
     */
    fun onNavDrawerItemSelected(@IdRes itemId: Int)
}
