package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.Team

interface TeamsRepository {
	suspend fun insertTeams(teams: List<Team>)
}