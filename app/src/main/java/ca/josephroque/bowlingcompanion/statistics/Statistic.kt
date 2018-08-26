package ca.josephroque.bowlingcompanion.statistics

import android.content.res.Resources
import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.series.Series
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
import ca.josephroque.bowlingcompanion.statistics.impl.general.TeamNameStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.matchplay.StrikeMiddleHitsStatistic
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
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsUnit
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

    /** Category that the statistic belongs to. */
    val category: StatisticsCategory

    /** Indicates if this statistic will be modified by a given [StatisticsUnit]. */
    fun isModifiedBy(unit: StatisticsUnit) = false

    /** Modify the statistic given a [StatisticsUnit]. */
    fun modify(unit: StatisticsUnit) {}

    /** Indicates if this statistic will be modified by a given [Series]. */
    fun isModifiedBy(series: Series) = false

    /** Modify the statistic given a [Series]. */
    fun modify(series: Series) {}

    /** Indicates if this statistic will be modified by a given [Game]. */
    fun isModifiedBy(game: Game) = false

    /** Modify the statistic given a [Game]. */
    fun modify(game: Game) {}

    /** Indicates if this statistic will be modified by a given [Frame]. */
    fun isModifiedBy(frame: Frame) = false

    /** Modify the statistic given a [Frame]. */
    fun modify(frame: Frame) {}

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
        val order: List<Int> = listOf(
            // General
            TeamNameStatistic.Id,
            BowlerNameStatistic.Id,
            LeagueNameStatistic.Id,
            SeriesNameStatistic.Id,
            GameNameStatistic.Id,
            // Overall
            HighSingleStatistic.Id,
            TotalPinfallStatistic.Id,
            NumberOfGamesStatistic.Id,
            GameAverageStatistic.Id,
            MiddleHitsStatistic.Id,
            LeftOfMiddleHitsStatistic.Id,
            RightOfMiddleHitsStatistic.Id,
            StrikeMiddleHitsStatistic.Id,
            StrikesStatistic.Id,
            SpareConversionsStatistic.Id,
            // First Ball
            HeadPinsStatistic.Id,
            HeadPinsSparedStatistic.Id,
            LeftsStatistic.Id,
            LeftsSparedStatistic.Id,
            RightsStatistic.Id,
            RightsSparedStatistic.Id,
            AcesStatistic.Id,
            AcesSparedStatistic.Id,
            ChopOffsStatistic.Id,
            ChopOffsSparedStatistic.Id,
            LeftChopOffsStatistic.Id,
            LeftChopOffsSparedStatistic.Id,
            RightChopOffsStatistic.Id,
            RightChopOffsSparedStatistic.Id,
            SplitsStatistic.Id,
            SplitsSparedStatistic.Id,
            LeftSplitsStatistic.Id,
            LeftSplitsSparedStatistic.Id,
            RightSplitsStatistic.Id,
            RightSplitsSparedStatistic.Id,
            // Fouls
            FoulsStatistic.Id,
            // Pins Left on Deck
            TotalPinsLeftStatistic.Id,
            AveragePinsLeftStatistic.Id,
            // Average
            Game1AverageStatistic.Id,
            Game2AverageStatistic.Id,
            Game3AverageStatistic.Id,
            Game4AverageStatistic.Id,
            Game5AverageStatistic.Id,
            Game6AverageStatistic.Id,
            Game7AverageStatistic.Id,
            Game8AverageStatistic.Id,
            Game9AverageStatistic.Id,
            Game10AverageStatistic.Id,
            Game11AverageStatistic.Id,
            Game12AverageStatistic.Id,
            Game13AverageStatistic.Id,
            Game14AverageStatistic.Id,
            Game15AverageStatistic.Id,
            Game16AverageStatistic.Id,
            Game17AverageStatistic.Id,
            Game18AverageStatistic.Id,
            Game19AverageStatistic.Id,
            Game20AverageStatistic.Id,
            // Series
            HighSeriesOf2Statistic.Id,
            HighSeriesOf3Statistic.Id,
            HighSeriesOf4Statistic.Id,
            HighSeriesOf5Statistic.Id,
            HighSeriesOf6Statistic.Id,
            HighSeriesOf7Statistic.Id,
            HighSeriesOf8Statistic.Id,
            HighSeriesOf9Statistic.Id,
            HighSeriesOf10Statistic.Id,
            HighSeriesOf11Statistic.Id,
            HighSeriesOf12Statistic.Id,
            HighSeriesOf13Statistic.Id,
            HighSeriesOf14Statistic.Id,
            HighSeriesOf15Statistic.Id,
            HighSeriesOf16Statistic.Id,
            HighSeriesOf17Statistic.Id,
            HighSeriesOf18Statistic.Id,
            HighSeriesOf19Statistic.Id,
            HighSeriesOf20Statistic.Id,
            // Match Play
            GamesWonStatistic.Id,
            GamesLostStatistic.Id,
            GamesTiedStatistic.Id
        )
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
    override val displayValue: String
        get() = "${formatter.format(numerator.div(denominator.toDouble()))}% [$numerator/$denominator]"

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(numerator)
        writeInt(denominator)
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

    /** @Override */
    override val displayValue: String
        get() = total.div(divisor).toString()

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(total)
        writeInt(divisor)
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
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(value)
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
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(value)
    }
}
