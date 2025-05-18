package ca.josephroque.bowlingcompanion.core.data.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.database.getStringOrNull
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.common.filesystem.FileManager
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.database.ApproachDatabase
import ca.josephroque.bowlingcompanion.core.database.DATABASE_NAME
import ca.josephroque.bowlingcompanion.core.database.DATABASE_SHM_NAME
import ca.josephroque.bowlingcompanion.core.database.DATABASE_WAL_NAME
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
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerSortOrder
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.MatchPlayID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.TeamID
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class SQLiteMigrationService @Inject constructor(
	@ApplicationContext private val context: Context,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
	private val fileManager: FileManager,
	private val bowlerDao: BowlerDao,
	private val teamDao: TeamDao,
	private var teamBowlerDao: TeamBowlerDao,
	private val leagueDao: LeagueDao,
	private val seriesDao: SeriesDao,
	private val gameDao: GameDao,
	private val frameDao: FrameDao,
	private val matchPlayDao: MatchPlayDao,
	private val legacyIDMappingDao: LegacyIDMappingDao,
) : MigrationService {
	override fun getLegacyDatabasePath(name: String): File = fileManager.getDatabasePath(name)
	override fun getLegacyDatabaseUri(name: String): Uri {
		val exportDestination = fileManager.exportsDir
			.resolve("error_$name.db")
		getLegacyDatabasePath(name).copyTo(exportDestination, overwrite = true)

		return FileProvider.getUriForFile(
			context,
			"ca.josephroque.bowlingcompanion.fileprovider",
			exportDestination,
		)
	}

	private val potentiallyValidDatabaseBackupDestination: File
		get() {
			val currentDate = Clock.System.now().toLocalDate()
			return fileManager.exportsDir
				.resolve("potentially_valid_$currentDate.db")
		}

	private val databaseFiles = listOf(
		fileManager.getDatabasePath(DATABASE_NAME),
		fileManager.getDatabasePath(DATABASE_SHM_NAME),
		fileManager.getDatabasePath(DATABASE_WAL_NAME),
	)

	override suspend fun getDatabaseType(name: String): DatabaseType? {
		context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null).use { db ->
			db.rawQuery(
				"PRAGMA table_info(${LegacyContract.BowlerEntry.TABLE_NAME})",
				emptyArray(),
			).use {
				if (it.moveToFirst()) {
					while (!it.isAfterLast) {
						val columnNameIndex = it.getColumnIndex("name")
						if (columnNameIndex != -1) {
							val columnName = it.getString(columnNameIndex)
							if (columnName == LegacyContract.BowlerEntry.COLUMN_BOWLER_NAME) {
								return DatabaseType.BOWLING_COMPANION
							} else if (columnName == "name") {
								return DatabaseType.APPROACH
							}
						}
						it.moveToNext()
					}
				}
			}
		}

		return null
	}

	override suspend fun migrateDefaultLegacyDatabase(): MigrationResult =
		migrateDatabase(LegacyDatabaseHelper.DATABASE_NAME)

	override suspend fun migrateDatabase(name: String): MigrationResult = withContext(ioDispatcher) {
		var result: MigrationResult = MigrationResult.Success

		// Forces the database to be re-opened and re-rerun migrations
		ApproachDatabase.close()

		// Due to #589 we might have accidentally created some valid data before the migration.
		// Just in case, so we don't lose this data, we create a zipped backup
		if (databaseFiles.any { it.exists() }) {
			val destinationFile = potentiallyValidDatabaseBackupDestination
			destinationFile.parentFile?.mkdirs()
			fileManager.zipFiles(
				destinationFile,
				databaseFiles,
			)

			result = MigrationResult.SuccessWithWarnings(
				didCreateIssue589Backup = true,
			)
		}

		LegacyDatabaseHelper.getInstance(context, name).let { dbHelper ->
			dbHelper.readableDatabase.use { db ->
				transactionRunner {
					migrateTeams(db = db)
					migrateBowlers(db = db)
					migrateTeamBowlers(db = db)
					migrateLeagues(db = db)
					migrateSeries(db = db)
					migrateGames(db = db)
					migrateMatchPlays(db = db)
					migrateFrames(db = db)
				}
			}
		}

		LegacyDatabaseHelper.closeInstance()
		result
	}

	private suspend fun migrateTeams(db: SQLiteDatabase) {
		val legacyTeams = getLegacyTeams(db)
		migrateTeamsToRoom(legacyTeams)
	}

	private fun getLegacyTeams(db: SQLiteDatabase): List<LegacyTeam> {
		val teams = mutableListOf<LegacyTeam>()

		db.rawQuery(
			"""
		SELECT ${LegacyContract.TeamEntry._ID}, ${LegacyContract.TeamEntry.COLUMN_TEAM_NAME}
		FROM ${LegacyContract.TeamEntry.TABLE_NAME}
			""".trimIndent(),
			emptyArray(),
		).use {
			if (it.moveToFirst()) {
				while (!it.isAfterLast) {
					val teamIdIndex = it.getColumnIndex(LegacyContract.TeamEntry._ID)
					val teamNameIndex = it.getColumnIndex(LegacyContract.TeamEntry.COLUMN_TEAM_NAME)

					if (teamIdIndex != -1 && teamNameIndex != -1) {
						val id = it.getLong(teamIdIndex)
						val name = it.getString(teamNameIndex)

						teams.add(LegacyTeam(id = id, name = name))
					}

					it.moveToNext()
				}
			}
		}

		return teams
	}

	private suspend fun migrateTeamsToRoom(teams: List<LegacyTeam>) {
		val migratedTeams = mutableListOf<TeamEntity>()
		val idMappings = mutableListOf<LegacyIDMappingEntity>()

		for (legacyTeam in teams) {
			val id = TeamID.randomID()
			idMappings.add(
				LegacyIDMappingEntity(
					id = id.value,
					legacyId = legacyTeam.id,
					key = LegacyIDMappingKey.TEAM,
				),
			)

			migratedTeams.add(
				TeamEntity(
					id = id,
					name = legacyTeam.name,
				),
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
		val bowlers = mutableListOf<LegacyBowler>()

		db.rawQuery(
			"""
		SELECT ${LegacyContract.BowlerEntry._ID}, ${LegacyContract.BowlerEntry.COLUMN_BOWLER_NAME}
		FROM ${LegacyContract.BowlerEntry.TABLE_NAME}
			""".trimIndent(),
			emptyArray(),
		).use {
			if (it.moveToFirst()) {
				while (!it.isAfterLast) {
					val bowlerIdIndex = it.getColumnIndex(LegacyContract.BowlerEntry._ID)
					val bowlerNameIndex = it.getColumnIndex(LegacyContract.BowlerEntry.COLUMN_BOWLER_NAME)

					if (bowlerIdIndex != -1 && bowlerNameIndex != -1) {
						val id = it.getLong(bowlerIdIndex)
						val name = it.getString(bowlerNameIndex)

						bowlers.add(LegacyBowler(id = id, name = name))
					}

					it.moveToNext()
				}
			}
		}

		return bowlers
	}

	private suspend fun migrateBowlersToRoom(bowlers: List<LegacyBowler>) {
		val migratedBowlers = mutableListOf<BowlerEntity>()
		val idMappings = mutableListOf<LegacyIDMappingEntity>()

		for (legacyBowler in bowlers) {
			val id = BowlerID.randomID()
			idMappings.add(
				LegacyIDMappingEntity(
					id = id.value,
					legacyId = legacyBowler.id,
					key = LegacyIDMappingKey.BOWLER,
				),
			)

			migratedBowlers.add(
				BowlerEntity(
					id = id,
					name = legacyBowler.name,
					kind = BowlerKind.PLAYABLE,
				),
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
		val teamBowlers = mutableListOf<LegacyTeamBowler>()

		db.rawQuery(
			"""
			SELECT ${LegacyContract.TeamBowlerEntry.COLUMN_BOWLER_ID}, ${LegacyContract.TeamBowlerEntry.COLUMN_TEAM_ID}
			FROM ${LegacyContract.TeamBowlerEntry.TABLE_NAME}
			""".trimIndent(),
			emptyArray(),
		).use {
			if (it.moveToFirst()) {
				while (!it.isAfterLast) {
					val bowlerIdIndex = it.getColumnIndex(LegacyContract.TeamBowlerEntry.COLUMN_BOWLER_ID)
					val teamIdIndex = it.getColumnIndex(LegacyContract.TeamBowlerEntry.COLUMN_TEAM_ID)

					if (bowlerIdIndex != -1 && teamIdIndex != -1) {
						val bowlerId = it.getLong(bowlerIdIndex)
						val teamId = it.getLong(teamIdIndex)

						teamBowlers.add(LegacyTeamBowler(teamId = teamId, bowlerId = bowlerId))
					}

					it.moveToNext()
				}
			}
		}

		return teamBowlers
	}

	private suspend fun migrateTeamBowlersToRoom(teamBowlers: List<LegacyTeamBowler>) {
		val migratedTeamBowlers = mutableListOf<TeamBowlerCrossRef>()

		val legacyBowlerIds = teamBowlers.map(LegacyTeamBowler::bowlerId).toSet().toList()
		val bowlerIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyBowlerIds,
			key = LegacyIDMappingKey.BOWLER,
		).associateBy({ it.legacyId }, { it.id })

		val legacyTeamIds = teamBowlers.map(LegacyTeamBowler::teamId).toSet().toList()
		val teamIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyTeamIds,
			key = LegacyIDMappingKey.TEAM,
		).associateBy({ it.legacyId }, { TeamID(it.id) })

		for (legacyTeamBowler in teamBowlers) {
			val teamId = teamIdMappings[legacyTeamBowler.teamId] ?: continue
			val bowlerId = bowlerIdMappings[legacyTeamBowler.bowlerId] ?: continue

			migratedTeamBowlers.add(
				TeamBowlerCrossRef(
					teamId = teamId,
					bowlerId = BowlerID(bowlerId),
					position = 0,
				),
			)
		}

		teamBowlerDao.migrateAll(migratedTeamBowlers)
	}

	private suspend fun migrateLeagues(db: SQLiteDatabase) {
		val legacyLeagues = getLegacyLeagues(db)
		migrateLeaguesToRoom(legacyLeagues)
	}

	private fun getLegacyLeagues(db: SQLiteDatabase): List<LegacyLeague> {
		val leagues = mutableListOf<LegacyLeague>()

		db.rawQuery(
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
			""".trimIndent(),
			emptyArray(),
		).use {
			if (it.moveToFirst()) {
				while (!it.isAfterLast) {
					val leagueIdIndex = it.getColumnIndex(LegacyContract.LeagueEntry._ID)
					val leagueNameIndex = it.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_LEAGUE_NAME)
					val leagueNumberOfGamesIndex =
						it.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_NUMBER_OF_GAMES)
					val leagueAdditionalGamesIndex =
						it.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_ADDITIONAL_GAMES)
					val leagueAdditionalPinFallIndex =
						it.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_ADDITIONAL_PINFALL)
					val leagueIsEventIndex = it.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_IS_EVENT)
					val leagueBowlerIdIndex =
						it.getColumnIndex(LegacyContract.LeagueEntry.COLUMN_BOWLER_ID)

					val id = it.getLong(leagueIdIndex)
					val name = it.getString(leagueNameIndex)
					val numberOfGames = it.getInt(leagueNumberOfGamesIndex)
					val additionalGames = it.getInt(leagueAdditionalGamesIndex)
					val additionalPinFall = it.getInt(leagueAdditionalPinFallIndex)
					val isEvent = it.getInt(leagueIsEventIndex) == 1
					val bowlerId = it.getLong(leagueBowlerIdIndex)

					leagues.add(
						LegacyLeague(
							id = id,
							name = name,
							isEvent = isEvent,
							gamesPerSeries = numberOfGames,
							additionalGames = additionalGames,
							additionalPinFall = additionalPinFall,
							bowlerId = bowlerId,
						),
					)

					it.moveToNext()
				}
			}
		}

		return leagues
	}

	private suspend fun migrateLeaguesToRoom(leagues: List<LegacyLeague>) {
		val migratedLeagues = mutableListOf<LeagueEntity>()
		val idMappings = mutableListOf<LegacyIDMappingEntity>()

		val legacyBowlerIds = leagues.map(LegacyLeague::bowlerId).toSet().toList()
		val bowlerIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyBowlerIds,
			key = LegacyIDMappingKey.BOWLER,
		).associateBy({ it.legacyId }, { it.id })

		for (legacyLeague in leagues) {
			val bowlerId = bowlerIdMappings[legacyLeague.bowlerId] ?: continue
			val id = LeagueID.randomID()
			idMappings.add(
				LegacyIDMappingEntity(
					id = id.value,
					legacyId = legacyLeague.id,
					key = LegacyIDMappingKey.LEAGUE,
				),
			)

			@Suppress("DEPRECATION")
			migratedLeagues.add(
				LeagueEntity(
					id = id,
					name = legacyLeague.name,
					recurrence = if (legacyLeague.isEvent) {
						LeagueRecurrence.ONCE
					} else {
						LeagueRecurrence.REPEATING
					},
					additionalGames = if (legacyLeague.additionalGames == 0) {
						null
					} else {
						legacyLeague.additionalGames
					},
					additionalPinFall = if (
						legacyLeague.additionalGames == 0 ||
						legacyLeague.additionalPinFall == 0
					) {
						null
					} else {
						legacyLeague.additionalPinFall
					},
					excludeFromStatistics = if (
						legacyLeague.name == LegacyLeague.PRACTICE_LEAGUE_NAME ||
						legacyLeague.name == LegacyLeague.OPEN_LEAGUE_NAME
					) {
						ExcludeFromStatistics.EXCLUDE
					} else {
						ExcludeFromStatistics.INCLUDE
					},
					numberOfGames = if (legacyLeague.gamesPerSeries == 0) {
						null
					} else {
						legacyLeague.gamesPerSeries
					},
					bowlerId = BowlerID(bowlerId),
				),
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
		val series = mutableListOf<LegacySeries>()

		db.rawQuery(
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
			""".trimIndent(),
			emptyArray(),
		).use {
			val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)

			if (it.moveToFirst()) {
				while (!it.isAfterLast) {
					val seriesIdIndex = it.getColumnIndex(LegacyContract.SeriesEntry._ID)
					val seriesDateIndex = it.getColumnIndex(LegacyContract.SeriesEntry.COLUMN_SERIES_DATE)
					val seriesLeagueIdIndex =
						it.getColumnIndex(LegacyContract.SeriesEntry.COLUMN_LEAGUE_ID)
					val seriesNumberOfGamesIndex = it.getColumnIndex("numberOfGames")

					val id = it.getLong(seriesIdIndex)
					val leagueId = it.getLong(seriesLeagueIdIndex)
					val numberOfGames = it.getInt(seriesNumberOfGamesIndex)
					val date = dateFormatter.parse(it.getString(seriesDateIndex)) ?: Date()

					series.add(
						LegacySeries(
							id = id,
							date = date,
							numberOfGames = numberOfGames,
							leagueId = leagueId,
						),
					)

					it.moveToNext()
				}
			}
		}

		return series
	}

	private suspend fun migrateSeriesToRoom(series: List<LegacySeries>) {
		val migratedSeries = mutableListOf<SeriesEntity>()
		val idMappings = mutableListOf<LegacyIDMappingEntity>()

		val legacyLeagueIds = series.map(LegacySeries::leagueId).toSet().toList()
		val leagueIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyLeagueIds,
			key = LegacyIDMappingKey.LEAGUE,
		).associateBy({ it.legacyId }, { LeagueID(it.id) })

		for (legacySeries in series) {
			val leagueId = leagueIdMappings[legacySeries.leagueId] ?: continue
			val id = SeriesID.randomID()
			idMappings.add(
				LegacyIDMappingEntity(
					id = id.value,
					legacyId = legacySeries.id,
					key = LegacyIDMappingKey.SERIES,
				),
			)

			migratedSeries.add(
				SeriesEntity(
					id = id,
					date = Instant
						.fromEpochMilliseconds(legacySeries.date.time)
						.toLocalDate(),
					excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
					preBowl = SeriesPreBowl.REGULAR,
					leagueId = leagueId,
					alleyId = null,
				),
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
		val games = mutableListOf<LegacyGame>()

		db.rawQuery(
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
			""".trimIndent(),
			emptyArray(),
		).use {
			if (it.moveToFirst()) {
				while (!it.isAfterLast) {
					val gameIdIndex = it.getColumnIndex(LegacyContract.GameEntry._ID)
					val gameNumberIndex = it.getColumnIndex(LegacyContract.GameEntry.COLUMN_GAME_NUMBER)
					val gameScoreIndex = it.getColumnIndex(LegacyContract.GameEntry.COLUMN_SCORE)
					val gameIsLockedIndex = it.getColumnIndex(LegacyContract.GameEntry.COLUMN_IS_LOCKED)
					val gameIsManualIndex = it.getColumnIndex(LegacyContract.GameEntry.COLUMN_IS_MANUAL)
					val gameMatchPlayIndex = it.getColumnIndex(LegacyContract.GameEntry.COLUMN_MATCH_PLAY)
					val gameSeriesId = it.getColumnIndex(LegacyContract.GameEntry.COLUMN_SERIES_ID)

					val id = it.getLong(gameIdIndex)
					val gameNumber = it.getInt(gameNumberIndex)
					val score = it.getInt(gameScoreIndex)
					val isLocked = it.getInt(gameIsLockedIndex) == 1
					val isManual = it.getInt(gameIsManualIndex) == 1
					val matchPlay = LegacyMatchPlayResult.fromInt(it.getInt(gameMatchPlayIndex))
						?: LegacyMatchPlayResult.NONE
					val seriesId = it.getLong(gameSeriesId)

					games.add(
						LegacyGame(
							id = id,
							gameNumber = gameNumber,
							score = score,
							isLocked = isLocked,
							isManual = isManual,
							matchPlayResult = matchPlay,
							seriesId = seriesId,
						),
					)

					it.moveToNext()
				}
			}
		}

		return games
	}

	private suspend fun migrateGamesToRoom(games: List<LegacyGame>) {
		val migratedGames = mutableListOf<GameEntity>()
		val migratedMatchPlays = mutableListOf<MatchPlayEntity>()

		val gameIdMappings = mutableListOf<LegacyIDMappingEntity>()

		val legacySeriesIds = games.map(LegacyGame::seriesId).toSet().toList()
		val seriesIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacySeriesIds,
			key = LegacyIDMappingKey.SERIES,
		).associateBy({ it.legacyId }, { SeriesID(it.id) })

		for (legacyGame in games) {
			val seriesId = seriesIdMappings[legacyGame.seriesId] ?: continue
			val gameId = GameID.randomID()
			gameIdMappings.add(
				LegacyIDMappingEntity(
					id = gameId.value,
					legacyId = legacyGame.id,
					key = LegacyIDMappingKey.GAME,
				),
			)

			migratedGames.add(
				GameEntity(
					id = gameId,
					index = legacyGame.gameNumber - 1,
					score = legacyGame.score,
					locked = if (legacyGame.isLocked) GameLockState.LOCKED else GameLockState.UNLOCKED,
					scoringMethod = if (legacyGame.isManual) {
						GameScoringMethod.MANUAL
					} else {
						GameScoringMethod.BY_FRAME
					},
					excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
					seriesId = seriesId,
				),
			)

			if (legacyGame.matchPlayResult != LegacyMatchPlayResult.NONE) {
				val matchPlayId = MatchPlayID.randomID()

				migratedMatchPlays.add(
					MatchPlayEntity(
						id = matchPlayId,
						gameId = gameId,
						opponentId = null,
						opponentScore = null,
						result = legacyGame.matchPlayResult.asMatchPlay(),
					),
				)
			}
		}

		legacyIDMappingDao.insertAll(gameIdMappings)
		gameDao.migrateAll(migratedGames)
		matchPlayDao.migrateAll(migratedMatchPlays)
	}

	private suspend fun migrateMatchPlays(db: SQLiteDatabase) {
		val legacyMatchPlays = getLegacyMatchPlays(db)
		migrateMatchPlaysToRoom(legacyMatchPlays)
	}

	private fun getLegacyMatchPlays(db: SQLiteDatabase): List<LegacyMatchPlay> {
		val matchPlays = mutableListOf<LegacyMatchPlay>()

		db.rawQuery(
			"""
			SELECT
				${LegacyContract.MatchPlayEntry._ID},
				${LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_NAME},
				${LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_SCORE},
				${LegacyContract.MatchPlayEntry.COLUMN_GAME_ID}
			FROM ${LegacyContract.MatchPlayEntry.TABLE_NAME}
			""".trimIndent(),
			emptyArray(),
		).use {
			if (it.moveToFirst()) {
				while (!it.isAfterLast) {
					val matchPlayIdIndex = it.getColumnIndex(LegacyContract.MatchPlayEntry._ID)
					val matchPlayOppNameIndex =
						it.getColumnIndex(LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_NAME)
					val matchPlayOppScoreIndex =
						it.getColumnIndex(LegacyContract.MatchPlayEntry.COLUMN_OPPONENT_SCORE)
					val matchPlayGameId = it.getColumnIndex(LegacyContract.MatchPlayEntry.COLUMN_GAME_ID)

					val id = it.getLong(matchPlayIdIndex)
					val opponentName = it.getStringOrNull(matchPlayOppNameIndex)
					val opponentScore = it.getInt(matchPlayOppScoreIndex)
					val gameId = it.getLong(matchPlayGameId)

					matchPlays.add(
						LegacyMatchPlay(
							id = id,
							opponentName = opponentName,
							opponentScore = opponentScore,
							gameId = gameId,
						),
					)

					it.moveToNext()
				}
			}
		}

		return matchPlays
	}

	private suspend fun migrateMatchPlaysToRoom(matchPlays: List<LegacyMatchPlay>) {
		val migratedMatchPlays = mutableListOf<MatchPlayEntity>()
		val migratedOpponents = mutableMapOf<String, BowlerEntity>()
		val matchPlayIdMappings = mutableListOf<LegacyIDMappingEntity>()

		val legacyGameIds = matchPlays.map(LegacyMatchPlay::gameId).toSet().toList()
		val gameIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyGameIds,
			key = LegacyIDMappingKey.GAME,
		).associateBy({ it.legacyId }, { GameID(it.id) })
		val existingMatchPlays = matchPlayDao
			.getMatchPlaysForGames(gameIdMappings.values)
			.associateBy { it.gameId }
		val existingBowlers = bowlerDao
			.getBowlersList(kind = BowlerKind.PLAYABLE, sortOrder = BowlerSortOrder.ALPHABETICAL)
			.first()
			.associateBy { it.name.lowercase() }

		for (legacyMatchPlay in matchPlays) {
			if (legacyMatchPlay.opponentScore == 0 && legacyMatchPlay.opponentName.isNullOrBlank()) {
				continue
			}

			val processedOpponentName = legacyMatchPlay.opponentName?.trim()
			val comparableOpponentName = processedOpponentName?.lowercase()
			val opponentId: BowlerID?

			if (processedOpponentName != null &&
				comparableOpponentName != null &&
				processedOpponentName.isNotBlank()
			) {
				if (existingBowlers.containsKey(comparableOpponentName)) {
					opponentId = existingBowlers[comparableOpponentName]?.id
				} else if (migratedOpponents.containsKey(comparableOpponentName)) {
					opponentId = migratedOpponents[comparableOpponentName]?.id
				} else {
					opponentId = BowlerID.randomID()
					migratedOpponents[comparableOpponentName] = BowlerEntity(
						id = opponentId,
						name = processedOpponentName,
						kind = BowlerKind.OPPONENT,
					)
				}
			} else {
				opponentId = null
			}

			val gameId = gameIdMappings[legacyMatchPlay.gameId] ?: continue
			val existingMatchPlay = existingMatchPlays[gameId]
			val matchPlayId = existingMatchPlay?.id ?: MatchPlayID.randomID()

			matchPlayIdMappings.add(
				LegacyIDMappingEntity(
					id = matchPlayId.value,
					legacyId = legacyMatchPlay.id,
					key = LegacyIDMappingKey.MATCH_PLAY,
				),
			)

			migratedMatchPlays.add(
				MatchPlayEntity(
					id = matchPlayId,
					opponentId = opponentId,
					opponentScore = existingMatchPlay?.opponentScore ?: legacyMatchPlay.opponentScore,
					result = existingMatchPlay?.result,
					gameId = gameId,
				),
			)
		}

		legacyIDMappingDao.insertAll(matchPlayIdMappings)
		bowlerDao.migrateAll(migratedOpponents.values.toList())
		matchPlayDao.migrateAll(migratedMatchPlays)
	}

	private suspend fun migrateFrames(db: SQLiteDatabase) {
		val legacyFrames = getLegacyFrames(db)
		migrateFramesToRoom(legacyFrames)
	}

	private fun getLegacyFrames(db: SQLiteDatabase): List<LegacyFrame> {
		val frames = mutableListOf<LegacyFrame>()

		db.rawQuery(
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
			""".trimIndent(),
			emptyArray(),
		).use {
			if (it.moveToFirst()) {
				while (!it.isAfterLast) {
					val frameIdIndex = it.getColumnIndex(LegacyContract.FrameEntry._ID)
					val frameNumberIndex = it.getColumnIndex(LegacyContract.FrameEntry.COLUMN_FRAME_NUMBER)
					val frameIsAccessedIndex = it.getColumnIndex(LegacyContract.FrameEntry.COLUMN_IS_ACCESSED)
					val framePinState0Index = it.getColumnIndex(LegacyContract.FrameEntry.COLUMN_PIN_STATE[0])
					val framePinState1Index = it.getColumnIndex(LegacyContract.FrameEntry.COLUMN_PIN_STATE[1])
					val framePinState2Index = it.getColumnIndex(LegacyContract.FrameEntry.COLUMN_PIN_STATE[2])
					val frameFoulsIndex = it.getColumnIndex(LegacyContract.FrameEntry.COLUMN_FOULS)
					val frameGameIdIndex = it.getColumnIndex(LegacyContract.FrameEntry.COLUMN_GAME_ID)

					val id = it.getLong(frameIdIndex)
					val frameNumber = it.getInt(frameNumberIndex)
					val isAccessed = it.getInt(frameIsAccessedIndex) == 1
					val pinState0 = it.getInt(framePinState0Index)
					val pinState1 = it.getInt(framePinState1Index)
					val pinState2 = it.getInt(framePinState2Index)
					val fouls = it.getInt(frameFoulsIndex)
					val gameId = it.getLong(frameGameIdIndex)

					frames.add(
						LegacyFrame(
							id = id,
							ordinal = frameNumber,
							isAccessed = isAccessed,
							firstPinState = pinState0,
							secondPinState = pinState1,
							thirdPinState = pinState2,
							fouls = fouls,
							gameId = gameId,
						),
					)

					it.moveToNext()
				}
			}
		}

		return frames
	}

	private suspend fun migrateFramesToRoom(frames: List<LegacyFrame>) {
		val migratedFrames = mutableListOf<FrameEntity>()

		val legacyGameIds = frames.map(LegacyFrame::gameId).toSet().toList()
		val gameIdMappings = legacyIDMappingDao.getLegacyIDMappings(
			legacyIds = legacyGameIds,
			key = LegacyIDMappingKey.GAME,
		).associateBy({ it.legacyId }, { GameID(it.id) })

		for (legacyFrame in frames) {
			val gameId = gameIdMappings[legacyFrame.gameId] ?: continue
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

			migratedFrames.add(
				FrameEntity(
					gameId = gameId,
					index = legacyFrame.ordinal - 1,
					roll0 = FrameEntity.Roll.fromBitString(rolls[0]),
					roll1 = FrameEntity.Roll.fromBitString(rolls[1]),
					roll2 = FrameEntity.Roll.fromBitString(rolls[2]),
					ball0 = null,
					ball1 = null,
					ball2 = null,
				),
			)
		}

		frameDao.migrateAll(migratedFrames)
	}
}
