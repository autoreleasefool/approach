package ca.josephroque.bowlingcompanion.core.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.CheckpointDao
import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.dao.MatchPlayDao
import ca.josephroque.bowlingcompanion.core.database.dao.SeriesDao
import ca.josephroque.bowlingcompanion.core.database.legacy.dao.LegacyIDMappingDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyBowler
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyGame
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyTeam
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyIDMappingEntity
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyIDMappingKey
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyLeague
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyMatchPlay
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyMatchPlayResult
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacySeries
import ca.josephroque.bowlingcompanion.core.database.legacy.model.asMatchPlay
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity
import ca.josephroque.bowlingcompanion.core.database.model.MatchPlayEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamEntity
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import kotlinx.datetime.Instant
import java.util.UUID
import javax.inject.Inject

class OfflineFirstLegacyMigrationRepository @Inject constructor(
	private val bowlerDao: BowlerDao,
	private val teamDao: TeamDao,
	private val leagueDao: LeagueDao,
	private val seriesDao: SeriesDao,
	private val gameDao: GameDao,
	private val matchPlayDao: MatchPlayDao,
	private val legacyIDMappingDao: LegacyIDMappingDao,
	private val checkpointDao: CheckpointDao,
	private val transactionRunner: TransactionRunner,
): LegacyMigrationRepository {

	override suspend fun migrateTeams(teams: List<LegacyTeam>) = transactionRunner {
		val migratedTeams = mutableListOf<TeamEntity>()
		val idMappings = mutableListOf<LegacyIDMappingEntity>()

		for (legacyTeam in teams) {
			val id = UUID.randomUUID()
			idMappings.add(LegacyIDMappingEntity(
				id = id,
				legacyId = legacyTeam.id,
				key = LegacyIDMappingKey.TEAM
			))

			migratedTeams.add(TeamEntity(
				id = id,
				name = legacyTeam.name,
			))
		}

		legacyIDMappingDao.insertAll(idMappings)
		teamDao.insertAll(migratedTeams)
	}

	override suspend fun migrateBowlers(bowlers: List<LegacyBowler>) {
		val migratedBowlers = mutableListOf<BowlerEntity>()
		val idMappings = mutableListOf<LegacyIDMappingEntity>()

		for (legacyBowler in bowlers) {
			val id = UUID.randomUUID()
			idMappings.add(LegacyIDMappingEntity(
				id = id,
				legacyId = legacyBowler.id,
				key = LegacyIDMappingKey.BOWLER,
			))

			migratedBowlers.add(BowlerEntity(
				id = id,
				name = legacyBowler.name,
				kind = BowlerKind.PLAYABLE,
			))
		}

		legacyIDMappingDao.insertAll(idMappings)
		bowlerDao.insertAll(migratedBowlers)
	}

	override suspend fun migrateLeagues(leagues: List<LegacyLeague>) {
		val migratedLeagues = mutableListOf<LeagueEntity>()
		val idMappings = mutableListOf<LegacyIDMappingEntity>()

		val legacyBowlerIds = leagues.map(LegacyLeague::bowlerId)
		val bowlerIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyBowlerIds,
			key = LegacyIDMappingKey.BOWLER,
		).associateBy({ it.legacyId }, { it.id })

		for (legacyLeague in leagues) {
			val id = UUID.randomUUID()
			idMappings.add(LegacyIDMappingEntity(
				id = id,
				legacyId = legacyLeague.id,
				key = LegacyIDMappingKey.LEAGUE,
			))

			migratedLeagues.add(LeagueEntity(
				id = id,
				name = legacyLeague.name,
				recurrence = if (legacyLeague.isEvent) LeagueRecurrence.ONCE else LeagueRecurrence.REPEATING,
				additionalGames = if (legacyLeague.additionalGames == 0) null else legacyLeague.additionalGames,
				additionalPinFall = if (legacyLeague.additionalGames == 0 || legacyLeague.additionalPinFall == 0) null else legacyLeague.additionalPinFall,
				excludeFromStatistics = if (legacyLeague.name == LegacyLeague.PRACTICE_LEAGUE_NAME || legacyLeague.name == LegacyLeague.OPEN_LEAGUE_NAME) ExcludeFromStatistics.EXCLUDE else ExcludeFromStatistics.INCLUDE,
				numberOfGames = if (legacyLeague.gamesPerSeries == 0) null else legacyLeague.gamesPerSeries,
				bowlerId = bowlerIdMappings[legacyLeague.bowlerId]!!,
			))
		}

		legacyIDMappingDao.insertAll(idMappings)
		leagueDao.insertAll(migratedLeagues)
	}

	override suspend fun migrateSeries(series: List<LegacySeries>) {
		val migratedSeries = mutableListOf<SeriesEntity>()
		val idMappings = mutableListOf<LegacyIDMappingEntity>()

		val legacyLeagueIds = series.map(LegacySeries::leagueId)
		val leagueIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyLeagueIds,
			key = LegacyIDMappingKey.LEAGUE,
		).associateBy({ it.legacyId }, { it.id })

		for (legacySeries in series) {
			val id = UUID.randomUUID()
			idMappings.add(LegacyIDMappingEntity(
				id = id,
				legacyId = legacySeries.id,
				key = LegacyIDMappingKey.SERIES,
			))

			migratedSeries.add(SeriesEntity(
				id = id,
				date = Instant.fromEpochMilliseconds(legacySeries.date.time),
				numberOfGames = legacySeries.numberOfGames,
				excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
				preBowl = SeriesPreBowl.REGULAR,
				leagueId = leagueIdMappings[legacySeries.leagueId]!!,
				alleyId = null,
			))
		}

		legacyIDMappingDao.insertAll(idMappings)
		seriesDao.insertAll(migratedSeries)
	}

	override suspend fun migrateGames(games: List<LegacyGame>) {
		val migratedGames = mutableListOf<GameEntity>()
		val migratedMatchPlays = mutableListOf<MatchPlayEntity>()

		val gameIdMappings = mutableListOf<LegacyIDMappingEntity>()

		val legacySeriesIds = games.map(LegacyGame::seriesId)
		val seriesIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacySeriesIds,
			key = LegacyIDMappingKey.SERIES,
		).associateBy({ it.legacyId }, { it.id })

		for (legacyGame in games) {
			val gameId = UUID.randomUUID()
			gameIdMappings.add(LegacyIDMappingEntity(
				id = gameId,
				legacyId = legacyGame.id,
				key = LegacyIDMappingKey.GAME,
			))

			migratedGames.add(GameEntity(
				id = gameId,
				index = legacyGame.gameNumber - 1,
				score = legacyGame.score,
				locked = if (legacyGame.isLocked) GameLockState.LOCKED else GameLockState.UNLOCKED,
				scoringMethod = if (legacyGame.isManual) GameScoringMethod.MANUAL else GameScoringMethod.BY_FRAME,
				excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
				seriesId = seriesIdMappings[legacyGame.seriesId]!!,
			))

			if (legacyGame.matchPlayResult != LegacyMatchPlayResult.NONE) {
				val matchPlayId = UUID.randomUUID()

				migratedMatchPlays.add(MatchPlayEntity(
					id = matchPlayId,
					gameId = gameId,
					opponentId = null,
					opponentScore = null,
					result = legacyGame.matchPlayResult.asMatchPlay(),
				)
				)
			}
		}

		legacyIDMappingDao.insertAll(gameIdMappings)
		gameDao.insertAll(migratedGames)
		matchPlayDao.insertAll(migratedMatchPlays)
	}

	override suspend fun migrateMatchPlays(matchPlays: List<LegacyMatchPlay>) {
		val migratedMatchPlays = mutableListOf<MatchPlayEntity>()
		val matchPlayIdMappings = mutableListOf<LegacyIDMappingEntity>()

		val legacyGameIds = matchPlays.map(LegacyMatchPlay::gameId)
		val gameIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyGameIds,
			key = LegacyIDMappingKey.GAME,
		).associateBy({ it.legacyId }, { it.id })
		val existingMatchPlays = matchPlayDao
			.getMatchPlaysForGames(gameIdMappings.values)
			.associateBy { it.gameId }

		for (legacyMatchPlay in matchPlays) {
			if (legacyMatchPlay.opponentScore == 0 && legacyMatchPlay.opponentName.isNullOrBlank()) {
				continue
			}

			val gameId = gameIdMappings[legacyMatchPlay.gameId]!!
			val existingMatchPlay = existingMatchPlays[gameId]
			val matchPlayId = existingMatchPlay?.id ?: UUID.randomUUID()

			matchPlayIdMappings.add(LegacyIDMappingEntity(
				id = matchPlayId,
				legacyId = legacyMatchPlay.id,
				key = LegacyIDMappingKey.MATCH_PLAY,
			))

			migratedMatchPlays.add(MatchPlayEntity(
				id =  matchPlayId,
				opponentId = existingMatchPlay?.opponentId,
				opponentScore = existingMatchPlay?.opponentScore ?: legacyMatchPlay.opponentScore,
				result = existingMatchPlay?.result,
				gameId = gameId,
			))
		}

		legacyIDMappingDao.insertAll(matchPlayIdMappings)
		matchPlayDao.insertAll(migratedMatchPlays)
	}

	override suspend fun recordCheckpoint() {
		checkpointDao.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
	}
}