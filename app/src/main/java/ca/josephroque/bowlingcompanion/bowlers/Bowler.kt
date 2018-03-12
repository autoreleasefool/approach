package ca.josephroque.bowlingcompanion.bowlers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.preference.PreferenceManager
import android.util.Log
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.INameAverage
import ca.josephroque.bowlingcompanion.common.KParcelable
import ca.josephroque.bowlingcompanion.common.parcelableCreator
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
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.Preferences

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single Bowler, who has leagues, events, series, games, and stats.
 */

data class Bowler(
        private val bowlerName: String,
        private val bowlerAverage: Double,
        private val bowlerId: Long
): INameAverage, KParcelable {

    override val name: String
        get() = bowlerName

    override val average: Double
        get() = bowlerAverage

    override val id: Long
        get() = bowlerId

    override var isDeleted: Boolean = false

    /**
     * Construct [Bowler] from a [Parcel]
     */
    private constructor(p: Parcel): this(
            bowlerName = p.readString(),
            bowlerAverage = p.readDouble(),
            bowlerId = p.readLong()
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeDouble(average)
        writeLong(id)
    }

    /**
     * Save the current bowler to the database. Creates a new [Bowler] if id < 0.
     *
     * @param context to get database instance
     * @return [Bowler] if successful and [BCError] if an error occurred
     */
    fun save(context: Context): Deferred<Pair<Bowler?, BCError?>> {
        return if (id < 0) {
            createNewAndSave(context)
        } else {
            update(context)
        }
    }

    /**
     * Create a new [Bowler] and save to the database.
     *
     * @param context to get database instance
     * @return [Bowler] if successful and [BCError] if an error occurred
     */
    private fun createNewAndSave(context: Context): Deferred<Pair<Bowler?, BCError?>> {
        return async(CommonPool) {
            if (!isBowlerNameValid(name)) {
                val error = BCError(
                        context.resources.getString(R.string.error_saving_bowler),
                        context.resources.getString(R.string.error_bowler_name_invalid),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            } else if (!isBowlerNameUnique(context, name).await()) {
                val error = BCError(
                        context.resources.getString(R.string.error_saving_bowler),
                        context.resources.getString(R.string.error_bowler_name_in_use),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            }

            val database = DatabaseHelper.getInstance(context).writableDatabase
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
                        put(LeagueEntry.COLUMN_NUMBER_OF_GAMES, 1)
                    }
                    database.insert(LeagueEntry.TABLE_NAME, null, values)
                }

                database.setTransactionSuccessful()
            } catch (ex: Exception) {
                Log.e(TAG, "Could not create a new bowler")
                val error = BCError(
                        context.resources.getString(R.string.error_saving_bowler),
                        context.resources.getString(R.string.error_bowler_not_saved),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            } finally {
                database.endTransaction()
            }

            Pair(Bowler(name, 0.0, bowlerId), null)
        }
    }

    /**
     * Update the [Bowler] in the database.
     *
     * @param context context to get database instance
     * @return [Bowler] if successful and [BCError] if an error occurred
     */
    private fun update(context: Context): Deferred<Pair<Bowler?, BCError?>> {
        return async(CommonPool) {
            if (!isBowlerNameValid(name)) {
                val error = BCError(
                        context.resources.getString(R.string.error_saving_bowler),
                        context.resources.getString(R.string.error_bowler_name_invalid),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            } else if (!isBowlerNameUnique(context, name, id).await()) {
                val error = BCError(
                        context.resources.getString(R.string.error_saving_bowler),
                        context.resources.getString(R.string.error_bowler_name_in_use),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            }

            val database = DatabaseHelper.getInstance(context).writableDatabase

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
                val error = BCError(
                        context.resources.getString(R.string.error_saving_bowler),
                        context.resources.getString(R.string.error_bowler_not_saved),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            } finally {
                database.endTransaction()
            }

            Pair(this@Bowler, null)
        }
    }

    /** @Override */
    override fun delete(context: Context): Deferred<Unit> {
        return async(CommonPool) {
            if (id < 0) {
                return@async
            }

            val database = DatabaseHelper.getInstance(context).writableDatabase
            val whereArgs = arrayOf(id.toString())
            database.beginTransaction()
            try {
                database.delete(BowlerEntry.TABLE_NAME,
                        BowlerEntry._ID + "=?",
                        whereArgs)
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

    /**
     * Check if this [Bowler] exists in a list
     *
     * @param list the list of bowlers to check
     * @return index of this item in the list if the [id] of this [Bowler] matches the [id] of a
     *         [Bowler] in the list
     */
    fun indexInList(list: List<Bowler>): Int = (0 until list.size).firstOrNull { list[it].id == id } ?: -1

    companion object {

        /** Logging identifier. */
        private const val TAG = "Bowler"

        /** Valid regex for a name. */
        val REGEX_NAME = "^[A-Za-z0-9]+[ A-Za-z0-9'!@#$%^&*()_+:\"?/~-]*[A-Za-z0-9'!@#$%^&*()_+:\"?/~-]*$".toRegex()

        /** Creator, required by [Parcelable]. */
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
        private fun isBowlerNameUnique(context: Context, name: String, id: Long = -1): Deferred<Boolean> {
            return async(CommonPool) {
                val database = DatabaseHelper.getInstance(context).readableDatabase

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
         * Get all of the bowlers available in the app.
         *
         * @param context to get database instance
         * @return a [MutableList] of [Bowler] instances from the database.
         */
        fun fetchAll(context: Context): Deferred<MutableList<Bowler>> {
            return async(CommonPool) {
                val bowlers: MutableList<Bowler> = ArrayList()

                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                val includeEvents = preferences.getBoolean(Settings.INCLUDE_EVENTS, true)
                val includeOpen = preferences.getBoolean(Settings.INCLUDE_OPEN, true)
                val sortBy = Sort.fromInt(preferences.getInt(Preferences.BOWLER_SORT_ORDER, Sort.Alphabetically.ordinal))

                val database = DatabaseHelper.getInstance(context).readableDatabase

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
                        + " GROUP BY bowler." + BowlerEntry._ID
                        + orderQueryBy)

                val rawBowlerArgs = arrayOf(
                        0.toString(),
                        0.toString(),
                        if (!includeOpen) League.PRACTICE_LEAGUE_NAME else 0.toString(),
                        0.toString())

                // Adds loaded bowler names and averages to lists to display
                val cursor = database.rawQuery(rawBowlerQuery, rawBowlerArgs)
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast) {
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
                                cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME)),
                                bowlerAverage,
                                cursor.getLong(cursor.getColumnIndex("bid")))
                        bowlers.add(bowler)
                        cursor.moveToNext()
                    }
                }
                cursor.close()

                bowlers
            }
        }
    }
}
