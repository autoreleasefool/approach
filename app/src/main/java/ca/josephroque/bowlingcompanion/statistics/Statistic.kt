package ca.josephroque.bowlingcompanion.statistics

import android.content.res.Resources
import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.statistics.immutable.StatFrame
import ca.josephroque.bowlingcompanion.statistics.immutable.StatGame
import ca.josephroque.bowlingcompanion.statistics.immutable.StatSeries
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

    /** Label for the primary graph data. */
    val primaryGraphDataLabelId: Int
        get() = titleId

    /** Label for the secondary graph data. */
    val secondaryGraphDataLabelId: Int?

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

    override val secondaryGraphDataLabelId: Int?
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

    override val secondaryGraphDataLabelId: Int?
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

    override val secondaryGraphDataLabelId: Int?
        get() = throw IllegalAccessException("StringStatistic does not have a secondaryGraphDataLabelId")

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(value)
    }

    /** @Override */
    override fun zero() {
        value = ""
    }
}
