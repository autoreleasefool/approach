package ca.josephroque.bowlingcompanion.core.data.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getStringOrNull
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.FrameDao
import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.dao.MatchPlayDao
import ca.josephroque.bowlingcompanion.core.database.dao.SeriesDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamBowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyDatabaseHelper
import ca.josephroque.bowlingcompanion.core.database.legacy.dao.LegacyIDMappingDao
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyBowler
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyFrame
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyGame
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyIDMappingEntity
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyIDMappingKey
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyLeague
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyMatchPlay
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyMatchPlayResult
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacySeries
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyTeam
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyTeamBowler
import ca.josephroque.bowlingcompanion.core.database.legacy.model.asMatchPlay
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.FrameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity
import ca.josephroque.bowlingcompanion.core.database.model.MatchPlayEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamBowlerCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.TeamEntity
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class SQLiteMigrationService @Inject constructor(
	@ApplicationContext private val context: Context,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
	private val bowlerDao: BowlerDao,
	private val teamDao: TeamDao,
	private var teamBowlerDao: TeamBowlerDao,
	private val leagueDao: LeagueDao,
	private val seriesDao: SeriesDao,
	private val gameDao: GameDao,
	private val frameDao: FrameDao,
	private val matchPlayDao: MatchPlayDao,
	private val legacyIDMappingDao: LegacyIDMappingDao,
): MigrationService {

	override suspend fun migrateDefaultLegacyDatabase() {
		migrateDatabase(LegacyDatabaseHelper.DATABASE_NAME)
	}

	override suspend fun migrateDatabase(name: String) = withContext(ioDispatcher) {
		val legacyDb = LegacyDatabaseHelper.getInstance(context, name).readableDatabase

		transactionRunner {
			migrateTeams(db = legacyDb)
			migrateBowlers(db = legacyDb)
			migrateTeamBowlers(db = legacyDb)
			migrateLeagues(db = legacyDb)
			migrateSeries(db = legacyDb)
			migrateGames(db = legacyDb)
			migrateMatchPlays(db = legacyDb)
			migrateFrames(db = legacyDb)
		}

		LegacyDatabaseHelper.closeInstance()
	}

	private suspend fun migrateTeams(db: SQLiteDatabase) {
		val legacyTeams = getLegacyTeams(db)
		migrateTeamsToRoom(legacyTeams)
	}

	private fun getLegacyTeams(db: SQLiteDatabase): List<LegacyTeam> {
		val cursor = db.rawQuery(
			"""
		SELECT ${LegacyContract.TeamEntry._ID}, ${LegacyContract.TeamEntry.COLUMN_TEAM_NAME}
		FROM ${LegacyContract.TeamEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
		)

		val teams = mutableListOf<LegacyTeam>()
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast) {
				val teamIdIndex = cursor.getColumnIndex(LegacyContract.TeamEntry._ID)
				val teamNameIndex = cursor.getColumnIndex(LegacyContract.TeamEntry.COLUMN_TEAM_NAME)

				if (teamIdIndex != -1 && teamNameIndex != -1) {
					val id = cursor.getLong(teamIdIndex)
					val name = cursor.getString(teamNameIndex)

					teams.add(LegacyTeam(id = id, name = name))
				}

				cursor.moveToNext()
			}
		}

		cursor.close()
		return teams
	}

	private suspend fun migrateTeamsToRoom(teams: List<LegacyTeam>) {
		val migratedTeams = mutableListOf<TeamEntity>()
		val idMappings = mutableListOf<LegacyIDMappingEntity>()

		for (legacyTeam in teams) {
			val id = UUID.randomUUID()
			idMappings.add(LegacyIDMappingEntity(
				id = id,
				legacyId = legacyTeam.id,
				key = LegacyIDMappingKey.TEAM
			))

			migratedTeams.add(
				TeamEntity(
				id = id,
				name = legacyTeam.name,
			)
			)
		}

		legacyIDMappingDao.insertAll(idMappings)
		teamDao.migrateAll(migratedTeams)
	}

	private suspend fun migrateBowlers(db: SQLiteDatabase) {
		val legacyBowlers = getLegacyBowlers(db)
		migrateBowlersToRoom(legacyBowlers)
	}

	private fun getLegacyBowlers(db: SQLiteDatabase): List<LegacyBowler> {
		val cursor = db.rawQuery(
			"""
		SELECT ${LegacyContract.BowlerEntry._ID}, ${LegacyContract.BowlerEntry.COLUMN_BOWLER_NAME}
		FROM ${LegacyContract.BowlerEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
		)

		val bowlers = mutableListOf<LegacyBowler>()
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast) {
				val bowlerIdIndex = cursor.getColumnIndex(LegacyContract.BowlerEntry._ID)
				val bowlerNameIndex = cursor.getColumnIndex(LegacyContract.BowlerEntry.COLUMN_BOWLER_NAME)

				if (bowlerIdIndex != -1 && bowlerNameIndex != -1) {
					val id = cursor.getLong(bowlerIdIndex)
					val name = cursor.getString(bowlerNameIndex)

					bowlers.add(LegacyBowler(id = id, name = name))
				}

				cursor.moveToNext()
			}
		}

		cursor.close()
		return bowlers
	}

	private suspend fun migrateBowlersToRoom(bowlers: List<LegacyBowler>) {
		val migratedBowlers = mutableListOf<BowlerEntity>()
		val idMappings = mutableListOf<LegacyIDMappingEntity>()

		for (legacyBowler in bowlers) {
			val id = UUID.randomUUID()
			idMappings.add(
				LegacyIDMappingEntity(
				id = id,
				legacyId = legacyBowler.id,
				key = LegacyIDMappingKey.BOWLER,
			)
			)

			migratedBowlers.add(
				BowlerEntity(
				id = id,
				name = legacyBowler.name,
				kind = BowlerKind.PLAYABLE,
			)
			)
		}

		legacyIDMappingDao.insertAll(idMappings)
		bowlerDao.migrateAll(migratedBowlers)
	}

	private suspend fun migrateTeamBowlers(db: SQLiteDatabase) {
		val teamBowlers = getLegacyTeamBowlers(db)
		migrateTeamBowlersToRoom(teamBowlers)
	}

	private fun getLegacyTeamBowlers(db: SQLiteDatabase): List<LegacyTeamBowler> {
		val cursor = db.rawQuery(
			"""
			SELECT ${LegacyContract.TeamBowlerEntry.COLUMN_BOWLER_ID}, ${LegacyContract.TeamBowlerEntry.COLUMN_TEAM_ID}
			FROM ${LegacyContract.TeamBowlerEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
		)

		val teamBowlers = mutableListOf<LegacyTeamBowler>()
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast) {
				val bowlerIdIndex = cursor.getColumnIndex(LegacyContract.TeamBowlerEntry.COLUMN_BOWLER_ID)
				val teamIdIndex = cursor.getColumnIndex(LegacyContract.TeamBowlerEntry.COLUMN_TEAM_ID)

				if (bowlerIdIndex != -1 && teamIdIndex != -1) {
					val bowlerId = cursor.getLong(bowlerIdIndex)
					val teamId = cursor.getLong(teamIdIndex)

					teamBowlers.add(LegacyTeamBowler(teamId = teamId, bowlerId = bowlerId))
				}

				cursor.moveToNext()
			}
		}

		cursor.close()
		return teamBowlers
	}

	private suspend fun migrateTeamBowlersToRoom(teamBowlers: List<LegacyTeamBowler>) {
		val migratedTeamBowlers = mutableListOf<TeamBowlerCrossRef>()

		val legacyBowlerIds = teamBowlers.map(LegacyTeamBowler::bowlerId)
		val bowlerIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyBowlerIds,
			key = LegacyIDMappingKey.BOWLER,
		).associateBy({ it.legacyId }, { it.id })

		val legacyTeamIds = teamBowlers.map(LegacyTeamBowler::teamId)
		val teamIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyTeamIds,
			key = LegacyIDMappingKey.TEAM,
		).associateBy({ it.legacyId }, { it.id })

		for (legacyTeamBowler in teamBowlers) {
			migratedTeamBowlers.add(
				TeamBowlerCrossRef(
					teamId = teamIdMappings[legacyTeamBowler.teamId]!!,
					bowlerId = bowlerIdMappings[legacyTeamBowler.bowlerId]!!,
				)
			)
		}

		teamBowlerDao.migrateAll(migratedTeamBowlers)
	}

	private suspend fun migrateLeagues(db: SQLiteDatabase) {
		val legacyLeagues = getLegacyLeagues(db)
		migrateLeaguesToRoom(legacyLeagues)
	}

	private fun getLegacyLeagues(db: SQLiteDatabase): List<LegacyLeague> {
		val cursor = db.rawQuery(
			"""
			SELECT
				${LegacyContract.LeagueEntry._ID},
				${LegacyContract.LeagueEntry.COLUMN_LEAGUE_NAME},
				${LegacyContract.LeagueEntry.COLUMN_NUMBER_OF_GAMES},
				${LegacyContract.LeagueEntry.COLUMN_ADDITIONAL_GAMES},
				${LegacyContract.LeagueEntry.COLUMN_ADDITIONAL_PINFALL},
				${LegacyContract.LeagueEntry.COLUMN_IS_EVENT},
				${LegacyContract.LeagueEntry.COLUMN_BOWLER_ID}
			FROM ${LegacyContract.LeagueEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
		)

		val leagues = mutableListOf<LegacyLeague>()
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast) {
				val leagueIdIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry._ID)
				val leagueNameIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_LEAGUE_NAME)
				val leagueNumberOfGamesIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_NUMBER_OF_GAMES)
				val leagueAdditionalGamesIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_ADDITIONAL_GAMES)
				val leagueAdditionalPinFallIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_ADDITIONAL_PINFALL)
				val leagueIsEventIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_IS_EVENT)
				val leagueBowlerIdIndex = cursor.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_BOWLER_ID)

				val id = cursor.getLong(leagueIdIndex)
				val name = cursor.getString(leagueNameIndex)
				val numberOfGames = cursor.getInt(leagueNumberOfGamesIndex)
				val additionalGames = cursor.getInt(leagueAdditionalGamesIndex)
				val additionalPinFall = cursor.getInt(leagueAdditionalPinFallIndex)
				val isEvent = cursor.getInt(leagueIsEventIndex) == 1
				val bowlerId = cursor.getLong(leagueBowlerIdIndex)

				leagues.add(LegacyLeague(
					id = id,
					name = name,
					isEvent = isEvent,
					gamesPerSeries = numberOfGames,
					additionalGames = additionalGames,
					additionalPinFall = additionalPinFall,
					bowlerId = bowlerId,
				))

				cursor.moveToNext()
			}
		}

		cursor.close()
		return leagues
	}

	private suspend fun migrateLeaguesToRoom(leagues: List<LegacyLeague>) {
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

			@Suppress("DEPRECATION")
			migratedLeagues.add(
				LeagueEntity(
				id = id,
				name = legacyLeague.name,
				recurrence = if (legacyLeague.isEvent) LeagueRecurrence.ONCE else LeagueRecurrence.REPEATING,
				additionalGames = if (legacyLeague.additionalGames == 0) null else legacyLeague.additionalGames,
				additionalPinFall = if (legacyLeague.additionalGames == 0 || legacyLeague.additionalPinFall == 0) null else legacyLeague.additionalPinFall,
				excludeFromStatistics = if (legacyLeague.name == LegacyLeague.PRACTICE_LEAGUE_NAME || legacyLeague.name == LegacyLeague.OPEN_LEAGUE_NAME) ExcludeFromStatistics.EXCLUDE else ExcludeFromStatistics.INCLUDE,
				numberOfGames = if (legacyLeague.gamesPerSeries == 0) null else legacyLeague.gamesPerSeries,
				bowlerId = bowlerIdMappings[legacyLeague.bowlerId]!!,
			)
			)
		}

		legacyIDMappingDao.insertAll(idMappings)
		leagueDao.migrateAll(migratedLeagues)
	}

	private suspend fun migrateSeries(db: SQLiteDatabase) {
		val legacySeries = getLegacySeries(db)
		migrateSeriesToRoom(legacySeries)
	}

	private fun getLegacySeries(db: SQLiteDatabase): List<LegacySeries> {
		val cursor = db.rawQuery(
			"""
			SELECT
				series.${LegacyContract.SeriesEntry._ID},
				series.${LegacyContract.SeriesEntry.COLUMN_SERIES_DATE},
				series.${LegacyContract.SeriesEntry.COLUMN_LEAGUE_ID},
				COUNT(*) AS numberOfGames
			FROM
				${LegacyContract.SeriesEntry.TABLE_NAME} AS series,
				${LegacyContract.GameEntry.TABLE_NAME} AS game
			WHERE
				series.${LegacyContract.SeriesEntry._ID} = game.${LegacyContract.GameEntry.COLUMN_SERIES_ID}
			GROUP BY
				series.${LegacyContract.SeriesEntry._ID}
		""".trimIndent(), emptyArray()
		)

		val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)

		val series = mutableListOf<LegacySeries>()
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast) {
				val seriesIdIndex = cursor.getColumnIndex(LegacyContract.SeriesEntry._ID)
				val seriesDateIndex = cursor.getColumnIndex(LegacyContract.SeriesEntry.COLUMN_SERIES_DATE)
				val seriesLeagueIdIndex = cursor.getColumnIndex(LegacyContract.SeriesEntry.COLUMN_LEAGUE_ID)
				val seriesNumberOfGamesIndex = cursor.getColumnIndex("numberOfGames")

				val id = cursor.getLong(seriesIdIndex)
				val leagueId = cursor.getLong(seriesLeagueIdIndex)
				val numberOfGames = cursor.getInt(seriesNumberOfGamesIndex)
				val date = dateFormatter.parse(cursor.getString(seriesDateIndex)) ?: Date()

				series.add(LegacySeries(
					id = id,
					date = date,
					numberOfGames = numberOfGames,
					leagueId = leagueId,
				))

				cursor.moveToNext()
			}
		}

		cursor.close()
		return series
	}

	private suspend fun migrateSeriesToRoom(series: List<LegacySeries>) {
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

			migratedSeries.add(
				SeriesEntity(
				id = id,
				date = Instant
					.fromEpochMilliseconds(legacySeries.date.time)
					.toLocalDate(),
				excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
				preBowl = SeriesPreBowl.REGULAR,
				leagueId = leagueIdMappings[legacySeries.leagueId]!!,
				alleyId = null,
			)
			)
		}

		legacyIDMappingDao.insertAll(idMappings)
		seriesDao.migrateAll(migratedSeries)
	}

	private suspend fun migrateGames(db: SQLiteDatabase) {
		val legacyGames = getLegacyGames(db)
		migrateGamesToRoom(legacyGames)
	}

	private fun getLegacyGames(db: SQLiteDatabase): List<LegacyGame> {
		val cursor = db.rawQuery(
			"""
			SELECT
				${LegacyContract.GameEntry._ID},
				${LegacyContract.GameEntry.COLUMN_GAME_NUMBER},
				${LegacyContract.GameEntry.COLUMN_SCORE},
				${LegacyContract.GameEntry.COLUMN_IS_LOCKED},
				${LegacyContract.GameEntry.COLUMN_IS_MANUAL},
				${LegacyContract.GameEntry.COLUMN_MATCH_PLAY},
				${LegacyContract.GameEntry.COLUMN_SERIES_ID}
			FROM
				${LegacyContract.GameEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
		)

		val games = mutableListOf<LegacyGame>()
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast) {
				val gameIdIndex = cursor.getColumnIndex(LegacyContract.GameEntry._ID)
				val gameNumberIndex = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_GAME_NUMBER)
				val gameScoreIndex = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_SCORE)
				val gameIsLockedIndex = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_IS_LOCKED)
				val gameIsManualIndex = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_IS_MANUAL)
				val gameMatchPlayIndex = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_MATCH_PLAY)
				val gameSeriesId = cursor.getColumnIndex(LegacyContract.GameEntry.COLUMN_SERIES_ID)

				val id = cursor.getLong(gameIdIndex)
				val gameNumber = cursor.getInt(gameNumberIndex)
				val score = cursor.getInt(gameScoreIndex)
				val isLocked = cursor.getInt(gameIsLockedIndex) == 1
				val isManual = cursor.getInt(gameIsManualIndex) == 1
				val matchPlay = LegacyMatchPlayResult.fromInt(cursor.getInt(gameMatchPlayIndex)) ?: LegacyMatchPlayResult.NONE
				val seriesId = cursor.getLong(gameSeriesId)

				games.add(LegacyGame(
					id = id,
					gameNumber = gameNumber,
					score = score,
					isLocked = isLocked,
					isManual = isManual,
					matchPlayResult = matchPlay,
					seriesId = seriesId,
				))

				cursor.moveToNext()
			}
		}

		cursor.close()
		return games
	}

	private suspend fun migrateGamesToRoom(games: List<LegacyGame>) {
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

			migratedGames.add(
				GameEntity(
				id = gameId,
				index = legacyGame.gameNumber - 1,
				score = legacyGame.score,
				locked = if (legacyGame.isLocked) GameLockState.LOCKED else GameLockState.UNLOCKED,
				scoringMethod = if (legacyGame.isManual) GameScoringMethod.MANUAL else GameScoringMethod.BY_FRAME,
				excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
				seriesId = seriesIdMappings[legacyGame.seriesId]!!,
			)
			)

			if (legacyGame.matchPlayResult != LegacyMatchPlayResult.NONE) {
				val matchPlayId = UUID.randomUUID()

				migratedMatchPlays.add(
					MatchPlayEntity(
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
		gameDao.migrateAll(migratedGames)
		matchPlayDao.migrateAll(migratedMatchPlays)
	}

	private suspend fun migrateMatchPlays(db: SQLiteDatabase) {
		val legacyMatchPlays = getLegacyMatchPlsys(db)
		migrateMatchPlaysToRoom(legacyMatchPlays)
	}

	private fun getLegacyMatchPlsys(db: SQLiteDatabase): List<LegacyMatchPlay> {
		val cursor = db.rawQuery(
			"""
			SELECT
				${LegacyContract.MatchPlayEntry._ID},
				${LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_NAME},
				${LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_SCORE},
				${LegacyContract.MatchPlayEntry.COLUMN_GAME_ID}
			FROM ${LegacyContract.MatchPlayEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
		)

		val matchPlays = mutableListOf<LegacyMatchPlay>()
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast) {
				val matchPlayIdIndex = cursor.getColumnIndex(LegacyContract.MatchPlayEntry._ID)
				val matchPlayOppNameIndex = cursor.getColumnIndex(LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_NAME)
				val matchPlayOppScoreIndex = cursor.getColumnIndex(LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_SCORE)
				val matchPlayGameId = cursor.getColumnIndex(LegacyContract.MatchPlayEntry.COLUMN_GAME_ID)

				val id = cursor.getLong(matchPlayIdIndex)
				val opponentName = cursor.getStringOrNull(matchPlayOppNameIndex)
				val opponentScore = cursor.getInt(matchPlayOppScoreIndex)
				val gameId = cursor.getLong(matchPlayGameId)

				matchPlays.add(
					LegacyMatchPlay(
						id = id,
						opponentName = opponentName,
						opponentScore = opponentScore,
						gameId = gameId,
					)
				)

				cursor.moveToNext()
			}
		}

		cursor.close()
		return matchPlays
	}

	private suspend fun migrateMatchPlaysToRoom(matchPlays: List<LegacyMatchPlay>) {
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
		matchPlayDao.migrateAll(migratedMatchPlays)
	}

	private suspend fun migrateFrames(db: SQLiteDatabase) {
		val legacyFrames = getLegacyFrames(db)
		migrateFramesToRoom(legacyFrames)
	}

	private fun getLegacyFrames(db: SQLiteDatabase): List<LegacyFrame> {
		val cursor = db.rawQuery(
			"""
			SELECT
				${LegacyContract.FrameEntry._ID},
				${LegacyContract.FrameEntry.COLUMN_FRAME_NUMBER},
				${LegacyContract.FrameEntry.COLUMN_IS_ACCESSED},
				${LegacyContract.FrameEntry.COLUMN_PIN_STATE[0]},
				${LegacyContract.FrameEntry.COLUMN_PIN_STATE[1]},
				${LegacyContract.FrameEntry.COLUMN_PIN_STATE[2]},
				${LegacyContract.FrameEntry.COLUMN_FOULS},
				${LegacyContract.FrameEntry.COLUMN_GAME_ID}
			FROM ${LegacyContract.FrameEntry.TABLE_NAME}
		""".trimIndent(), emptyArray()
		)

		val frames = mutableListOf<LegacyFrame>()
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast) {
				val frameIdIndex = cursor.getColumnIndex(LegacyContract.FrameEntry._ID)
				val frameNumberIndex = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_FRAME_NUMBER)
				val frameIsAccessedIndex = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_IS_ACCESSED)
				val framePinState0Index = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_PIN_STATE[0])
				val framePinState1Index = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_PIN_STATE[1])
				val framePinState2Index = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_PIN_STATE[2])
				val frameFoulsIndex = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_FOULS)
				val frameGameIdIndex = cursor.getColumnIndex(LegacyContract.FrameEntry.COLUMN_GAME_ID)

				val id = cursor.getLong(frameIdIndex)
				val frameNumber = cursor.getInt(frameNumberIndex)
				val isAccessed = cursor.getInt(frameIsAccessedIndex) == 1
				val pinState0 = cursor.getInt(framePinState0Index)
				val pinState1 = cursor.getInt(framePinState1Index)
				val pinState2 = cursor.getInt(framePinState2Index)
				val fouls = cursor.getInt(frameFoulsIndex)
				val gameId = cursor.getLong(frameGameIdIndex)

				frames.add(LegacyFrame(
					id = id,
					ordinal = frameNumber,
					isAccessed = isAccessed,
					firstPinState = pinState0,
					secondPinState = pinState1,
					thirdPinState = pinState2,
					fouls = fouls,
					gameId = gameId,
				))

				cursor.moveToNext()
			}
		}

		cursor.close()
		return frames
	}

	private suspend fun migrateFramesToRoom(frames: List<LegacyFrame>) {
		val migratedFrames = mutableListOf<FrameEntity>()

		val legacyGameIds = frames.map(LegacyFrame::gameId)
		val gameIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyGameIds,
			key = LegacyIDMappingKey.GAME,
		).associateBy({ it.legacyId }, { it.id })

		for (legacyFrame in frames) {
			val fouls = (if (legacyFrame.fouls in 24..30) legacyFrame.fouls - 23 else 0)
				.toString(2)
				.padStart(3)
				.map { it == '1' }

			val rollPins = listOf(
				legacyFrame.firstPinState,
				legacyFrame.firstPinState.xor(legacyFrame.secondPinState),
				legacyFrame.secondPinState.xor(legacyFrame.thirdPinState),
			)

			val rolls = (fouls zip rollPins)
				.map { "${if (it.first) 1 else 0}${Integer.toBinaryString(it.second).padStart(5, '0')}" }

			migratedFrames.add(FrameEntity(
				gameId = gameIdMappings[legacyFrame.gameId]!!,
				index = legacyFrame.ordinal - 1,
				roll0 = FrameEntity.Roll.fromBitString(rolls[0]),
				roll1 = FrameEntity.Roll.fromBitString(rolls[1]),
				roll2 = FrameEntity.Roll.fromBitString(rolls[2]),
				ball0 = null,
				ball1 = null,
				ball2 = null,
			))
		}

		frameDao.migrateAll(migratedFrames)
	}
}