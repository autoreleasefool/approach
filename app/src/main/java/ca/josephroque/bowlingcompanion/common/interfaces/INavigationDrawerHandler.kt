package ca.josephroque.bowlingcompanion.common.interfaces

import android.support.annotation.IdRes
import ca.josephroque.bowlingcompanion.common.NavigationDrawerController

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Indicate an object can handle a Navigation Drawer event.
 */
interface INavigationDrawerHandler {
    var navigationDrawerController: NavigationDrawerController

    fun onNavDrawerItemSelected(@IdRes itemId: Int)
}
