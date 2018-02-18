package ca.josephroque.bowlingcompanion.bowlers.dummy

import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.teams.Team
import java.util.*

/**
 * Helper class for providing sample content.
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    val BOWLERS: MutableList<Bowler> = ArrayList()
    val TEAMS: MutableList<Team> = ArrayList()

    val BOWLER_MAP: MutableMap<Long, Bowler> = HashMap()
    val TEAM_MAP: MutableMap<Long, Team> = HashMap()

    private val COUNT = 25

    init {
        for (i in 1..COUNT) {
            addBowler(createDummyBowler(i))
            addTeam(createDummyTeam(i))
        }
    }

    private fun addBowler(bowler: Bowler) {
        BOWLERS.add(bowler)
        BOWLER_MAP.put(bowler.id, bowler)
    }

    private fun addTeam(team: Team) {
        TEAMS.add(team)
        TEAM_MAP.put(team.id, team)
    }

    private fun createDummyBowler(position: Int): Bowler {
        return Bowler("Trevor Hansen", 193.5, 0L)
    }

    private fun createDummyTeam(position: Int): Team {
        return Team("4 Steps Boys", 193.5, 0L)
    }
}
