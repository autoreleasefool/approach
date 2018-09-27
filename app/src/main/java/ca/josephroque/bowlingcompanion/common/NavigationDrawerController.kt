package ca.josephroque.bowlingcompanion.common

import android.support.design.widget.NavigationView
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.leagues.League
import java.lang.ref.WeakReference

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Provides limited access to the navigation drawer so fragments can adjust display values,
 * but not alter the contents otherwise.
 */
class NavigationDrawerController(private val navigationView: WeakReference<NavigationView>) {

    companion object {
        @Suppress("unused")
        private const val TAG = "GameControllerFragment"

        val navGameItemIds = intArrayOf(R.id.nav_game_1, R.id.nav_game_2, R.id.nav_game_3,
                R.id.nav_game_4, R.id.nav_game_5, R.id.nav_game_6, R.id.nav_game_7, R.id.nav_game_8,
                R.id.nav_game_9, R.id.nav_game_10, R.id.nav_game_11, R.id.nav_game_12,
                R.id.nav_game_13, R.id.nav_game_14, R.id.nav_game_15, R.id.nav_game_16,
                R.id.nav_game_17, R.id.nav_game_18, R.id.nav_game_19, R.id.nav_game_20)
    }

    var isTeamMember: Boolean = false
        set(value) {
            field = value
            navigationView.get()?.post {
                navigationView.get()?.menu?.findItem(R.id.nav_series)?.isVisible = !value && !isEvent
            }
        }

    var isEvent: Boolean = false
        set(value) {
            field = value
            navigationView.get()?.post {
                navigationView.get()?.menu?.findItem(R.id.nav_series)?.isVisible = !value && !isTeamMember
            }
        }

    var numberOfGames: Int = League.MAX_NUMBER_OF_GAMES
        set(value) {
            field = value
            navigationView.get()?.post {
                navGameItemIds.forEachIndexed { index, id ->
                    navigationView.get()?.menu?.findItem(id)?.isVisible = index < value
                }
            }
        }

    var bowlerName: CharSequence? = null
        set(value) {
            field = value
            navigationView.get()?.post {
                navigationView.get()?.getHeaderView(0)?.findViewById<TextView>(R.id.nav_bowler_name)?.text = value
            }
        }

    var leagueName: CharSequence? = null
        set(value) {
            field = value
            navigationView.get()?.post {
                navigationView.get()?.getHeaderView(0)?.findViewById<TextView>(R.id.nav_league_name)?.text = value
            }
        }

    var gameNumber: Int = 0
        set(value) {
            field = value
            navigationView.get()?.post {
                navGameItemIds.forEachIndexed { index, id ->
                    navigationView.get()?.menu?.findItem(id)?.isChecked = value == index
                }
            }
        }
}
