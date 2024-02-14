package ca.josephroque.bowlingcompanion.core.statistics

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticGroup
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.AcesSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.AcesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.ChopOffsSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.ChopOffsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.FivesSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.FivesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.HeadPinsSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.HeadPinsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftChopOffsSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftChopOffsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftFivesSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftFivesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftSplitsSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftSplitsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftTapsSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftTapsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftThreesSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftThreesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftTwelvesSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.LeftTwelvesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightChopOffsSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightChopOffsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightFivesSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightFivesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightSplitsSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightSplitsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightTapsSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightTapsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightThreesSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightThreesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightTwelvesSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.RightTwelvesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.SplitsSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.SplitsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.TapsSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.TapsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.ThreesSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.ThreesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.TwelvesSparedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.firstroll.TwelvesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.foul.FoulsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.mark.SpareConversionsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.mark.StrikesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.matchplay.MatchesLostStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.matchplay.MatchesPlayedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.matchplay.MatchesTiedStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.matchplay.MatchesWonStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.middlehit.LeftOfMiddleHitsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.middlehit.MiddleHitsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.middlehit.RightOfMiddleHitsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.middlehit.StrikeMiddleHitsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.GameAverageStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.HighSingleStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.NumberOfGamesStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.TotalPinFallStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.TotalRollsStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.pinsleft.AveragePinsLeftOnDeckStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.pinsleft.TotalPinsLeftOnDeckStatistic
import ca.josephroque.bowlingcompanion.core.statistics.trackable.series.HighSeriesOf3Statistic

fun allStatistics(
	source: TrackableFilter.Source? = null,
	supportingWidgets: Boolean? = null,
): List<Statistic> = listOf(
	// Overall
	HighSingleStatistic(),
	TotalPinFallStatistic(),
	NumberOfGamesStatistic(),
	GameAverageStatistic(),
	TotalRollsStatistic(),

	// Middle Hits
	MiddleHitsStatistic(),
	LeftOfMiddleHitsStatistic(),
	RightOfMiddleHitsStatistic(),
	StrikeMiddleHitsStatistic(),

	// Strikes and Spares
	StrikesStatistic(),
	SpareConversionsStatistic(),

	// Head Pins
	HeadPinsStatistic(),
	HeadPinsSparedStatistic(),

	// Fives
	FivesStatistic(),
	FivesSparedStatistic(),
	LeftFivesStatistic(),
	LeftFivesSparedStatistic(),
	RightFivesStatistic(),
	RightFivesSparedStatistic(),

	// Threes
	ThreesStatistic(),
	ThreesSparedStatistic(),
	LeftThreesStatistic(),
	LeftThreesSparedStatistic(),
	RightThreesStatistic(),
	RightThreesSparedStatistic(),

	// Taps
	TapsStatistic(),
	TapsSparedStatistic(),
	LeftTapsStatistic(),
	LeftTapsSparedStatistic(),
	RightTapsStatistic(),
	RightTapsSparedStatistic(),

	// Aces
	AcesStatistic(),
	AcesSparedStatistic(),

	// Chop offs
	ChopOffsStatistic(),
	ChopOffsSparedStatistic(),
	LeftChopOffsStatistic(),
	LeftChopOffsSparedStatistic(),
	RightChopOffsStatistic(),
	RightChopOffsSparedStatistic(),

	// Splits
	SplitsStatistic(),
	SplitsSparedStatistic(),
	LeftSplitsStatistic(),
	LeftSplitsSparedStatistic(),
	RightSplitsStatistic(),
	RightSplitsSparedStatistic(),

	// Twelves
	TwelvesStatistic(),
	TwelvesSparedStatistic(),
	LeftTwelvesStatistic(),
	LeftTwelvesSparedStatistic(),
	RightTwelvesStatistic(),
	RightTwelvesSparedStatistic(),

	// Fouls
	FoulsStatistic(),

	// Pins Left
	TotalPinsLeftOnDeckStatistic(),
	AveragePinsLeftOnDeckStatistic(),

	// Match Play
	MatchesPlayedStatistic(),
	MatchesWonStatistic(),
	MatchesLostStatistic(),
	MatchesTiedStatistic(),

	// Series
	HighSeriesOf3Statistic(),
).filter {
	source == null || it.supportsSource(source)
}.filter {
	supportingWidgets == null || !supportingWidgets || it.supportsWidgets
}

fun widgetStatistics(): List<StatisticGroup> = allStatistics(supportingWidgets = true)
	.groupBy { it.category }
	.toSortedMap { o1, o2 -> o1.ordinal.compareTo(o2.ordinal) }
	.map { StatisticGroup(it.key.titleResourceId, it.value) }

fun statisticInstanceFromID(id: StatisticID): Statistic = allStatistics().first { it.id == id }
