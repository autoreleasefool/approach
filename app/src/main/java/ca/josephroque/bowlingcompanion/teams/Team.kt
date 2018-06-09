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
import ca.josephroque.bowlingcompanion.common.interfaces.IDeletable
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.database.Contract.*
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMember
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
        override val id: Long,
        val name: String,
        val members: List<TeamMember>
): KParcelable, IDeletable, IIdentifiable {

    /** Private field to indicate if the item is deleted. */
    private var _isDeleted: Boolean = false
    /** @Override */
    override val isDeleted: Boolean
        get() = _isDeleted

    /** Get the list of series for the team members. */
    val series: List<Series>
        get() = members.map { it.series!! }

    /**
     * Construct [Team] from a [Parcel]
     */
    private constructor(p: Parcel): this(
            id = p.readLong(),
            name = p.readString(),
            members = arrayListOf<TeamMember>().apply {
                val parcelableArray = p.readParcelableArray(TeamMember::class.java.classLoader)
                this.addAll(parcelableArray.map {
                    return@map it as TeamMember
                })
            }
    )

    /**
     * Construct [Team] from a [Team].
     */
    constructor(team: Team): this(
            id = team.id,
            name = team.name,
            members = team.members
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(name)
        writeParcelableArray(members.toTypedArray(), 0)
    }

    /** @Override */
    override fun markForDeletion(): Team {
        val newInstance = Team(this)
        newInstance._isDeleted = true
        return newInstance
    }

    /** @Override */
    override fun cleanDeletion(): Team {
        val newInstance = Team(this)
        newInstance._isDeleted = false
        return this
    }

    /** @Override */
    override fun delete(context: Context): Deferred<Unit> {
        return async(CommonPool) {
            if (id < 0) {
                return@async
            }

            val database = DatabaseHelper.getInstance(context).writableDatabase
            database.beginTransaction()
            try {
                database.delete(TeamEntry.TABLE_NAME,
                        TeamEntry._ID + "=?",
                        arrayOf(id.toString()))
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

    /**
     * Replace a member of the team with a newer instance.
     *
     * @param newMember the team member to add
     * @return a new instance of [Team]
     */
    fun replaceTeamMember(newMember: TeamMember): Team {
        val oldMembers = members.toMutableList()
        val replacedMemberIndex = newMember.indexInList(members)
        assert(replacedMemberIndex > -1)
        oldMembers[replacedMemberIndex] = newMember

        return Team(
                id = this.id,
                name = this.name,
                members = oldMembers
        )
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "Team"

        /** Valid regex for a name. */
        private val REGEX_NAME = Bowler.REGEX_NAME

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
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
         * @param id id of the existing team, so if the name is unchanged it can be saved
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
         * Ensure all preconditions to save are met.
         *
         * @param context to get error strings
         * @param id -1 for a new team, or the id of the existing team
         * @param name name for the team
         * @param members list of team members
         * @return [BCError] if a precondition is not met, null otherwise
         */
        private fun validateSavePreconditions(
                context: Context,
                id: Long,
                name: String,
                members: List<TeamMember>
        ): Deferred<BCError?> {
            return async(CommonPool) {
                val errorMessage = R.string.issue_saving_team
                val errorTitle: Int? = if (!isTeamNameValid(name)) {
                    R.string.error_team_name_invalid
                } else if (!isTeamNameUnique(context, name, id).await()) {
                    R.string.error_team_name_in_use
                } else if (members.isEmpty()) {
                    R.string.error_team_has_no_members
                } else {
                    null
                }

                return@async if (errorTitle != null) {
                    BCError(errorTitle, errorMessage, BCError.Severity.Warning)
                } else {
                    null
                }
            }
        }

        /**
         * Save the current team to the database.
         *
         * @param context to get database instance
         * @param id -1 for a new team, or the id of the existing team
         * @param name name for the team
         * @param members list of team members
         * @return the saved [Team] or a [BCError] if any errors occurred
         */
        fun save(
                context: Context,
                id: Long,
                name: String,
                members: List<TeamMember>
        ): Deferred<Pair<Team?, BCError?>> {
            return if (id < 0) {
                createNewAndSave(context, name, members)
            } else {
                update(context, id, name, members)
            }
        }

        /**
         * Create a new [TeamEntry] in the database.
         *
         * @param context to get database instance
         * @param name name for the team
         * @param members list of team members
         * @return the saved [Team] or a [BCError] if any errors occurred
         */
        private fun createNewAndSave(
                context: Context,
                name: String,
                members: List<TeamMember>
        ): Deferred<Pair<Team?, BCError?>> {
            return async(CommonPool) {
                val error = validateSavePreconditions(context, -1, name, members).await()
                if (error != null) {
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
                            put(TeamBowlerEntry.COLUMN_BOWLER_ID, it.bowlerId)
                            put(TeamBowlerEntry.COLUMN_TEAM_ID, teamId)
                        }

                        database.insert(TeamBowlerEntry.TABLE_NAME, null, values)
                    }

                    database.setTransactionSuccessful()
                } catch (ex: Exception) {
                    Log.e(TAG, "Could not create a new team")
                    return@async Pair(
                            null,
                            BCError(R.string.error_saving_team, R.string.error_team_not_saved)
                    )
                } finally {
                    database.endTransaction()
                }

                Pair(Team(teamId, name, members), null)
            }
        }

        /**
         * Update the [TeamEntry] in the database.
         *
         * @param context context to get database instance
         * @param id id of the team to update
         * @param name name for the team
         * @param members list of team members
         * @return the saved [Team] or a [BCError] if any errors occurred
         */
        private fun update(
                context: Context,
                id: Long,
                name: String,
                members: List<TeamMember>
        ): Deferred<Pair<Team?, BCError?>> {
            return async(CommonPool) {
                val error = validateSavePreconditions(context, id, name, members).await()
                if (error != null) {
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
                            arrayOf(id.toString())
                    )

                    members.forEach {
                        values = ContentValues().apply {
                            put(TeamBowlerEntry.COLUMN_BOWLER_ID, it.bowlerId)
                            put(TeamBowlerEntry.COLUMN_TEAM_ID, id)
                        }

                        database.insert(TeamBowlerEntry.TABLE_NAME, null, values)
                    }

                    database.setTransactionSuccessful()
                } catch (ex: Exception) {
                    Log.e(TAG, "Could not update team")
                    return@async Pair(
                            null,
                            BCError(R.string.error_saving_team, R.string.error_team_not_saved)
                    )
                } finally {
                    database.endTransaction()
                }

                Pair(Team(id, name, members), null)
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
                        var members: MutableList<TeamMember> = ArrayList()

                        while (!cursor.isAfterLast) {
                            val newId = cursor.getLong(cursor.getColumnIndex("tid"))
                            if (newId != teamId) {
                                teams.add(Team(teamId, teamName, members))

                                teamId = newId
                                teamName = cursor.getString(cursor.getColumnIndex(TeamEntry.COLUMN_TEAM_NAME))
                                members = ArrayList()
                            }

                            members.add(TeamMember(
                                    teamId = teamId,
                                    bowlerName = cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME)),
                                    bowlerId = cursor.getLong(cursor.getColumnIndex("bid"))
                            ))
                            cursor.moveToNext()
                        }

                        teams.add(Team(teamId, teamName, members))
                    }


                } finally {
                    cursor?.close()
                }

                teams
            }
        }
    }
}
