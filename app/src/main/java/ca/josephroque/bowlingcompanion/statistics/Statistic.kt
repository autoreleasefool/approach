package ca.josephroque.bowlingcompanion.statistics

import android.content.res.Resources
import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
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
    fun isModifiedBy(unit: StatisticsUnit): Boolean

    /** Modify the statistic given a [StatisticsUnit]. */
    fun modify(unit: StatisticsUnit) {}

    /** Indicates if this statistic will be modified by a given [Game]. */
    fun isModifiedBy(game: Game): Boolean

    /** Modify the statistic given a [Game]. */
    fun modify(game: Game) {}

    /** Indicates if this statistic will be modified by a given [Frame]. */
    fun isModifiedBy(frame: Frame): Boolean

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
