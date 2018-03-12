package ca.josephroque.bowlingcompanion.teams

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.preference.PreferenceManager
import android.util.Log
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.IDeletable
import ca.josephroque.bowlingcompanion.common.KParcelable
import ca.josephroque.bowlingcompanion.common.parcelableCreator
import ca.josephroque.bowlingcompanion.database.Contract.BowlerEntry
import ca.josephroque.bowlingcompanion.database.Contract.TeamBowlerEntry
import ca.josephroque.bowlingcompanion.database.Contract.TeamEntry
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.Preferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single Team, which has a set of bowlers.
 */
data class Team(
        private val teamName: String,
        private val teamId: Long,
        private val teamMembers: List<Pair<String, Long>>
): KParcelable, IDeletable {

    val name: String
        get() = teamName

    val id: Long
        get() = teamId

    override var isDeleted: Boolean = false

    /* Names and IDs of members of the team. */
    val members = teamMembers

    /**
     * Construct [Team] from a [Parcel]
     */
    private constructor(p: Parcel): this(
            teamName = p.readString(),
            teamId = p.readLong(),
            teamMembers = arrayListOf<Pair<String, Long>>().apply {
                val names: MutableList<String> = ArrayList()
                p.readStringList(names)

                val memberIdArr = LongArray(size)
                p.readLongArray(memberIdArr)
                val ids: MutableList<Long> = ArrayList()
                memberIdArr.toCollection(ids)

                names.forEachIndexed({ index, name ->
                    this.add(Pair(name, ids[index]))
                })
            }
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeLong(id)

        val names: MutableList<String> = ArrayList()
        val ids: MutableList<Long> = ArrayList()
        members.forEach({
            names.add(it.first)
            ids.add(it.second)
        })

        writeStringList(names)
        writeLongArray(ids.toLongArray())
    }

    /**
     * Save the current team to the database. Creates a new [Team] if id < 0.
     *
     * @param context to get database instance
     * @return [Team] if successful and [BCError] if an error occurred
     */
    fun save(context: Context): Deferred<Pair<Team?, BCError?>> {
        return if (id < 0) {
            createNewAndSave(context)
        } else {
            update(context)
        }
    }

    /**
     * Create a new [Team] and save to the database.
     *
     * @param context to get database instance
     * @return [Team] if successful and [BCError] if an error occurred
     */
    private fun createNewAndSave(context: Context): Deferred<Pair<Team?, BCError?>> {
        return async(CommonPool) {
            if (!isTeamNameValid(name)) {
                val error = BCError(
                        context.resources.getString(R.string.error_saving_team),
                        context.resources.getString(R.string.error_team_name_invalid),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            } else if (!isTeamNameUnique(context, name).await()) {
                val error = BCError(
                        context.resources.getString(R.string.error_saving_team),
                        context.resources.getString(R.string.error_team_name_in_use),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            }

            val database = DatabaseHelper.getInstance(context).writableDatabase
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
            val currentDate = dateFormat.format(Date())

            var values = ContentValues().apply {
                put(TeamEntry.COLUMN_TEAM_NAME, name)
                put(TeamEntry.COLUMN_DATE_MODIFIED, currentDate)
            }

            val teamId: Long

            database.beginTransaction()
            try {
                teamId = database.insert(TeamEntry.TABLE_NAME, null, values)
                members.forEach {
                    values = ContentValues().apply {
                        put(TeamBowlerEntry.COLUMN_BOWLER_ID, it.second)
                        put(TeamBowlerEntry.COLUMN_TEAM_ID, teamId)
                    }

                    database.insert(TeamBowlerEntry.TABLE_NAME, null, values)
                }

                database.setTransactionSuccessful()
            } catch (ex: Exception) {
                Log.e(TAG, "Could not create a new team")
                val error = BCError(
                        context.resources.getString(R.string.error_saving_team),
                        context.resources.getString(R.string.error_team_not_saved),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            } finally {
                database.endTransaction()
            }

            Pair(Team(name, teamId, members), null)
        }
    }

    /**
     * Update the [Team] in the database.
     *
     * @param context context to get database instance
     * @return [Team] if successful and [BCError] if an error occurred
     */
    private fun update(context: Context): Deferred<Pair<Team?, BCError?>> {
        return async(CommonPool) {
            if (!isTeamNameValid(name)) {
                val error = BCError(
                        context.resources.getString(R.string.error_saving_team),
                        context.resources.getString(R.string.error_team_name_invalid),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            } else if (!isTeamNameUnique(context, name, id).await()) {
                val error = BCError(
                        context.resources.getString(R.string.error_saving_team),
                        context.resources.getString(R.string.error_team_name_in_use),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            }

            val database = DatabaseHelper.getInstance(context).writableDatabase

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
            val currentDate = dateFormat.format(Date())

            var values = ContentValues().apply {
                put(TeamEntry.COLUMN_TEAM_NAME, name)
                put(TeamEntry.COLUMN_DATE_MODIFIED, currentDate)
            }

            database.beginTransaction()
            try {
                database.update(
                        TeamEntry.TABLE_NAME,
                        values,
                        "${TeamEntry._ID}=?",
                        arrayOf(id.toString())
                )

                database.delete(
                        TeamBowlerEntry.TABLE_NAME,
                        "${TeamBowlerEntry.COLUMN_TEAM_ID}=?",
                        arrayOf(teamId.toString())
                )

                members.forEach {
                    values = ContentValues().apply {
                        put(TeamBowlerEntry.COLUMN_BOWLER_ID, it.second)
                        put(TeamBowlerEntry.COLUMN_TEAM_ID, teamId)
                    }

                    database.insert(TeamBowlerEntry.TABLE_NAME, null, values)
                }

                database.setTransactionSuccessful()
            } catch (ex: Exception) {
                Log.e(TAG, "Could not update team")
                val error = BCError(
                        context.resources.getString(R.string.error_saving_team),
                        context.resources.getString(R.string.error_team_not_saved),
                        BCError.Severity.Error
                )
                return@async Pair(null, error)
            } finally {
                database.endTransaction()
            }

            Pair(this@Team, null)
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
                database.delete(TeamEntry.TABLE_NAME,
                        TeamEntry._ID + "=?",
                        whereArgs)
                database.setTransactionSuccessful()
            } catch (e: Exception) {
                // Does nothing
                // If there's an error deleting this team, then they just remain in the
                // user's data and no harm is done.
            } finally {
                database.endTransaction()
            }
        }
    }

    companion object {
        /** Logging identifier. */
        private const val TAG = "Team"

        /** Valid regex for a name. */
        private val REGEX_NAME = Bowler.REGEX_NAME

        /** Creator, required by [Parcelable]. */
        @JvmField val CREATOR = parcelableCreator(::Team)

        /**
         * Order by which to sort teams.
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
         * Check if a name is a valid [Team] name.
         *
         * @param name name to check
         * @return true if the name is valid, false otherwise
         */
        private fun isTeamNameValid(name: String): Boolean = REGEX_NAME.matches(name)

        /**
         * Check if a name is unique in the Team database.
         *
         * @param context to get database instance
         * @param name name to check
         * @param id id of the existing tean, so if the name is unchanged it can be saved
         * @return true if the name is not already in the database, false otherwise
         */
        private fun isTeamNameUnique(context: Context, name: String, id: Long = -1): Deferred<Boolean> {
            return async(CommonPool) {
                val database = DatabaseHelper.getInstance(context).readableDatabase

                var cursor: Cursor? = null
                try {
                    cursor = database.query(
                            TeamEntry.TABLE_NAME,
                            arrayOf(TeamEntry.COLUMN_TEAM_NAME),
                            "${TeamEntry.COLUMN_TEAM_NAME}=? AND ${TeamEntry._ID}!=?",
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
         * Get all of the teams available in the app.
         *
         * @param context to get database instance
         * @return a [MutableList] of [Team] instances from the database
         */
        fun fetchAll(context: Context): Deferred<MutableList<Team>> {
            return async(CommonPool) {
                val teams: MutableList<Team> = ArrayList()

                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                val sortBy = Sort.fromInt(preferences.getInt(Preferences.TEAM_SORT_ORDER, Sort.Alphabetically.ordinal))
                val database = DatabaseHelper.getInstance(context).readableDatabase

                val orderQueryBy = if (sortBy == Sort.Alphabetically) {
                    " ORDER BY team." + TeamEntry.COLUMN_TEAM_NAME
                } else {
                    " ORDER BY team." + TeamEntry.COLUMN_DATE_MODIFIED + " DESC"
                }

                val rawTeamQuery = ("SELECT "
                        + "team." + TeamEntry.COLUMN_TEAM_NAME + ", "
                        + "team." + TeamEntry._ID + " AS tid, "
                        + "bowler." + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                        + "bowler." + BowlerEntry._ID + " as bid "
                        + "FROM " + TeamEntry.TABLE_NAME + " AS team "
                        + "JOIN " + TeamBowlerEntry.TABLE_NAME + " AS tb "
                        + "ON team." + TeamEntry._ID + "=" + TeamBowlerEntry.COLUMN_TEAM_ID + " "
                        + "JOIN " + BowlerEntry.TABLE_NAME + " AS bowler "
                        + "ON tb." + TeamBowlerEntry.COLUMN_BOWLER_ID + "=bowler." + BowlerEntry._ID
                        + orderQueryBy + ", "
                        + "bowler." + BowlerEntry.COLUMN_BOWLER_NAME)

                var cursor: Cursor? = null
                try {
                    cursor = database.rawQuery(rawTeamQuery, emptyArray())
                    if (cursor.moveToFirst()) {
                        var teamId = cursor.getLong(cursor.getColumnIndex("tid"))
                        var teamName = cursor.getString(cursor.getColumnIndex(TeamEntry.COLUMN_TEAM_NAME))
                        var members: MutableList<Pair<String, Long>> = ArrayList()

                        while (!cursor.isAfterLast) {
                            val currentTeamId = cursor.getLong(cursor.getColumnIndex("tid"))
                            if (teamId != currentTeamId) {
                                val team = Team(
                                        teamName,
                                        teamId,
                                        members
                                )
                                teams.add(team)

                                teamId = currentTeamId
                                teamName = cursor.getString(cursor.getColumnIndex(TeamEntry.COLUMN_TEAM_NAME))
                                members = ArrayList()
                            }

                            members.add(Pair(
                                    cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME)),
                                    cursor.getLong(cursor.getColumnIndex("bid"))
                            ))
                            cursor.moveToNext()
                        }
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed) {
                        cursor.close()
                    }
                }

                teams
            }
        }
    }
}