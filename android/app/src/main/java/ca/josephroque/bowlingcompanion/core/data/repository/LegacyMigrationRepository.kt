package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyBowler
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyFrame
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyGame
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyLeague
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyMatchPlay
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacySeries
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyTeam
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyTeamBowler

interface LegacyMigrationRepository {
	suspend fun migrateTeams(teams: List<LegacyTeam>)
	suspend fun migrateBowlers(bowlers: List<LegacyBowler>)
	suspend fun migrateTeamBowlers(teamBowlers: List<LegacyTeamBowler>)
	suspend fun migrateLeagues(leagues: List<LegacyLeague>)
	suspend fun migrateSeries(series: List<LegacySeries>)
	suspend fun migrateGames(games: List<LegacyGame>)
	suspend fun migrateMatchPlays(matchPlays: List<LegacyMatchPlay>)
	suspend fun migrateFrames(frames: List<LegacyFrame>)

	suspend fun recordCheckpoint()
}