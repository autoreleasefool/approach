package ca.josephroque.bowlingcompanion.statistics.provider

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A statistic to display.
 */
sealed class Statistic : IIdentifiable, KParcelable {

    abstract val displayValue: String

    /** A statistic with a percentage value to display. */
    data class PercentageStatistic(override val id: Long, val name: String, val numerator: Int, val denominator: Int) : Statistic() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(::PercentageStatistic)
        }

        /**
         * Construct [PercentageStatistic] from a [Parcel].
         */
        constructor(p: Parcel): this(p.readLong(), p.readString(), p.readInt(), p.readInt())

        /** @Override */
        override val displayValue: String
            get() = ""

        /** @Override */
        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeLong(id)
            writeString(name)
            writeInt(numerator)
            writeInt(denominator)
        }
    }

    /** A statistic with a numeric value to display. */
    data class FlatStatistic(override val id: Long, val name: String, val value: Int) : Statistic() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(::FlatStatistic)
        }

        /**
         * Construct [FlatStatistic] from a [Parcel].
         */
        constructor(p: Parcel): this(p.readLong(), p.readString(), p.readInt())

        /** @Override */
        override val displayValue: String
            get() = value.toString()

        /** @Override */
        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeLong(id)
            writeString(name)
            writeInt(value)
        }
    }

    /** @Override */
    override fun describeContents(): Int {
        // When changing, update `Statistic.getParcelable`
        return when (this) {
            is PercentageStatistic -> 0
            is FlatStatistic -> 1
        }
    }

    companion object {
        /**
         * Get the [Statistic] from the [Bundle] depending on the given [type].
         *
         * @param arguments bundle to get statistic from
         * @param key the key identiftying the [Parcel] in the [Bundle]
         * @param type the type of statistic to get
         * @return the [Statistic]
         */
        fun getParcelable(arguments: Bundle?, key: String, type: Int): Statistic? {
            return when (type) {
                // When changing, update `Statistic::describeContents`
                0 -> arguments?.getParcelable<PercentageStatistic>(key)
                1 -> arguments?.getParcelable<FlatStatistic>(key)
                else -> throw IllegalArgumentException("Statistic type $type does not exist")
            }
        }
    }
}
