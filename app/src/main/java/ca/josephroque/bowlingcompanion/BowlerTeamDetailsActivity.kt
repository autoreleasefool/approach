package ca.josephroque.bowlingcompanion

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Activity to display bowler or team details
 */
class BowlerTeamDetailsActivity : AppCompatActivity() {

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bowler_team_details)
    }
}
