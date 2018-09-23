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

    companion object {
        const val EMPTY_STATISTIC = "—"
    }

    val titleId: Int
    val displayValue: String
    val category: StatisticsCategory
    val canBeGraphed: Boolean
    val primaryGraphY: Float?
    val secondaryGraphY: Float?

    val primaryGraphDataLabelId: Int
        get() = titleId

    val secondaryGraphDataLabelId: Int?

    fun getSubtitle(): String? { return null }

    fun isModifiedBy(unit: StatisticsUnit) = false

    fun modify(unit: StatisticsUnit) {}

    fun isModifiedBy(series: StatSeries) = false

    fun modify(series: StatSeries) {}

    fun isModifiedBy(game: StatGame) = false

    fun modify(game: StatGame) {}

    fun isModifiedBy(frame: StatFrame) = false

    fun modify(frame: StatFrame) {}

    fun zero()

    fun getTitle(resources: Resources): String {
        return resources.getString(titleId)
    }

    override fun describeContents(): Int {
        return titleId
    }
}

interface PercentageStatistic : Statistic {

    companion object {
        private val formatter = DecimalFormat("#.##")
    }

    var numerator: Int
    var denominator: Int

    override val canBeGraphed
        get() = true

    override val primaryGraphY: Float?
        get() = numerator.toFloat()

    override val secondaryGraphY: Float?
        get() = denominator.toFloat()

    val percentage: Double
        get() {
            return if (denominator > 0) {
                numerator.div(denominator.toDouble()).times(100)
            } else {
                0.0
            }
        }

    override val displayValue: String
        get() = if (denominator > 0) {
            "${formatter.format(percentage)}% [$numerator/$denominator]"
        } else {
            Statistic.EMPTY_STATISTIC
        }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(numerator)
        writeInt(denominator)
    }

    override fun zero() {
        numerator = 0
        denominator = 0
    }
}

interface AverageStatistic : Statistic {

    companion object {
        private val formatter = DecimalFormat("#.##")
    }

    var total: Int
    var divisor: Int

    val average: Double
        get() = if (divisor > 0) total.div(divisor.toDouble()) else 0.0

    override val primaryGraphY: Float?
        get() = average.toFloat()

    override val secondaryGraphY: Float?
        get() = null

    override val secondaryGraphDataLabelId: Int?
        get() = null

    override val displayValue: String
        get() {
            return if (average > 0) formatter.format(average) else Statistic.EMPTY_STATISTIC
        }

    override val canBeGraphed
        get() = true

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(total)
        writeInt(divisor)
    }

    override fun zero() {
        total = 0
        divisor = 0
    }
}

interface IntegerStatistic : Statistic {
    var value: Int

    override val displayValue: String
        get() = value.toString()

    override val canBeGraphed
        get() = true

    override val primaryGraphY: Float?
        get() = value.toFloat()

    override val secondaryGraphY: Float?
        get() = null

    override val secondaryGraphDataLabelId: Int?
        get() = null

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(value)
    }

    override fun zero() {
        value = 0
    }
}

interface StringStatistic : Statistic {
    var value: String

    override val displayValue: String
        get() = value

    override val canBeGraphed
        get() = false

    override val primaryGraphY: Float?
        get() = throw IllegalAccessException("StringStatistic does not have a primaryGraphY")

    override val secondaryGraphY: Float?
        get() = throw IllegalAccessException("StringStatistic does not have a secondaryGraphY")

    override val secondaryGraphDataLabelId: Int?
        get() = throw IllegalAccessException("StringStatistic does not have a secondaryGraphDataLabelId")

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(value)
    }

    override fun zero() {
        value = ""
    }
}
