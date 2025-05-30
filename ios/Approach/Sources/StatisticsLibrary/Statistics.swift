extension Statistics {
	public static let allCases: [Statistic.Type] = [
		// Overall
		Statistics.HighSingle.self,
		Statistics.TotalPinfall.self,
		Statistics.NumberOfGames.self,
		Statistics.GameAverage.self,
		Statistics.TotalRolls.self,

		// Middle Hits
		Statistics.MiddleHits.self,
		Statistics.LeftOfMiddleHits.self,
		Statistics.RightOfMiddleHits.self,
		Statistics.StrikeMiddleHits.self,

		// Strikes and Spares
		Statistics.Strikes.self,
		Statistics.SpareConversions.self,

		// First Rolls
		Statistics.FirstRollAverage.self,

		// Head Pins
		Statistics.HeadPins.self,
		Statistics.HeadPinsSpared.self,

		// Fives
		Statistics.Fives.self,
		Statistics.FivesSpared.self,
		Statistics.LeftFives.self,
		Statistics.LeftFivesSpared.self,
		Statistics.RightFives.self,
		Statistics.RightFivesSpared.self,

		// Threes
		Statistics.Threes.self,
		Statistics.ThreesSpared.self,
		Statistics.LeftThrees.self,
		Statistics.LeftThreesSpared.self,
		Statistics.RightThrees.self,
		Statistics.RightThreesSpared.self,

		// Taps
		Statistics.Taps.self,
		Statistics.TapsSpared.self,
		Statistics.LeftTaps.self,
		Statistics.LeftTapsSpared.self,
		Statistics.RightTaps.self,
		Statistics.RightTapsSpared.self,

		// Aces
		Statistics.Aces.self,
		Statistics.AcesSpared.self,

		// Chop Offs
		Statistics.ChopOffs.self,
		Statistics.ChopOffsSpared.self,
		Statistics.LeftChopOffs.self,
		Statistics.LeftChopOffsSpared.self,
		Statistics.RightChopOffs.self,
		Statistics.RightChopOffsSpared.self,

		// Splits
		Statistics.Splits.self,
		Statistics.SplitsSpared.self,
		Statistics.LeftSplits.self,
		Statistics.LeftSplitsSpared.self,
		Statistics.RightSplits.self,
		Statistics.RightSplitsSpared.self,

		// Twelves
		Statistics.Twelves.self,
		Statistics.TwelvesSpared.self,
		Statistics.LeftTwelves.self,
		Statistics.LeftTwelvesSpared.self,
		Statistics.RightTwelves.self,
		Statistics.RightTwelvesSpared.self,

		// Fouls
		Statistics.Fouls.self,

		// Pins Left
		Statistics.TotalPinsLeftOnDeck.self,
		Statistics.AveragePinsLeftOnDeck.self,

		// Match Play
		Statistics.MatchesPlayed.self,
		Statistics.MatchesWon.self,
		Statistics.MatchesLost.self,
		Statistics.MatchesTied.self,

		// Series
		Statistics.HighSeriesOf2.self,
		Statistics.HighSeriesOf3.self,
		Statistics.HighSeriesOf4.self,
		Statistics.HighSeriesOf5.self,
		Statistics.HighSeriesOf8.self,
		Statistics.HighSeriesOf10.self,
		Statistics.HighSeriesOf12.self,
		Statistics.HighSeriesOf15.self,
		Statistics.HighSeriesOf20.self,
	]

	public static func all(forSource trackableSource: TrackableFilter.Source) -> [Statistic.Type] {
		allCases.filter { $0.supports(trackableSource: trackableSource) }
	}

	public static func supportingWidgets() -> [Statistic.Type] {
		allCases.filter { $0.supportsWidgets }
	}

	public static func type(of id: String, fallbackResolver: ((String) -> Statistic.Type?)? = nil) -> Statistic.Type? {
		Self.allCases.first(where: { $0.title == id }) ?? fallbackResolver?(id)
	}
}
