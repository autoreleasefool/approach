package ca.josephroque.bowlingcompanion.leagues

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Parcel
import android.support.v7.app.AlertDialog
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.widget.NumberPicker
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.interfaces.INameAverage
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.common.interfaces.readBoolean
import ca.josephroque.bowlingcompanion.common.interfaces.writeBoolean
import ca.josephroque.bowlingcompanion.database.Annihilator
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry
import ca.josephroque.bowlingcompanion.database.DatabaseManager
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.scoring.Average
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.Preferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single League, which has a set of series.
 */
data class League(
    val bowler: Bowler,
    override val id: Long,
    override val name: String,
    override val average: Double,
    val isEvent: Boolean,
    val gamesPerSeries: Int,
    val additionalPinfall: Int,
    val additionalGames: Int,
    val gameHighlight: Int,
    val seriesHighlight: Int
) : INameAverage, KParcelable {

    private var _isDeleted: Boolean = false
    override val isDeleted: Boolean
        get() = _isDeleted

    val isPractice: Boolean
        get() = name == PRACTICE_LEAGUE_NAME

    // MARK: Constructors

    private constructor(p: Parcel): this(
            bowler = p.readParcelable<Bowler>(Bowler::class.java.classLoader)!!,
            id = p.readLong(),
            name = p.readString()!!,
            average = p.readDouble(),
            isEvent = p.readBoolean(),
            gamesPerSeries = p.readInt(),
            additionalPinfall = p.readInt(),
            additionalGames = p.readInt(),
            gameHighlight = p.readInt(),
            seriesHighlight = p.readInt()
    )

    constructor(league: League): this(
            bowler = league.bowler,
            id = league.id,
            name = league.name,
            average = league.average,
            isEvent = league.isEvent,
            gamesPerSeries = league.gamesPerSeries,
            additionalPinfall = league.additionalPinfall,
            additionalGames = league.additionalGames,
            gameHighlight = league.gameHighlight,
            seriesHighlight = league.seriesHighlight
    )

    // MARK: Parcelable

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(bowler, 0)
        writeLong(id)
        writeString(name)
        writeDouble(average)
        writeBoolean(isEvent)
        writeInt(gamesPerSeries)
        writeInt(additionalPinfall)
        writeInt(additionalGames)
        writeInt(gameHighlight)
        writeInt(seriesHighlight)
    }

    // MARK: League

    fun fetchSeries(context: Context): Deferred<MutableList<Series>> {
        return Series.fetchAll(context, this)
    }

    fun createNewSeries(context: Context, openDatabase: SQLiteDatabase? = null, numberOfPracticeGamesOverride: Int? = null): Deferred<Pair<Series?, BCError?>> {
        val numberOfGames = if (isPractice) {
            numberOfPracticeGamesOverride ?: gamesPerSeries
        } else {
            gamesPerSeries
        }

        return async(CommonPool) {
            return@async Series.save(
                    context = context,
                    league = this@League,
                    id = -1,
                    date = Date(),
                    numberOfGames = numberOfGames,
                    scores = IntArray(numberOfGames).toList(),
                    matchPlay = ByteArray(numberOfGames).toList(),
                    openDatabase = openDatabase
            ).await()
        }
    }

    // MARK: IDeletable

    override fun markForDeletion(): League {
        val newInstance = League(this)
        newInstance._isDeleted = true
        return newInstance
    }

    override fun cleanDeletion(): League {
        val newInstance = League(this)
        newInstance._isDeleted = false
        return newInstance
    }

    override fun delete(context: Context): Deferred<Unit> {
        return async(CommonPool) {
            if (id < 0) {
                return@async
            }

            Annihilator.instance.delete(
                    weakContext = WeakReference(context),
                    tableName = LeagueEntry.TABLE_NAME,
                    whereClause = "${LeagueEntry._ID}=?",
                    whereArgs = arrayOf(id.toString())
            )
        }
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "League"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::League)

        private val REGEX_NAME = Bowler.REGEX_NAME

        @Deprecated("Replaced with PRACTICE_LEAGUE_NAME")
        const val OPEN_LEAGUE_NAME = "Open"
        const val PRACTICE_LEAGUE_NAME = "Practice"

        const val MAX_NUMBER_OF_GAMES = 20
        const val MIN_NUMBER_OF_GAMES = 1

        const val DEFAULT_NUMBER_OF_GAMES = 1
        const val DEFAULT_GAME_HIGHLIGHT = 300
        val DEFAULT_SERIES_HIGHLIGHT = intArrayOf(250, 500, 750, 1000, 1250, 1500, 1750, 2000, 2250, 2500, 2750, 3000, 3250, 3500, 3750, 4000, 4250, 4500, 4750, 5000)

        enum class Sort {
            Alphabetically,
            LastModified;

            companion object {
                private val map = Sort.values().associateBy(Sort::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }

        fun showPracticeGamesPicker(context: Context, completionHandler: (Int) -> Unit) {
            val numberPicker = NumberPicker(context)
            numberPicker.maxValue = League.MAX_NUMBER_OF_GAMES
            numberPicker.minValue = 1
            numberPicker.wrapSelectorWheel = false

            val listener = DialogInterface.OnClickListener { dialog, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    completionHandler(numberPicker.value)
                }
                dialog.dismiss()
            }

            AlertDialog.Builder(context)
                    .setTitle(R.string.how_many_practice_games)
                    .setView(numberPicker)
                    .setPositiveButton(R.string.bowl, listener)
                    .setNegativeButton(R.string.cancel, listener)
                    .create()
                    .show()
        }

        private fun isLeagueNameValid(name: String): Boolean = REGEX_NAME.matches(name)

        private fun isLeagueNameUnique(context: Context, name: String, id: Long = -1): Deferred<Boolean> {
            return async(CommonPool) {
                val database = DatabaseManager.getReadableDatabase(context).await()

                var cursor: Cursor? = null
                try {
                    cursor = database.query(
                            LeagueEntry.TABLE_NAME,
                            arrayOf(LeagueEntry.COLUMN_LEAGUE_NAME),
                            "${LeagueEntry.COLUMN_LEAGUE_NAME}=? AND ${LeagueEntry._ID}!=?",
                            arrayOf(name, id.toString()),
                            "",
                            "",
                            ""
                    )

                    if ((cursor?.count ?: 0) > 0) {
                        return@async false
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed) {
                        cursor.close()
                    }
                }

                true
            }
        }

        private fun validateSavePreconditions(
            context: Context,
            id: Long,
            name: String,
            isEvent: Boolean,
            gamesPerSeries: Int,
            additionalPinfall: Int,
            additionalGames: Int,
            gameHighlight: Int,
            seriesHighlight: Int
        ): Deferred<BCError?> {
            return async(CommonPool) {
                val errorTitle = if (isEvent) R.string.issue_saving_event else R.string.issue_saving_league
                val errorMessage: Int?
                if (!isLeagueNameValid(name)) {
                    errorMessage = if (isEvent) R.string.error_event_name_invalid else R.string.error_league_name_invalid
                } else if (!isLeagueNameUnique(context, name, id).await()) {
                    errorMessage = if (isEvent) R.string.error_event_name_in_use else R.string.error_league_name_in_use
                } else if (name == PRACTICE_LEAGUE_NAME) {
                    errorMessage = R.string.error_cannot_edit_practice_league
                } else if (
                        (isEvent && (additionalPinfall != 0 || additionalGames != 0)) ||
                        (additionalPinfall < 0 || additionalGames < 0) ||
                        (additionalPinfall > 0 && additionalGames == 0) ||
                        (additionalPinfall.toDouble() / additionalGames.toDouble() > 450)
                ) {
                    errorMessage = R.string.error_league_additional_info_unbalanced
                } else if (
                        gameHighlight < 0 || gameHighlight > Game.MAX_SCORE ||
                        seriesHighlight < 0 || seriesHighlight > Game.MAX_SCORE * gamesPerSeries
                ) {
                    errorMessage = R.string.error_league_highlight_invalid
                } else if (gamesPerSeries < MIN_NUMBER_OF_GAMES || gamesPerSeries > MAX_NUMBER_OF_GAMES) {
                    errorMessage = R.string.error_league_number_of_games_invalid
                } else {
                    errorMessage = null
                }

                return@async if (errorMessage != null) {
                    BCError(errorTitle, errorMessage, BCError.Severity.Warning)
                } else {
                    null
                }
            }
        }

        fun save(
            context: Context,
            bowler: Bowler,
            id: Long,
            name: String,
            isEvent: Boolean,
            gamesPerSeries: Int,
            additionalPinfall: Int,
            additionalGames: Int,
            gameHighlight: Int,
            seriesHighlight: Int,
            average: Double = 0.0
        ): Deferred<Pair<League?, BCError?>> {
            return if (id < 0) {
                createNewAndSave(context, bowler, name, isEvent, gamesPerSeries, additionalPinfall, additionalGames, gameHighlight, seriesHighlight)
            } else {
                update(context, bowler, id, name, average, isEvent, gamesPerSeries, additionalPinfall, additionalGames, gameHighlight, seriesHighlight)
            }
        }

        private fun createNewAndSave(
            context: Context,
            bowler: Bowler,
            name: String,
            isEvent: Boolean,
            gamesPerSeries: Int,
            additionalPinfall: Int,
            additionalGames: Int,
            gameHighlight: Int,
            seriesHighlight: Int
        ): Deferred<Pair<League?, BCError?>> {
            return async(CommonPool) {
                val error = validateSavePreconditions(
                        context = context,
                        id = -1,
                        name = name,
                        isEvent = isEvent,
                        gamesPerSeries = gamesPerSeries,
                        additionalPinfall = additionalPinfall,
                        additionalGames = additionalGames,
                        gameHighlight = gameHighlight,
                        seriesHighlight = seriesHighlight

                ).await()
                if (error != null) {
                    return@async Pair(null, error)
                }

                val database = DatabaseManager.getWritableDatabase(context).await()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
                val currentDate = dateFormat.format(Date())

                val values = ContentValues().apply {
                    put(LeagueEntry.COLUMN_LEAGUE_NAME, name)
                    put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate)
                    put(LeagueEntry.COLUMN_BOWLER_ID, bowler.id)
                    put(LeagueEntry.COLUMN_ADDITIONAL_PINFALL, additionalPinfall)
                    put(LeagueEntry.COLUMN_ADDITIONAL_GAMES, additionalGames)
                    put(LeagueEntry.COLUMN_NUMBER_OF_GAMES, gamesPerSeries)
                    put(LeagueEntry.COLUMN_IS_EVENT, isEvent)
                    put(LeagueEntry.COLUMN_GAME_HIGHLIGHT, gameHighlight)
                    put(LeagueEntry.COLUMN_SERIES_HIGHLIGHT, seriesHighlight)
                }

                val league: League
                database.beginTransaction()
                try {
                    val leagueId = database.insert(LeagueEntry.TABLE_NAME, null, values)
                    league = League(
                            bowler = bowler,
                            id = leagueId,
                            name = name,
                            average = 0.0,
                            isEvent = isEvent,
                            gamesPerSeries = gamesPerSeries,
                            additionalPinfall = additionalPinfall,
                            additionalGames = additionalGames,
                            gameHighlight = gameHighlight,
                            seriesHighlight = seriesHighlight
                    )

                    if (league.id != -1L && isEvent) {
                        /*
                         * If the new entry is an event, its series is also created at this time
                         * since there is only a single series to an event
                         */
                        val (series, seriesError) = league.createNewSeries(context).await()
                        if (seriesError != null || (series?.id ?: -1L) == -1L) {
                            throw IllegalStateException("Series was not saved.")
                        }
                    }

                    database.setTransactionSuccessful()
                } catch (ex: Exception) {
                    Log.e(TAG, "Could not create a new league")
                    return@async Pair(
                            null,
                            BCError(R.string.error_saving_league, R.string.error_league_not_saved)
                    )
                } finally {
                    database.endTransaction()
                }

                Pair(league, null)
            }
        }

        fun update(
            context: Context,
            bowler: Bowler,
            id: Long,
            name: String,
            average: Double,
            isEvent: Boolean,
            gamesPerSeries: Int,
            additionalPinfall: Int,
            additionalGames: Int,
            gameHighlight: Int,
            seriesHighlight: Int
        ): Deferred<Pair<League?, BCError?>> {
            return async(CommonPool) {
                val error = validateSavePreconditions(
                        context = context,
                        id = id,
                        name = name,
                        isEvent = isEvent,
                        gamesPerSeries = gamesPerSeries,
                        additionalPinfall = additionalPinfall,
                        additionalGames = additionalGames,
                        gameHighlight = gameHighlight,
                        seriesHighlight = seriesHighlight

                ).await()
                if (error != null) {
                    return@async Pair(null, error)
                }

                val database = DatabaseManager.getWritableDatabase(context).await()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
                val currentDate = dateFormat.format(Date())

                val values = ContentValues().apply {
                    put(LeagueEntry.COLUMN_LEAGUE_NAME, name)
                    put(LeagueEntry.COLUMN_ADDITIONAL_PINFALL, additionalPinfall)
                    put(LeagueEntry.COLUMN_ADDITIONAL_GAMES, additionalGames)
                    put(LeagueEntry.COLUMN_GAME_HIGHLIGHT, gameHighlight)
                    put(LeagueEntry.COLUMN_SERIES_HIGHLIGHT, seriesHighlight)
                    put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate)
                }

                database.beginTransaction()
                try {
                    database.update(LeagueEntry.TABLE_NAME, values, LeagueEntry._ID + "=?", arrayOf(id.toString()))
                    database.setTransactionSuccessful()
                } catch (ex: Exception) {
                    Log.e(TAG, "Error updating league/event details ($name, $additionalPinfall, $additionalGames)", ex)
                } finally {
                    database.endTransaction()
                }

                Pair(League(
                        bowler = bowler,
                        id = id,
                        name = name,
                        average = average,
                        isEvent = isEvent,
                        gamesPerSeries = gamesPerSeries,
                        additionalPinfall = additionalPinfall,
                        additionalGames = additionalGames,
                        gameHighlight = gameHighlight,
                        seriesHighlight = seriesHighlight
                ), null)
            }
        }

        fun fetchAll(
            context: Context,
            bowler: Bowler,
            includeLeagues: Boolean = true,
            includeEvents: Boolean = false
        ): Deferred<MutableList<League>> {
            return async(CommonPool) {
                val leagues: MutableList<League> = ArrayList()
                val database = DatabaseManager.getReadableDatabase(context).await()

                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                val sortBy = Sort.fromInt(preferences.getInt(Preferences.LEAGUE_SORT_ORDER, Sort.Alphabetically.ordinal))

                val orderQueryBy = if (sortBy == Sort.Alphabetically) {
                    "ORDER BY league.${LeagueEntry.COLUMN_LEAGUE_NAME} "
                } else {
                    "ORDER BY league.${LeagueEntry.COLUMN_DATE_MODIFIED} DESC "
                }

                val rawLeagueEventQuery = ("SELECT " +
                        "league.${LeagueEntry._ID} AS lid, " +
                        "${LeagueEntry.COLUMN_LEAGUE_NAME}, " +
                        "${LeagueEntry.COLUMN_IS_EVENT}, " +
                        "${LeagueEntry.COLUMN_ADDITIONAL_PINFALL}, " +
                        "${LeagueEntry.COLUMN_ADDITIONAL_GAMES}, " +
                        "${LeagueEntry.COLUMN_GAME_HIGHLIGHT}, " +
                        "${LeagueEntry.COLUMN_SERIES_HIGHLIGHT}, " +
                        "${LeagueEntry.COLUMN_NUMBER_OF_GAMES}, " +
                        "${GameEntry.COLUMN_SCORE} " +
                        "FROM ${LeagueEntry.TABLE_NAME} AS league " +
                        "LEFT JOIN ${SeriesEntry.TABLE_NAME} AS series " +
                        "ON league.${LeagueEntry._ID}=series.${SeriesEntry.COLUMN_LEAGUE_ID} " +
                        "LEFT JOIN ${GameEntry.TABLE_NAME} AS game " +
                        "ON series.${SeriesEntry._ID}=game.${GameEntry.COLUMN_SERIES_ID} " +
                        "WHERE ${LeagueEntry.COLUMN_BOWLER_ID}=? " +
                        orderQueryBy)

                val cursor = database.rawQuery(rawLeagueEventQuery, arrayOf(bowler.id.toString()))
                var lastId: Long = -1
                var leagueNumberOfGames = 0
                var leagueTotal = 0

                fun isCurrentLeagueEvent(cursor: Cursor): Boolean {
                    return cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_IS_EVENT)) == 1
                }

                fun buildLeagueFromCursor(cursor: Cursor): League {
                    val id = cursor.getLong(cursor.getColumnIndex("lid"))
                    val name = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME))
                    val additionalPinfall = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_ADDITIONAL_PINFALL))
                    val additionalGames = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_ADDITIONAL_GAMES))
                    val gameHighlight = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_GAME_HIGHLIGHT))
                    val seriesHighlight = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_SERIES_HIGHLIGHT))
                    val gamesPerSeries = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES))
                    val average = Average.getAdjustedAverage(leagueTotal, leagueNumberOfGames, additionalPinfall, additionalGames)
                    val isEvent = isCurrentLeagueEvent(cursor)

                    return League(
                            bowler,
                            id,
                            name,
                            average,
                            isEvent,
                            gamesPerSeries,
                            additionalPinfall,
                            additionalGames,
                            gameHighlight,
                            seriesHighlight
                    )
                }

                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast) {
                        val newId = cursor.getLong(cursor.getColumnIndex("lid"))
                        if (newId != lastId && lastId != -1L) {
                            cursor.moveToPrevious()

                            val isEvent = isCurrentLeagueEvent(cursor)
                            if ((includeEvents && isEvent) || (includeLeagues && !isEvent)) {
                                leagues.add(buildLeagueFromCursor(cursor))
                            }

                            leagueTotal = 0
                            leagueNumberOfGames = 0

                            cursor.moveToNext()
                        }
                        val score = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE))
                        if (score > 0) {
                            leagueTotal += score.toInt()
                            leagueNumberOfGames++
                        }

                        lastId = newId
                        cursor.moveToNext()
                    }
                    cursor.moveToPrevious()

                    val isEvent = isCurrentLeagueEvent(cursor)
                    if ((includeEvents && isEvent) || (includeLeagues && !isEvent)) {
                        leagues.add(buildLeagueFromCursor(cursor))
                    }
                }

                cursor.close()

                leagues
            }
        }
    }
}
