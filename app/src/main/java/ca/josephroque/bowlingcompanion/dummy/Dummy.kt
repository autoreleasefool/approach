package ca.josephroque.bowlingcompanion.dummy

import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.teams.Team
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Helper class for providing sample content.
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    val BOWLERS: MutableList<Bowler> = ArrayList()
    val TEAMS: MutableList<Team> = ArrayList()
    val LEAGUES: MutableList<League> = ArrayList()
    val EVENTS: MutableList<League> = ArrayList()

    val BOWLER_MAP: MutableMap<Long, Bowler> = HashMap()
    val TEAM_MAP: MutableMap<Long, Team> = HashMap()
    val LEAGUE_MAP: MutableMap<Long, League> = HashMap()
    val EVENT_MAP: MutableMap<Long, League> = HashMap()

    private val COUNT = 25

    init {
        for (i in 1..COUNT) {
            addBowler(createDummyBowler(i))
            addTeam(createDummyTeam(i))
            addLeague(createDummyLeague(i))
            addEvent(createDummyEvent(i))
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

    private fun addLeague(league: League) {
        LEAGUES.add(league)
        LEAGUE_MAP.put(league.id, league)
    }

    private fun addEvent(league: League) {
        EVENTS.add(league)
        EVENT_MAP.put(league.id, league)
    }

    private fun createDummyBowler(position: Int): Bowler {
        return Bowler("Trevor Hansen", 193.5, 0L)
    }

    private fun createDummyTeam(position: Int): Team {
        return Team("4 Steps Boys", 193.5, 0L)
    }

    private fun createDummyLeague(position: Int): League {
        return League("Wednesday Night, 16-17", 203.4, 0L, false, 3)
    }

    private fun createDummyEvent(position: Int): League {
        return League("4 Steps, 2017", 212.9, 0L, true, 5)
    }
}
