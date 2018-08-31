package ca.josephroque.bowlingcompanion.statistics

import android.content.res.Resources
import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.statistics.immutable.StatFrame
import ca.josephroque.bowlingcompanion.statistics.immutable.StatGame
import ca.josephroque.bowlingcompanion.statistics.immutable.StatSeries
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.AcesSparedStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.AcesStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.pinsleftondeck.AveragePinsLeftStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.general.BowlerNameStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.ChopOffsSparedStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.ChopOffsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.foul.FoulsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game10AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game11AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game12AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game13AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game14AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game15AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game16AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game17AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game18AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game19AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game1AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game20AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game2AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game3AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game4AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game5AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game6AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game7AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game8AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.average.Game9AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.GameAverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.general.GameNameStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.matchplay.GamesLostStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.matchplay.GamesTiedStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.matchplay.GamesWonStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.HeadPinsSparedStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.HeadPinsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.HighSingleStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.general.LeagueNameStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.LeftChopOffsSparedStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.LeftChopOffsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.LeftOfMiddleHitsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.LeftSplitsSparedStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.LeftSplitsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.LeftsSparedStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.LeftsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.MiddleHitsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.NumberOfGamesStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.RightChopOffsSparedStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.RightChopOffsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.RightOfMiddleHitsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.RightSplitsSparedStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.RightSplitsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.RightsSparedStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.RightsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.general.SeriesNameStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.SpareConversionsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.SplitsSparedStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.SplitsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.StrikesStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.StrikeMiddleHitsStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.TotalPinfallStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.pinsleftondeck.TotalPinsLeftStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf10Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf11Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf12Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf13Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf14Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf15Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf16Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf17Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf18Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf19Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf20Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf2Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf3Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf4Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf5Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf6Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf7Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf8Statistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesOf9Statistic
import ca.josephroque.bowlingcompanion.statistics.list.StatisticListItem
import ca.josephroque.bowlingcompanion.statistics.unit.StatisticsUnit
import java.text.DecimalFormat

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A statistic to display.
 */
interface Statistic : StatisticListItem, KParcelable {

    /** String ID of the title of the statistic. */
    val titleId: Int

    /** Value to be displayed by the statistic. */
    val displayValue: String

    /** Optional subtitle to be displayed by the statistic. */
    fun getSubtitle(): String? { return null }

    /** Category that the statistic belongs to. */
    val category: StatisticsCategory

    /** Indicates if a graph can be generated from the statistic. */
    val canBeGraphed: Boolean

    /** Get a primary Y position for a graph entry from the statistic's current state, if available. */
    val primaryGraphY: Float?

    /** Get a secondary Y position for a graph entry from the statistic's current state, if available. */
    val secondaryGraphY: Float?

    /** Indicates if this statistic will be modified by a given [StatisticsUnit]. */
    fun isModifiedBy(unit: StatisticsUnit) = false

    /** Modify the statistic given a [StatisticsUnit]. */
    fun modify(unit: StatisticsUnit) {}

    /** Indicates if this statistic will be modified by a given [StatSeries]. */
    fun isModifiedBy(series: StatSeries) = false

    /** Modify the statistic given a [StatSeries]. */
    fun modify(series: StatSeries) {}

    /** Indicates if this statistic will be modified by a given [StatGame]. */
    fun isModifiedBy(game: StatGame) = false

    /** Modify the statistic given a [StatGame]. */
    fun modify(game: StatGame) {}

    /** Indicates if this statistic will be modified by a given [StatFrame]. */
    fun isModifiedBy(frame: StatFrame) = false

    /** Modify the statistic given a [StatFrame]. */
    fun modify(frame: StatFrame) {}

    /** Zero the statistic. */
    fun zero()

    /**
     * Get the title of the statistic.
     *
     * @param resources to get string
     * @return the title from the app resources
     */
    fun getTitle(resources: Resources): String {
        return resources.getString(titleId)
    }

    /** @Override */
    override fun describeContents(): Int {
        return titleId
    }

    companion object {
        /** Displayed value for an empty statistic. */
        const val EMPTY_STATISTIC = "—"

        /**
         * Get a full list of new instances of [Statistic]s.
         *
         * @return a list with a new instance of each [Statistic]
         */
        fun getFreshStatistics(): MutableList<Statistic> = mutableListOf(
                // General
                BowlerNameStatistic(),
                LeagueNameStatistic(),
                SeriesNameStatistic(),
                GameNameStatistic(),
                // Overall
                HighSingleStatistic(),
                TotalPinfallStatistic(),
                NumberOfGamesStatistic(),
                GameAverageStatistic(),
                MiddleHitsStatistic(),
                LeftOfMiddleHitsStatistic(),
                RightOfMiddleHitsStatistic(),
                StrikeMiddleHitsStatistic(),
                StrikesStatistic(),
                SpareConversionsStatistic(),
                // First Ball
                HeadPinsStatistic(),
                HeadPinsSparedStatistic(),
                LeftsStatistic(),
                LeftsSparedStatistic(),
                RightsStatistic(),
                RightsSparedStatistic(),
                AcesStatistic(),
                AcesSparedStatistic(),
                ChopOffsStatistic(),
                ChopOffsSparedStatistic(),
                LeftChopOffsStatistic(),
                LeftChopOffsSparedStatistic(),
                RightChopOffsStatistic(),
                RightChopOffsSparedStatistic(),
                SplitsStatistic(),
                SplitsSparedStatistic(),
                LeftSplitsStatistic(),
                LeftSplitsSparedStatistic(),
                RightSplitsStatistic(),
                RightSplitsSparedStatistic(),
                // Fouls
                FoulsStatistic(),
                // Pins Left on Deck
                TotalPinsLeftStatistic(),
                AveragePinsLeftStatistic(),
                // Average
                Game1AverageStatistic(),
                Game2AverageStatistic(),
                Game3AverageStatistic(),
                Game4AverageStatistic(),
                Game5AverageStatistic(),
                Game6AverageStatistic(),
                Game7AverageStatistic(),
                Game8AverageStatistic(),
                Game9AverageStatistic(),
                Game10AverageStatistic(),
                Game11AverageStatistic(),
                Game12AverageStatistic(),
                Game13AverageStatistic(),
                Game14AverageStatistic(),
                Game15AverageStatistic(),
                Game16AverageStatistic(),
                Game17AverageStatistic(),
                Game18AverageStatistic(),
                Game19AverageStatistic(),
                Game20AverageStatistic(),
                // Series
                HighSeriesOf2Statistic(),
                HighSeriesOf3Statistic(),
                HighSeriesOf4Statistic(),
                HighSeriesOf5Statistic(),
                HighSeriesOf6Statistic(),
                HighSeriesOf7Statistic(),
                HighSeriesOf8Statistic(),
                HighSeriesOf9Statistic(),
                HighSeriesOf10Statistic(),
                HighSeriesOf11Statistic(),
                HighSeriesOf12Statistic(),
                HighSeriesOf13Statistic(),
                HighSeriesOf14Statistic(),
                HighSeriesOf15Statistic(),
                HighSeriesOf16Statistic(),
                HighSeriesOf17Statistic(),
                HighSeriesOf18Statistic(),
                HighSeriesOf19Statistic(),
                HighSeriesOf20Statistic(),
                // Match Play
                GamesWonStatistic(),
                GamesLostStatistic(),
                GamesTiedStatistic()
        )

        /**
         * Read a [Statistic] from a [Parcel] and cast to the proper type based on the given ID.
         *
         * @param p parcel to read from
         * @param id id of the statistic
         * @return the [Statistic] read from the [Parcel]
         */
        fun readParcelable(p: Parcel, id: Int): Statistic {
            return when (id) {
                BowlerNameStatistic.Id -> p.readParcelable<BowlerNameStatistic>(BowlerNameStatistic::class.java.classLoader)
                LeagueNameStatistic.Id -> p.readParcelable<LeagueNameStatistic>(LeagueNameStatistic::class.java.classLoader)
                SeriesNameStatistic.Id -> p.readParcelable<SeriesNameStatistic>(SeriesNameStatistic::class.java.classLoader)
                GameNameStatistic.Id -> p.readParcelable<GameNameStatistic>(GameNameStatistic::class.java.classLoader)
                HighSingleStatistic.Id -> p.readParcelable<HighSingleStatistic>(HighSingleStatistic::class.java.classLoader)
                TotalPinfallStatistic.Id -> p.readParcelable<TotalPinfallStatistic>(TotalPinfallStatistic::class.java.classLoader)
                NumberOfGamesStatistic.Id -> p.readParcelable<NumberOfGamesStatistic>(NumberOfGamesStatistic::class.java.classLoader)
                GameAverageStatistic.Id -> p.readParcelable<GameAverageStatistic>(GameAverageStatistic::class.java.classLoader)
                MiddleHitsStatistic.Id -> p.readParcelable<MiddleHitsStatistic>(MiddleHitsStatistic::class.java.classLoader)
                LeftOfMiddleHitsStatistic.Id -> p.readParcelable<LeftOfMiddleHitsStatistic>(LeftOfMiddleHitsStatistic::class.java.classLoader)
                RightOfMiddleHitsStatistic.Id -> p.readParcelable<RightOfMiddleHitsStatistic>(RightOfMiddleHitsStatistic::class.java.classLoader)
                StrikeMiddleHitsStatistic.Id -> p.readParcelable<StrikeMiddleHitsStatistic>(StrikeMiddleHitsStatistic::class.java.classLoader)
                StrikesStatistic.Id -> p.readParcelable<StrikesStatistic>(StrikesStatistic::class.java.classLoader)
                SpareConversionsStatistic.Id -> p.readParcelable<SpareConversionsStatistic>(SpareConversionsStatistic::class.java.classLoader)
                HeadPinsStatistic.Id -> p.readParcelable<HeadPinsStatistic>(HeadPinsStatistic::class.java.classLoader)
                HeadPinsSparedStatistic.Id -> p.readParcelable<HeadPinsSparedStatistic>(HeadPinsSparedStatistic::class.java.classLoader)
                LeftsStatistic.Id -> p.readParcelable<LeftsStatistic>(LeftsStatistic::class.java.classLoader)
                LeftsSparedStatistic.Id -> p.readParcelable<LeftsSparedStatistic>(LeftsSparedStatistic::class.java.classLoader)
                RightsStatistic.Id -> p.readParcelable<RightsStatistic>(RightsStatistic::class.java.classLoader)
                RightsSparedStatistic.Id -> p.readParcelable<RightsSparedStatistic>(RightsSparedStatistic::class.java.classLoader)
                AcesStatistic.Id -> p.readParcelable<AcesStatistic>(AcesStatistic::class.java.classLoader)
                AcesSparedStatistic.Id -> p.readParcelable<AcesSparedStatistic>(AcesSparedStatistic::class.java.classLoader)
                ChopOffsStatistic.Id -> p.readParcelable<ChopOffsStatistic>(ChopOffsStatistic::class.java.classLoader)
                ChopOffsSparedStatistic.Id -> p.readParcelable<ChopOffsSparedStatistic>(ChopOffsSparedStatistic::class.java.classLoader)
                LeftChopOffsStatistic.Id -> p.readParcelable<LeftChopOffsStatistic>(LeftChopOffsStatistic::class.java.classLoader)
                LeftChopOffsSparedStatistic.Id -> p.readParcelable<LeftChopOffsSparedStatistic>(LeftChopOffsSparedStatistic::class.java.classLoader)
                RightChopOffsStatistic.Id -> p.readParcelable<RightChopOffsStatistic>(RightChopOffsStatistic::class.java.classLoader)
                RightChopOffsSparedStatistic.Id -> p.readParcelable<RightChopOffsSparedStatistic>(RightChopOffsSparedStatistic::class.java.classLoader)
                SplitsStatistic.Id -> p.readParcelable<SplitsStatistic>(SplitsStatistic::class.java.classLoader)
                SplitsSparedStatistic.Id -> p.readParcelable<SplitsSparedStatistic>(SplitsSparedStatistic::class.java.classLoader)
                LeftSplitsStatistic.Id -> p.readParcelable<LeftSplitsStatistic>(LeftSplitsStatistic::class.java.classLoader)
                LeftSplitsSparedStatistic.Id -> p.readParcelable<LeftSplitsSparedStatistic>(LeftsSparedStatistic::class.java.classLoader)
                RightSplitsStatistic.Id -> p.readParcelable<RightSplitsStatistic>(RightSplitsStatistic::class.java.classLoader)
                RightSplitsSparedStatistic.Id -> p.readParcelable<RightSplitsSparedStatistic>(RightsSparedStatistic::class.java.classLoader)
                FoulsStatistic.Id -> p.readParcelable<FoulsStatistic>(FoulsStatistic::class.java.classLoader)
                TotalPinsLeftStatistic.Id -> p.readParcelable<TotalPinsLeftStatistic>(TotalPinsLeftStatistic::class.java.classLoader)
                AveragePinsLeftStatistic.Id -> p.readParcelable<AveragePinsLeftStatistic>(AveragePinsLeftStatistic::class.java.classLoader)
                Game1AverageStatistic.Id -> p.readParcelable<Game1AverageStatistic>(Game1AverageStatistic::class.java.classLoader)
                Game2AverageStatistic.Id -> p.readParcelable<Game2AverageStatistic>(Game2AverageStatistic::class.java.classLoader)
                Game3AverageStatistic.Id -> p.readParcelable<Game3AverageStatistic>(Game3AverageStatistic::class.java.classLoader)
                Game4AverageStatistic.Id -> p.readParcelable<Game4AverageStatistic>(Game4AverageStatistic::class.java.classLoader)
                Game5AverageStatistic.Id -> p.readParcelable<Game5AverageStatistic>(Game5AverageStatistic::class.java.classLoader)
                Game6AverageStatistic.Id -> p.readParcelable<Game6AverageStatistic>(Game6AverageStatistic::class.java.classLoader)
                Game7AverageStatistic.Id -> p.readParcelable<Game7AverageStatistic>(Game7AverageStatistic::class.java.classLoader)
                Game8AverageStatistic.Id -> p.readParcelable<Game8AverageStatistic>(Game8AverageStatistic::class.java.classLoader)
                Game9AverageStatistic.Id -> p.readParcelable<Game9AverageStatistic>(Game9AverageStatistic::class.java.classLoader)
                Game10AverageStatistic.Id -> p.readParcelable<Game10AverageStatistic>(Game10AverageStatistic::class.java.classLoader)
                Game11AverageStatistic.Id -> p.readParcelable<Game11AverageStatistic>(Game11AverageStatistic::class.java.classLoader)
                Game12AverageStatistic.Id -> p.readParcelable<Game12AverageStatistic>(Game12AverageStatistic::class.java.classLoader)
                Game13AverageStatistic.Id -> p.readParcelable<Game13AverageStatistic>(Game13AverageStatistic::class.java.classLoader)
                Game14AverageStatistic.Id -> p.readParcelable<Game14AverageStatistic>(Game14AverageStatistic::class.java.classLoader)
                Game15AverageStatistic.Id -> p.readParcelable<Game15AverageStatistic>(Game15AverageStatistic::class.java.classLoader)
                Game16AverageStatistic.Id -> p.readParcelable<Game16AverageStatistic>(Game16AverageStatistic::class.java.classLoader)
                Game17AverageStatistic.Id -> p.readParcelable<Game17AverageStatistic>(Game17AverageStatistic::class.java.classLoader)
                Game18AverageStatistic.Id -> p.readParcelable<Game18AverageStatistic>(Game18AverageStatistic::class.java.classLoader)
                Game19AverageStatistic.Id -> p.readParcelable<Game19AverageStatistic>(Game19AverageStatistic::class.java.classLoader)
                Game20AverageStatistic.Id -> p.readParcelable<Game20AverageStatistic>(Game20AverageStatistic::class.java.classLoader)
                HighSeriesOf2Statistic.Id -> p.readParcelable<HighSeriesOf2Statistic>(HighSeriesOf2Statistic::class.java.classLoader)
                HighSeriesOf3Statistic.Id -> p.readParcelable<HighSeriesOf3Statistic>(HighSeriesOf3Statistic::class.java.classLoader)
                HighSeriesOf4Statistic.Id -> p.readParcelable<HighSeriesOf4Statistic>(HighSeriesOf4Statistic::class.java.classLoader)
                HighSeriesOf5Statistic.Id -> p.readParcelable<HighSeriesOf5Statistic>(HighSeriesOf5Statistic::class.java.classLoader)
                HighSeriesOf6Statistic.Id -> p.readParcelable<HighSeriesOf6Statistic>(HighSeriesOf6Statistic::class.java.classLoader)
                HighSeriesOf7Statistic.Id -> p.readParcelable<HighSeriesOf7Statistic>(HighSeriesOf7Statistic::class.java.classLoader)
                HighSeriesOf8Statistic.Id -> p.readParcelable<HighSeriesOf8Statistic>(HighSeriesOf8Statistic::class.java.classLoader)
                HighSeriesOf9Statistic.Id -> p.readParcelable<HighSeriesOf9Statistic>(HighSeriesOf9Statistic::class.java.classLoader)
                HighSeriesOf10Statistic.Id -> p.readParcelable<HighSeriesOf10Statistic>(HighSeriesOf10Statistic::class.java.classLoader)
                HighSeriesOf11Statistic.Id -> p.readParcelable<HighSeriesOf11Statistic>(HighSeriesOf11Statistic::class.java.classLoader)
                HighSeriesOf12Statistic.Id -> p.readParcelable<HighSeriesOf12Statistic>(HighSeriesOf12Statistic::class.java.classLoader)
                HighSeriesOf13Statistic.Id -> p.readParcelable<HighSeriesOf13Statistic>(HighSeriesOf13Statistic::class.java.classLoader)
                HighSeriesOf14Statistic.Id -> p.readParcelable<HighSeriesOf14Statistic>(HighSeriesOf14Statistic::class.java.classLoader)
                HighSeriesOf15Statistic.Id -> p.readParcelable<HighSeriesOf15Statistic>(HighSeriesOf15Statistic::class.java.classLoader)
                HighSeriesOf16Statistic.Id -> p.readParcelable<HighSeriesOf16Statistic>(HighSeriesOf16Statistic::class.java.classLoader)
                HighSeriesOf17Statistic.Id -> p.readParcelable<HighSeriesOf17Statistic>(HighSeriesOf17Statistic::class.java.classLoader)
                HighSeriesOf18Statistic.Id -> p.readParcelable<HighSeriesOf18Statistic>(HighSeriesOf18Statistic::class.java.classLoader)
                HighSeriesOf19Statistic.Id -> p.readParcelable<HighSeriesOf19Statistic>(HighSeriesOf19Statistic::class.java.classLoader)
                HighSeriesOf20Statistic.Id -> p.readParcelable<HighSeriesOf20Statistic>(HighSeriesOf20Statistic::class.java.classLoader)
                GamesWonStatistic.Id -> p.readParcelable<GamesWonStatistic>(GamesWonStatistic::class.java.classLoader)
                GamesLostStatistic.Id -> p.readParcelable<GamesLostStatistic>(GamesLostStatistic::class.java.classLoader)
                GamesTiedStatistic.Id -> p.readParcelable<GamesTiedStatistic>(GamesTiedStatistic::class.java.classLoader)
                else -> throw IllegalArgumentException("$id is not a valid Statistic id.")
            }
        }
    }
}

/**
 * A statistic with a percentage value.
 */
interface PercentageStatistic : Statistic {

    companion object {
        /** Format percentages consistently. */
        private val formatter = DecimalFormat("#.##")
    }

    /** Numerator of the percentage. */
    var numerator: Int
    /** Denominator of the percentage. */
    var denominator: Int

    /** @Override */
    override val canBeGraphed
        get() = true

    override val primaryGraphY: Float?
        get() = percentage.toFloat()

    override val secondaryGraphY: Float?
        get() = denominator.toFloat()

    /** Get the percentage. */
    val percentage: Double
        get() {
            return if (denominator > 0) {
                numerator.div(denominator.toDouble()).times(100)
            } else {
                0.0
            }
        }

    /** @Override */
    override val displayValue: String
        get() = if (denominator > 0) {
            "${formatter.format(percentage)}% [$numerator/$denominator]"
        } else {
            Statistic.EMPTY_STATISTIC
        }

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(numerator)
        writeInt(denominator)
    }

    /** @Override */
    override fun zero() {
        numerator = 0
        denominator = 0
    }
}

/**
 * A statistic with a value averaged over a number.
 */
interface AverageStatistic : Statistic {

    /** Total number of occurrences or total value. */
    var total: Int
    /** Divisor to average total across. */
    var divisor: Int

    val average: Double
        get() = if (divisor > 0) total.div(divisor.toDouble()) else 0.0

    override val primaryGraphY: Float?
        get() = average.toFloat()

    override val secondaryGraphY: Float?
        get() = null

    /** @Override */
    override val displayValue: String
        get() {
            return if (average > 0) average.toString() else Statistic.EMPTY_STATISTIC
        }

    /** @Override */
    override val canBeGraphed
        get() = true

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(total)
        writeInt(divisor)
    }

    /** @Override */
    override fun zero() {
        total = 0
        divisor = 0
    }
}

/**
 * A statistic with an integer value.
 */
interface IntegerStatistic : Statistic {
    /** The integer value of the statistic. */
    var value: Int

    /** @Override */
    override val displayValue: String
        get() = value.toString()

    /** @Override */
    override val canBeGraphed
        get() = true

    override val primaryGraphY: Float?
        get() = value.toFloat()

    override val secondaryGraphY: Float?
        get() = null

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(value)
    }

    /** @Override */
    override fun zero() {
        value = 0
    }
}

/**
 * A statistic with a string value.
 */
interface StringStatistic : Statistic {
    /** The string value of the statistic. */
    var value: String

    /** @Override */
    override val displayValue: String
        get() = value

    /** @Override */
    override val canBeGraphed
        get() = false

    override val primaryGraphY: Float?
        get() = throw IllegalAccessException("StringStatistic does not have a primaryGraphY")

    override val secondaryGraphY: Float?
        get() = throw IllegalAccessException("StringStatistic does not have a secondaryGraphY")

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(value)
    }

    /** @Override */
    override fun zero() {
        value = ""
    }
}
