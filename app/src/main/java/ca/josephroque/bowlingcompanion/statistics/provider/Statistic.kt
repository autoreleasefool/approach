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

    /** A statistic with a string value to display. */
    data class StringStatistic(
            override val identifier: Identifier,
            val value: String
    ) : Statistic() {
        companion object {
            /** Creator, required by [Parcelable]. */
            @Suppress("unused")
            @JvmField val CREATOR = parcelableCreator(::StringStatistic)
        }

        /**
         * Construct [StringStatistic] from a [Parcel].
         */
        constructor(p: Parcel): this(Identifier.fromInt(p.readInt())!!, p.readString())

        /** @Override */
        override val displayValue: String
            get() = value

        /** @Override */
        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeInt(identifier.ordinal)
            writeString(value)
        }
    }

    /** @Override */
    override fun describeContents(): Int {
        // When changing, update `Statistic.getParcelable`
        return when (this) {
            is PercentageStatistic -> 0
            is IntegerStatistic -> 1
            is StringStatistic -> 2
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
                2 -> arguments?.getParcelable<StringStatistic>(key)
                else -> throw IllegalArgumentException("Statistic type $type does not exist")
            }
        }

        /** IDs for statistics. */
        enum class Identifier(val displayName: Int, val category: StatisticsCategory) {
            Bowler(R.string.statistic_bowler, StatisticsCategory.General),
            MiddleHits(R.string.statistic_middle_hits, StatisticsCategory.General),
            HitsLeftOfMiddle(R.string.statistic_left_of_middle, StatisticsCategory.General),
            HitsRightOfMiddle(R.string.statistic_right_of_middle, StatisticsCategory.General),
            Strikes(R.string.statistic_strikes, StatisticsCategory.General),
            SpareConversions(R.string.statistic_spare_conversion, StatisticsCategory.General),
            HeadPins(R.string.statistic_head_pins, StatisticsCategory.FirstBall),
            HeadPinsSpared(R.string.statistic_head_pins_spared, StatisticsCategory.FirstBall),
            Lefts(R.string.statistic_lefts, StatisticsCategory.FirstBall),
            LeftsSpared(R.string.statistic_lefts_spared, StatisticsCategory.FirstBall),
            Rights(R.string.statistic_rights, StatisticsCategory.FirstBall),
            RightsSpared(R.string.statistic_rights_spared, StatisticsCategory.FirstBall),
            Aces(R.string.statistic_aces, StatisticsCategory.FirstBall),
            AcesSpared(R.string.statistic_aces_spared, StatisticsCategory.FirstBall),
            Chops(R.string.statistic_chops, StatisticsCategory.FirstBall),
            ChopsSpared(R.string.statistic_chops_spared, StatisticsCategory.FirstBall),
            LeftChops(R.string.statistic_left_chops, StatisticsCategory.FirstBall),
            LeftChopsSpared(R.string.statistic_left_chops_spared, StatisticsCategory.FirstBall),
            RightChops(R.string.statistic_right_chops, StatisticsCategory.FirstBall),
            RightChopsSpared(R.string.statistic_right_chops_spared, StatisticsCategory.FirstBall),
            Splits(R.string.statistic_splits, StatisticsCategory.FirstBall),
            SplitsSpared(R.string.statistic_splits_spared, StatisticsCategory.FirstBall),
            LeftSplits(R.string.statistic_left_splits, StatisticsCategory.FirstBall),
            LeftSplitsSpared(R.string.statistic_left_splits_spared, StatisticsCategory.FirstBall),
            RightSplits(R.string.statistic_right_splits, StatisticsCategory.FirstBall),
            RightSplitsSpared(R.string.statistic_right_splits_spared, StatisticsCategory.FirstBall),
            Fouls(R.string.statistic_fouls, StatisticsCategory.Fouls),
            TotalPinsLeft(R.string.statistic_pins_left, StatisticsCategory.PinsOnDeck),
            AveragePinsLeft(R.string.statistic_average_pins_left, StatisticsCategory.PinsOnDeck),
            Average1(R.string.statistic_average_1, StatisticsCategory.Average),
            Average2(R.string.statistic_average_2, StatisticsCategory.Average),
            Average3(R.string.statistic_average_3, StatisticsCategory.Average),
            Average4(R.string.statistic_average_4, StatisticsCategory.Average),
            Average5(R.string.statistic_average_5, StatisticsCategory.Average),
            Average6(R.string.statistic_average_6, StatisticsCategory.Average),
            Average7(R.string.statistic_average_7, StatisticsCategory.Average),
            Average8(R.string.statistic_average_8, StatisticsCategory.Average),
            Average9(R.string.statistic_average_9, StatisticsCategory.Average),
            Average10(R.string.statistic_average_10, StatisticsCategory.Average),
            Average11(R.string.statistic_average_11, StatisticsCategory.Average),
            Average12(R.string.statistic_average_12, StatisticsCategory.Average),
            Average13(R.string.statistic_average_13, StatisticsCategory.Average),
            Average14(R.string.statistic_average_14, StatisticsCategory.Average),
            Average15(R.string.statistic_average_15, StatisticsCategory.Average),
            Average16(R.string.statistic_average_16, StatisticsCategory.Average),
            Average17(R.string.statistic_average_17, StatisticsCategory.Average),
            Average18(R.string.statistic_average_18, StatisticsCategory.Average),
            Average19(R.string.statistic_average_19, StatisticsCategory.Average),
            Average20(R.string.statistic_average_20, StatisticsCategory.Average),
            GamesWon(R.string.statistic_games_won, StatisticsCategory.MatchPlay),
            GamesLost(R.string.statistic_games_lost, StatisticsCategory.MatchPlay),
            GamesTied(R.string.statistic_games_tied, StatisticsCategory.MatchPlay),
            Average(R.string.statistic_average, StatisticsCategory.Overall),
            HighSingle(R.string.statistic_high_single, StatisticsCategory.Overall),
            HighSeries(R.string.statistic_high_series, StatisticsCategory.Overall),
            TotalPinfall(R.string.statistic_total_pinfall, StatisticsCategory.Overall),
            NumberOfGames(R.string.statistic_number_of_games, StatisticsCategory.Overall);

            companion object {
                private val map = Identifier.values().associateBy(Identifier::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }
}
