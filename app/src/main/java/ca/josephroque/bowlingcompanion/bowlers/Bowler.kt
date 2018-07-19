package ca.josephroque.bowlingcompanion.bowlers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.preference.PreferenceManager
import android.util.Log
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.INameAverage
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.database.Contract.*
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.settings.Settings
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.text.SimpleDateFormat
import java.util.*
import ca.josephroque.bowlingcompanion.database.Contract.BowlerEntry
import ca.josephroque.bowlingcompanion.database.Saviour
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.Preferences

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single Bowler, who has leagues, events, series, games, and stats.
 */
class Bowler(
        override val id: Long,
        override val name: String,
        override val average: Double
) : INameAverage, KParcelable {

    /** Private field to indicate if the item is deleted. */
    private var _isDeleted: Boolean = false
    /** @Override */
    override val isDeleted: Boolean
        get() = _isDeleted

    /**
     * Construct [Bowler] from a [Parcel]
     */
    private constructor(p: Parcel): this(
            id = p.readLong(),
            name = p.readString(),
            average = p.readDouble()
    )

    /**
     * Construct [Bowler] from a [Bowler].
     */
    constructor(bowler: Bowler): this(
            id = bowler.id,
            name = bowler.name,
            average = bowler.average
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(name)
        writeDouble(average)
    }

    /** @Override */
    override fun markForDeletion(): Bowler {
        val newInstance = Bowler(this)
        newInstance._isDeleted = true
        return newInstance
    }

    /** @Override */
    override fun cleanDeletion(): Bowler {
        val newInstance = Bowler(this)
        newInstance._isDeleted = false
        return newInstance
    }

    /**
     * Get all [League] instances belonging to this [Bowler] in the database.
     *
     * @param context to access database
     * @return a [MutableList] of [League] instances
     */
    fun fetchLeagues(context: Context): Deferred<MutableList<League>> {
        return League.fetchAll(context, this)
    }

    /**
     * Get all [League] instances belonging to this [Bowler] in the database which are events.
     *
     * @param context to access database
     * @return a [MutableList] of [League] instances
     */
    fun fetchEvents(context: Context): Deferred<MutableList<League>> {
        return League.fetchAll(context, this, false, true)
    }

    /**
     * Get all [League] instances belonging to this [Bowler] in the database.
     *
     * @param context to access database
     * @return a [MutableList] of [League] instances
     */
    fun fetchLeaguesAndEvents(context: Context): Deferred<MutableList<League>> {
        return League.fetchAll(context, this, true, true)
    }

    /** @Override */
    override fun delete(context: Context): Deferred<Unit> {
        return async(CommonPool) {
            if (id < 0) {
                return@async
            }

            val database = Saviour.instance.getWritableDatabase(context).await()
            database.beginTransaction()
            try {
                database.delete(BowlerEntry.TABLE_NAME,
                        BowlerEntry._ID + "=?",
                        arrayOf(id.toString()))
                database.setTransactionSuccessful()
            } catch (e: Exception) {
                // Does nothing
                // If there's an error deleting this bowler, then they just remain in the
                // user's data and no harm is done.
            } finally {
                database.endTransaction()
            }
        }
    }

    companion object {

        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "Bowler"

        /** Valid regex for a name. */
        val REGEX_NAME = "^[A-Za-z0-9]+[ A-Za-z0-9'!@#$%^&*()_+:\"?/~-]*[A-Za-z0-9'!@#$%^&*()_+:\"?/~-]*$".toRegex()

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::Bowler)

        /**
         * Order by which to sort bowlers.
         */
        enum class Sort {
            Alphabetically,
            LastModified;

            companion object {
                private val map = Sort.values().associateBy(Sort::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }

        /**
         * Check if a name is a valid [Bowler] name.
         *
         * @param name name to check
         * @return true if the name is valid, false otherwise
         */
        private fun isBowlerNameValid(name: String): Boolean = REGEX_NAME.matches(name)

        /**
         * Check if a name is unique in the Bowler database.
         *
         * @param context to get database instance
         * @param name name to check
         * @param id id of the existing bowler, so if the name is unchanged it can be saved
         * @return true if the name is not already in the database, false otherwise
         */
        private fun isBowlerNameUnique(
                context: Context,
                name: String,
                id: Long = -1
        ): Deferred<Boolean> {
            return async(CommonPool) {
                val database = Saviour.instance.getReadableDatabase(context).await()

                var cursor: Cursor? = null
                try {
                    cursor = database.query(
                            BowlerEntry.TABLE_NAME,
                            arrayOf(BowlerEntry.COLUMN_BOWLER_NAME),
                            "${BowlerEntry.COLUMN_BOWLER_NAME}=? AND ${BowlerEntry._ID}!=?",
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

        /**
         * Ensure all preconditions to save are met.
         *
         * @param context to get error strings
         * @param id id for the bowler
         * @param name name for the bowler
         * @return [BCError] if any conditions are not met, or null if the bowler can be saved
         */
        private fun validateSavePreconditions(
                context: Context,
                id: Long,
                name: String
        ): Deferred<BCError?> {
            return async(CommonPool) {
                val errorTitle = R.string.issue_saving_bowler
                val errorMessage: Int? = if (!isBowlerNameValid(name)) {
                    R.string.error_bowler_name_invalid
                } else if (!isBowlerNameUnique(context, name, id).await()) {
                    R.string.error_bowler_name_in_use
                } else {
                    null
                }

                return@async if (errorMessage != null) {
                    BCError(errorTitle, errorMessage, BCError.Severity.Warning)
                } else {
                    null
                }
            }
        }

        /**
         * Save the bowler to the database.
         *
         * @param context to get database instance
         * @param id id of the bowler to save, or -1 to create a new entry
         * @param name name of the bowler to save
         * @param average average of the bowler to apply to the new [Bowler]
         * @return the saved [Bowler] or a [BCError] if any errors occurred
         */
        fun save(
                context: Context,
                id: Long,
                name: String,
                average: Double = 0.0
        ): Deferred<Pair<Bowler?, BCError?>> {
            return if (id < 0) {
                createNewAndSave(context, name)
            } else {
                update(context, id, name, average)
            }
        }

        /**
         * Create a new [BowlerEntry] in the database.
         *
         * @param context to get database instance
         * @param name name of the bowler to create
         * @return the saved [Bowler] or a [BCError] if any errors occurred
         */
        private fun createNewAndSave(context: Context,
                                     name: String): Deferred<Pair<Bowler?, BCError?>> {
            return async(CommonPool) {
                val error = validateSavePreconditions(context, -1, name).await()
                if (error != null) {
                    return@async Pair(null, error)
                }

                val database = Saviour.instance.getWritableDatabase(context).await()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
                val currentDate = dateFormat.format(Date())

                var values = ContentValues().apply {
                    put(BowlerEntry.COLUMN_BOWLER_NAME, name)
                    put(BowlerEntry.COLUMN_DATE_MODIFIED, currentDate)
                }

                val bowlerId: Long

                database.beginTransaction()
                try {
                    bowlerId = database.insert(BowlerEntry.TABLE_NAME, null, values)

                    if (bowlerId != -1L) {
                        values = ContentValues().apply {
                            put(LeagueEntry.COLUMN_LEAGUE_NAME, League.PRACTICE_LEAGUE_NAME)
                            put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate)
                            put(LeagueEntry.COLUMN_BOWLER_ID, bowlerId)
                            put(LeagueEntry.COLUMN_NUMBER_OF_GAMES, League.DEFAULT_NUMBER_OF_GAMES)
                        }
                        database.insert(LeagueEntry.TABLE_NAME, null, values)
                    }

                    database.setTransactionSuccessful()
                } catch (ex: Exception) {
                    Log.e(TAG, "Could not create a new bowler")
                    return@async Pair(
                            null,
                            BCError(R.string.error_saving_bowler, R.string.error_bowler_not_saved)
                    )
                } finally {
                    database.endTransaction()
                }

                Pair(Bowler(bowlerId, name, 0.0), null)
            }
        }

        /**
         * Update the [BowlerEntry] in the database.
         *
         * @param context context to get database instance
         * @param id id of the entry to update
         * @param name name for the bowler
         * @param average average of the bowler
         * @return the saved [Bowler] or a [BCError] if any errors occurred
         */
        private fun update(
                context: Context,
                id: Long,
                name: String,
                average: Double
        ): Deferred<Pair<Bowler?, BCError?>> {
            return async(CommonPool) {
                val error = validateSavePreconditions(context, id, name).await()
                if (error != null) {
                    return@async Pair(null, error)
                }

                val database = Saviour.instance.getWritableDatabase(context).await()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
                val currentDate = dateFormat.format(Date())

                val values = ContentValues().apply {
                    put(BowlerEntry.COLUMN_BOWLER_NAME, name)
                    put(BowlerEntry.COLUMN_DATE_MODIFIED, currentDate)
                }

                database.beginTransaction()
                try {
                    database.update(
                            BowlerEntry.TABLE_NAME,
                            values,
                            "${BowlerEntry._ID}=?",
                            arrayOf(id.toString()))

                    database.setTransactionSuccessful()
                } catch (ex: Exception) {
                    Log.e(TAG, "Could not update bowler")
                    return@async Pair(
                            null,
                            BCError(R.string.error_saving_bowler, R.string.error_bowler_not_saved)
                    )
                } finally {
                    database.endTransaction()
                }

                Pair(Bowler(id, name, average), null)
            }
        }

        /**
         * Fetch a single bowler from the app by their ID.
         *
         * @return a [Bowler] if the ID was valid, null otherwise
         */
        fun fetch(context: Context, id: Long): Deferred<Bowler?> {
            return async(CommonPool) {
                val bowlerList = fetchAll(context, id).await()
                if (bowlerList.size == 1) {
                    bowlerList[0]
                } else {
                    null
                }
            }
        }

        /**
         * Get all of the bowlers available in the app.
         *
         * @param context to get database instance
         * @param filterId filter to a single bowler ID
         * @return a [MutableList] of [Bowler] instances from the database.
         */
        fun fetchAll(context: Context, filterId: Long = -1): Deferred<MutableList<Bowler>> {
            return async(CommonPool) {
                val bowlers: MutableList<Bowler> = ArrayList()
                val database = Saviour.instance.getReadableDatabase(context).await()

                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                val includeEvents = preferences.getBoolean(Settings.INCLUDE_EVENTS, true)
                val includeOpen = preferences.getBoolean(Settings.INCLUDE_OPEN, true)
                val sortBy = Sort.fromInt(preferences.getInt(Preferences.BOWLER_SORT_ORDER, Sort.Alphabetically.ordinal))

                val gameSumAndCountQuery = ("SELECT "
                        + "league2." + LeagueEntry._ID + " AS lid2, "
                        + "SUM(game2." + GameEntry.COLUMN_SCORE + ") AS gameSum, "
                        + "COUNT(game2." + GameEntry._ID + ") AS gameCount"
                        + " FROM " + LeagueEntry.TABLE_NAME + " AS league2"
                        + " INNER JOIN " + SeriesEntry.TABLE_NAME + " AS series2"
                        + " ON lid2=" + SeriesEntry.COLUMN_LEAGUE_ID
                        + " INNER JOIN " + GameEntry.TABLE_NAME + " AS game2"
                        + " ON series2." + SeriesEntry._ID + "=" + GameEntry.COLUMN_SERIES_ID
                        + " WHERE "
                        + " game2." + GameEntry.COLUMN_SCORE + ">?"
                        + " AND "
                        + (if (!includeEvents) LeagueEntry.COLUMN_IS_EVENT else "'0'") + "=?"
                        + " AND "
                        + (if (!includeOpen) LeagueEntry.COLUMN_LEAGUE_NAME + "!" else "'0'") + "=?"
                        + " GROUP BY league2." + LeagueEntry._ID)

                val baseAverageAndGamesQuery = ("SELECT "
                        + "league3." + LeagueEntry._ID + " AS lid3, "
                        + LeagueEntry.COLUMN_BASE_AVERAGE + " * " + LeagueEntry.COLUMN_BASE_GAMES + " AS baseSum, "
                        + LeagueEntry.COLUMN_BASE_GAMES + " AS baseGames"
                        + " FROM " + LeagueEntry.TABLE_NAME + " AS league3"
                        + " WHERE "
                        + " league3." + LeagueEntry.COLUMN_BASE_AVERAGE + ">?")

                val orderQueryBy = if (sortBy == Sort.Alphabetically) {
                    " ORDER BY bowler." + BowlerEntry.COLUMN_BOWLER_NAME
                } else {
                    " ORDER BY bowler." + BowlerEntry.COLUMN_DATE_MODIFIED + " DESC"
                }

                // Query to retrieve bowler names and averages from database
                val rawBowlerQuery = ("SELECT "
                        + "bowler." + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                        + "bowler." + BowlerEntry._ID + " AS bid, "
                        + "SUM(t.gameSum) AS totalSum, "
                        + "SUM(t.gameCount) AS totalCount, "
                        + "SUM(u.baseSum) AS totalBaseSum, "
                        + "SUM(u.baseGames) AS totalBaseGames"
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " LEFT JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bowler." + BowlerEntry._ID + "=" + LeagueEntry.COLUMN_BOWLER_ID
                        + " LEFT JOIN (" + gameSumAndCountQuery + ") AS t"
                        + " ON t.lid2=league." + LeagueEntry._ID
                        + " LEFT JOIN (" + baseAverageAndGamesQuery + ") AS u"
                        + " ON u.lid3=league." + LeagueEntry._ID
                        + " WHERE "
                        + (if (filterId != -1L) "bid" else "'0'") + "=?"
                        + " GROUP BY bowler." + BowlerEntry._ID
                        + orderQueryBy)

                val rawBowlerArgs = arrayOf(
                        /* game2.SCORE > */ 0.toString(),
                        /* COLUMN_IS_EVENT || 0 = */ 0.toString(),
                        /* COLUMN_LEAGUE_NAME || 0 = */ if (!includeOpen) League.PRACTICE_LEAGUE_NAME else 0.toString(),
                        /* COLUMN_BASE_AVERAGE > */ 0.toString(),
                        /* bid || 0 */ if (filterId != -1L) filterId.toString() else 0.toString())

                // Adds loaded bowler names and averages to lists to display
                var cursor: Cursor? = null
                try {
                    cursor = database.rawQuery(rawBowlerQuery, rawBowlerArgs)
                    while (cursor.moveToNext()) {
                        val totalSum = cursor.getInt(cursor.getColumnIndex("totalSum"))
                        val totalCount = cursor.getInt(cursor.getColumnIndex("totalCount"))
                        val totalBaseSum = cursor.getInt(cursor.getColumnIndex("totalBaseSum"))
                        val totalBaseGames = cursor.getInt(cursor.getColumnIndex("totalBaseGames"))
                        val bowlerAverage: Double =
                                if (totalCount + totalBaseGames > 0)
                                    (totalSum + totalBaseSum) / (totalCount + totalBaseGames).toDouble()
                                else
                                    0.0

                        val bowler = Bowler(
                                cursor.getLong(cursor.getColumnIndex("bid")),
                                cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME)),
                                bowlerAverage)
                        bowlers.add(bowler)
                    }
                } finally {
                    cursor?.close()
                }

                bowlers
            }
        }
    }
}
