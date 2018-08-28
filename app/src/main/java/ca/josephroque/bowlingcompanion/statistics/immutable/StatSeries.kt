package ca.josephroque.bowlingcompanion.statistics.immutable

import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.database.Contract.BowlerEntry
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry
import ca.josephroque.bowlingcompanion.database.Contract.TeamBowlerEntry
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.games.lane.Pin
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.scoring.Fouls
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * An immutable Series for calculating statistics, created from the database.
 */
class StatSeries(
    override val id: Long,
    val games: List<StatGame>
) : IIdentifiable, KParcelable {

    /** Series total. */
    val total: Int
        get() = games.sumBy { it.score }

    // MARK: KParcelable

    /**
     * Construct a [StatSeries] from a [Parcel].
     */
    private constructor(p: Parcel): this(
        id = p.readLong(),
        games = arrayListOf<StatGame>().apply {
            val parcelableArray = p.readParcelableArray(StatGame::class.java.classLoader)
            this.addAll(parcelableArray.map {
                return@map it as StatGame
            })
        }
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeParcelableArray(games.toTypedArray(), 0)
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "StatSeries"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::StatSeries)

        /** Fields to query to create a series. */
        private val queryFields = (
                "series.${SeriesEntry._ID} as sid, "
                + "${StatGame.QUERY_FIELDS.joinToString(separator = ", ")}, "
                + "${StatFrame.QUERY_FIELDS.joinToString(separator = ", ")} ")

        /**
         * Build a list of [StatSeries] from a team.
         *
         * @param context to access database
         * @param teamId id of the team to get series for
         * @return the list of series from the database
         */
        fun loadSeriesForTeam(context: Context, teamId: Long): Deferred<List<StatSeries>> {
            val query = ("SELECT "
                    + queryFields
                    + "FROM ${TeamBowlerEntry.TABLE_NAME} as teamBowlers "
                    + "INNER JOIN ${BowlerEntry.TABLE_NAME} as bowler "
                    + "ON ${TeamBowlerEntry.COLUMN_BOWLER_ID}=bowler.${BowlerEntry._ID} "
                    + "INNER JOIN ${LeagueEntry.TABLE_NAME} as league "
                    + "ON ${BowlerEntry._ID}=${LeagueEntry.COLUMN_BOWLER_ID} "
                    + "INNER JOIN ${SeriesEntry.TABLE_NAME} as series "
                    + "ON league.${LeagueEntry._ID}=${SeriesEntry.COLUMN_LEAGUE_ID} "
                    + "INNER JOIN ${GameEntry.TABLE_NAME} as game "
                    + "ON series.${SeriesEntry._ID}=game.${GameEntry.COLUMN_SERIES_ID} "
                    + "INNER JOIN ${FrameEntry.TABLE_NAME} as frame "
                    + "ON game.${GameEntry._ID}=frame.${FrameEntry.COLUMN_GAME_ID} "
                    + "WHERE .${TeamBowlerEntry.COLUMN_TEAM_ID}=? "
                    + "ORDER BY "
                    + "bowler.${BowlerEntry._ID}, "
                    + "league.${LeagueEntry._ID}, "
                    + "series.${SeriesEntry.COLUMN_SERIES_DATE}, "
                    + "game.${GameEntry.COLUMN_GAME_NUMBER}, "
                    + "frame.${FrameEntry.COLUMN_FRAME_NUMBER}")
            val args = arrayOf(teamId.toString())
            return loadSeries(context, query, args)
        }

        /**
         * Build a list of [StatSeries] from a bowler.
         *
         * @param context to access database
         * @param bowlerId id of the bowler to get series for
         * @return the list of series from the database
         */
        fun loadSeriesForBowler(context: Context, bowlerId: Long): Deferred<List<StatSeries>> {
            val query = ("SELECT "
                    + queryFields
                    + "FROM ${LeagueEntry.TABLE_NAME} as league "
                    + "INNER JOIN ${SeriesEntry.TABLE_NAME} as series "
                    + "ON league.${LeagueEntry._ID}=${SeriesEntry.COLUMN_LEAGUE_ID} "
                    + "INNER JOIN ${GameEntry.TABLE_NAME} as game "
                    + "ON series.${SeriesEntry._ID}=game.${GameEntry.COLUMN_SERIES_ID} "
                    + "INNER JOIN ${FrameEntry.TABLE_NAME} as frame "
                    + "ON game.${GameEntry._ID}=frame.${FrameEntry.COLUMN_GAME_ID} "
                    + "WHERE league.${LeagueEntry.COLUMN_BOWLER_ID}=? "
                    + "ORDER BY "
                    + "league.${LeagueEntry._ID}, "
                    + "series.${SeriesEntry.COLUMN_SERIES_DATE}, "
                    + "game.${GameEntry.COLUMN_GAME_NUMBER}, "
                    + "frame.${FrameEntry.COLUMN_FRAME_NUMBER}")
            val args = arrayOf(bowlerId.toString())
            return loadSeries(context, query, args)
        }

        /**
         * Build a list of [StatSeries] from a league.
         *
         * @param context to access database
         * @param leagueId id of the league to get series for
         * @return the list of series from the database
         */
        fun loadSeriesForLeague(context: Context, leagueId: Long): Deferred<List<StatSeries>> {
            val query = ("SELECT "
                    + queryFields
                    + "FROM ${SeriesEntry.TABLE_NAME} as series "
                    + "INNER JOIN ${GameEntry.TABLE_NAME} as game "
                    + "ON series.${SeriesEntry._ID}=game.${GameEntry.COLUMN_SERIES_ID} "
                    + "INNER JOIN ${FrameEntry.TABLE_NAME} as frame "
                    + "ON game.${GameEntry._ID}=frame.${FrameEntry.COLUMN_GAME_ID} "
                    + "WHERE series.${SeriesEntry.COLUMN_LEAGUE_ID}=? "
                    + "ORDER BY "
                    + "series.${SeriesEntry.COLUMN_SERIES_DATE}, "
                    + "game.${GameEntry.COLUMN_GAME_NUMBER}, "
                    + "frame.${FrameEntry.COLUMN_FRAME_NUMBER}")
            val args = arrayOf(leagueId.toString())
            return loadSeries(context, query, args)
        }

        /**
         * Build a list of [StatSeries] from a series.
         *
         * @param context to access database
         * @param seriesId id of the series to get series for
         * @return the list of series from the database
         */
        fun loadSeriesForSeries(context: Context, seriesId: Long): Deferred<List<StatSeries>> {
            val query = ("SELECT "
                    + queryFields
                    + "FROM ${SeriesEntry.TABLE_NAME} as series "
                    + "INNER JOIN ${GameEntry.TABLE_NAME} as game "
                    + "ON series.${SeriesEntry._ID}=game.${GameEntry.COLUMN_SERIES_ID} "
                    + "INNER JOIN ${FrameEntry.TABLE_NAME} as frame "
                    + "ON game.${GameEntry._ID}=frame.${FrameEntry.COLUMN_GAME_ID} "
                    + "WHERE series.${SeriesEntry._ID}=? "
                    + "ORDER BY game.${GameEntry.COLUMN_GAME_NUMBER}, frame.${FrameEntry.COLUMN_FRAME_NUMBER}")
            val args = arrayOf(seriesId.toString())
            return loadSeries(context, query, args)
        }

        /**
         * Build a list of [StatSeries] from a query.
         *
         * @param context to access database
         * @param query query to get cursor
         * @param args args for the query
         * @return the list of series from the database
         */
        private fun loadSeries(context: Context, query: String, args: Array<String>): Deferred<List<StatSeries>> {
            return async(CommonPool) {
                val db = DatabaseHelper.getInstance(context).writableDatabase

                var lastGameId: Long = -1
                var lastSeriesId: Long = -1
                val series: MutableList<StatSeries> = ArrayList()
                var games: MutableList<StatGame> = ArrayList(League.MAX_NUMBER_OF_GAMES)
                var frames: MutableList<StatFrame> = ArrayList(Game.NUMBER_OF_FRAMES)

                /**
                 * Build a series from a cursor into the database.
                 *
                 * @param cursor database accessor
                 * @return a new series
                 */
                fun buildSeriesFromCursor(cursor: Cursor): StatSeries {
                    return StatSeries(
                            id = cursor.getLong(cursor.getColumnIndex("sid")),
                            games = games
                    )
                }

                /**
                 * Build a game from a cursor into the database.
                 *
                 * @param cursor database accessor
                 * @return a new game
                 */
                fun buildGameFromCursor(cursor: Cursor): StatGame {
                    return StatGame(
                            id = cursor.getLong(cursor.getColumnIndex("gid")),
                            ordinal = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_GAME_NUMBER)),
                            score = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_SCORE)),
                            isManual = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_IS_MANUAL)) == 1,
                            frames = frames,
                            matchPlay = MatchPlayResult.fromInt(cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_MATCH_PLAY)))!!
                    )
                }

                var cursor: Cursor? = null
                try {
                    cursor = db.rawQuery(query, args, null)
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast) {
                            val newGameId = cursor.getLong(cursor.getColumnIndex("gid"))
                            if (newGameId != lastGameId && lastGameId != -1L) {
                                cursor.moveToPrevious()
                                games.add(buildGameFromCursor(cursor))
                                frames = ArrayList(Game.NUMBER_OF_FRAMES)
                                cursor.moveToNext()
                            }

                            val newSeriesId = cursor.getLong(cursor.getColumnIndex("sid"))
                            if (newSeriesId != lastSeriesId && lastSeriesId != -1L) {
                                cursor.moveToPrevious()
                                series.add(buildSeriesFromCursor(cursor))
                                games = ArrayList(League.MAX_NUMBER_OF_GAMES)
                                cursor.moveToNext()
                            }

                            frames.add(StatFrame(
                                    id = cursor.getLong(cursor.getColumnIndex("fid")),
                                    ordinal = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_FRAME_NUMBER)),
                                    isAccessed = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_IS_ACCESSED)) == 1,
                                    pinState = Array(Frame.NUMBER_OF_BALLS) {
                                        return@Array Pin.deckFromInt(cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_PIN_STATE[it])))
                                    },
                                    ballFouled = BooleanArray(Frame.NUMBER_OF_BALLS) {
                                        return@BooleanArray Fouls.foulIntToString(cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_FOULS))).contains((it + 1).toString())
                                    }
                            ))

                            lastSeriesId = newSeriesId
                            lastGameId = newGameId
                            cursor.moveToNext()
                        }

                        cursor.moveToPrevious()

                        games.add(buildGameFromCursor(cursor))
                        series.add(buildSeriesFromCursor(cursor))
                    }
                } finally {
                    cursor?.close()
                }

                return@async series
            }
        }
    }
}
