package ca.josephroque.bowlingcompanion.common.interfaces

import android.support.annotation.IdRes
import ca.josephroque.bowlingcompanion.common.NavigationDrawerController

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Indicate an object can handle a Navigation Drawer event.
 */
interface INavigationDrawerHandler {

    /** Controller for the navigation drawer. */
    var navigationDrawerController: NavigationDrawerController

    /**
     * Invoked when an item in the Navigation Drawer is selected.
     *
     * @param itemId the id of the item
     */
    fun onNavDrawerItemSelected(@IdRes itemId: Int)
}
