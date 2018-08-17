package ca.josephroque.bowlingcompanion.statistics.provider

import android.content.res.Resources
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import java.text.DecimalFormat

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A statistic to display.
 */
sealed class Statistic : IIdentifiable, KParcelable {

    /** Uniquely identify each statistic. */
    abstract val identifier: Identifier

    /** Value to be displayed by the statistic. */
    abstract val displayValue: String

    /** @Override */
    override val id: Long
        get() = identifier.ordinal.toLong()

    /** Name to display. */
    fun getDisplayName(resources: Resources): String = resources.getString(identifier.displayName)

    /** A statistic with a percentage value to display. */
    data class PercentageStatistic(
        override val identifier: Identifier,
        val numerator: Int,
        val denominator: Int
    ) : Statistic() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(::PercentageStatistic)

            /** Format percentages consistently. */
            private val formatter = DecimalFormat("#.##")
        }

        /**
         * Construct [PercentageStatistic] from a [Parcel].
         */
        constructor(p: Parcel): this(Identifier.fromInt(p.readInt())!!, p.readInt(), p.readInt())

        /** @Override */
        override val displayValue: String
            get() = "${formatter.format(numerator.div(denominator.toDouble()))}% [$numerator/$denominator]"

        /** @Override */
        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeInt(identifier.ordinal)
            writeInt(numerator)
            writeInt(denominator)
        }
    }

    /** A statistic with a numeric value to display. */
    data class IntegerStatistic(
        override val identifier: Identifier,
        val value: Int
    ) : Statistic() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(::IntegerStatistic)
        }

        /**
         * Construct [IntegerStatistic] from a [Parcel].
         */
        constructor(p: Parcel): this(Identifier.fromInt(p.readInt())!!, p.readInt())

        /** @Override */
        override val displayValue: String
            get() = value.toString()

        /** @Override */
        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeInt(identifier.ordinal)
            writeInt(value)
        }
    }

    /** @Override */
    override fun describeContents(): Int {
        // When changing, update `Statistic.getParcelable`
        return when (this) {
            is PercentageStatistic -> 0
            is IntegerStatistic -> 1
        }
    }

    companion object {
        /**
         * Get the [Statistic] from the [Bundle] depending on the given [type].
         *
         * @param arguments bundle to get statistic from
         * @param key the key identifying the [Parcel] in the [Bundle]
         * @param type the type of statistic to get
         * @return the [Statistic]
         */
        fun getParcelable(arguments: Bundle?, key: String, type: Int): Statistic? {
            return when (type) {
                // When changing, update `Statistic::describeContents`
                0 -> arguments?.getParcelable<PercentageStatistic>(key)
                1 -> arguments?.getParcelable<IntegerStatistic>(key)
                else -> throw IllegalArgumentException("Statistic type $type does not exist")
            }
        }

        /** IDs for statistics. */
        enum class Identifier(val displayName: Int) {
            MiddleHits(R.string.statistic_middle_hits);

            companion object {
                private val map = Identifier.values().associateBy(Identifier::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }
}
