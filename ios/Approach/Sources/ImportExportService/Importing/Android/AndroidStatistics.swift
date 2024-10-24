import StatisticsLibrary

enum AndroidStatistic: String, CaseIterable {
	case HIGH_SINGLE_GAME
	case TOTAL_PIN_FALL
	case NUMBER_OF_GAMES
	case GAME_AVERAGE
	case TOTAL_ROLLS

	case MIDDLE_HITS
	case LEFT_OF_MIDDLE_HITS
	case RIGHT_OF_MIDDLE_HITS
	case STRIKE_MIDDLE_HITS

	case STRIKES
	case SPARE_CONVERSIONS

	case AVERAGE_FIRST_ROLL

	case HEAD_PINS
	case HEAD_PINS_SPARED

	case FIVES
	case FIVES_SPARED
	case LEFT_FIVES
	case LEFT_FIVES_SPARED
	case RIGHT_FIVES
	case RIGHT_FIVES_SPARED

	case THREES
	case THREES_SPARED
	case LEFT_THREES
	case LEFT_THREES_SPARED
	case RIGHT_THREES
	case RIGHT_THREES_SPARED

	case TAPS
	case TAPS_SPARED
	case LEFT_TAPS
	case LEFT_TAPS_SPARED
	case RIGHT_TAPS
	case RIGHT_TAPS_SPARED

	case ACES
	case ACES_SPARED

	case CHOPS
	case CHOPS_SPARED
	case LEFT_CHOPS
	case LEFT_CHOPS_SPARED
	case RIGHT_CHOPS
	case RIGHT_CHOPS_SPARED

	case SPLITS
	case SPLITS_SPARED
	case LEFT_SPLITS
	case LEFT_SPLITS_SPARED
	case RIGHT_SPLITS
	case RIGHT_SPLITS_SPARED

	case TWELVES
	case TWELVES_SPARED
	case LEFT_TWELVES
	case LEFT_TWELVES_SPARED
	case RIGHT_TWELVES
	case RIGHT_TWELVES_SPARED

	case FOULS

	case TOTAL_PINS_LEFT_ON_DECK
	case AVERAGE_PINS_LEFT_ON_DECK

	case MATCHES_PLAYED
	case MATCHES_WON
	case MATCHES_LOST
	case MATCHES_TIED

	case HIGH_SERIES_OF_3

	var statistic: Statistic.Type {
		switch self {
		case .ACES: Statistics.Aces.self
		case .ACES_SPARED: Statistics.AcesSpared.self
		case .CHOPS: Statistics.ChopOffs.self
		case .CHOPS_SPARED: Statistics.ChopOffsSpared.self
		case .FIVES: Statistics.Fives.self
		case .FIVES_SPARED: Statistics.FivesSpared.self
		case .HEAD_PINS: Statistics.HeadPins.self
		case .HEAD_PINS_SPARED: Statistics.HeadPinsSpared.self
		case .LEFT_CHOPS: Statistics.LeftChopOffs.self
		case .LEFT_CHOPS_SPARED: Statistics.LeftChopOffsSpared.self
		case .LEFT_FIVES: Statistics.LeftFives.self
		case .LEFT_FIVES_SPARED: Statistics.LeftFivesSpared.self
		case .LEFT_SPLITS: Statistics.LeftSplits.self
		case .LEFT_SPLITS_SPARED: Statistics.LeftSplitsSpared.self
		case .LEFT_TAPS: Statistics.LeftTaps.self
		case .LEFT_TAPS_SPARED: Statistics.LeftTapsSpared.self
		case .LEFT_THREES: Statistics.LeftThrees.self
		case .LEFT_THREES_SPARED: Statistics.LeftThreesSpared.self
		case .LEFT_TWELVES: Statistics.LeftTwelves.self
		case .LEFT_TWELVES_SPARED: Statistics.LeftTwelvesSpared.self
		case .RIGHT_CHOPS: Statistics.RightChopOffs.self
		case .RIGHT_CHOPS_SPARED: Statistics.RightChopOffsSpared.self
		case .RIGHT_FIVES: Statistics.RightFives.self
		case .RIGHT_FIVES_SPARED: Statistics.RightFivesSpared.self
		case .RIGHT_SPLITS: Statistics.RightSplits.self
		case .RIGHT_SPLITS_SPARED: Statistics.RightSplitsSpared.self
		case .RIGHT_TAPS: Statistics.RightTaps.self
		case .RIGHT_TAPS_SPARED: Statistics.RightTapsSpared.self
		case .RIGHT_THREES: Statistics.RightThrees.self
		case .RIGHT_THREES_SPARED: Statistics.RightThreesSpared.self
		case .RIGHT_TWELVES: Statistics.RightTwelves.self
		case .RIGHT_TWELVES_SPARED: Statistics.RightTwelvesSpared.self
		case .SPLITS: Statistics.Splits.self
		case .SPLITS_SPARED: Statistics.SplitsSpared.self
		case .TAPS: Statistics.Taps.self
		case .TAPS_SPARED: Statistics.TapsSpared.self
		case .THREES: Statistics.Threes.self
		case .THREES_SPARED: Statistics.ThreesSpared.self
		case .TWELVES: Statistics.Twelves.self
		case .TWELVES_SPARED: Statistics.TwelvesSpared.self
		case .FOULS: Statistics.Fouls.self
		case .SPARE_CONVERSIONS: Statistics.SpareConversions.self
		case .STRIKES: Statistics.Strikes.self
		case .MATCHES_LOST: Statistics.MatchesLost.self
		case .MATCHES_WON: Statistics.MatchesWon.self
		case .MATCHES_TIED: Statistics.MatchesTied.self
		case .MATCHES_PLAYED: Statistics.MatchesPlayed.self
		case .LEFT_OF_MIDDLE_HITS: Statistics.LeftOfMiddleHits.self
		case .RIGHT_OF_MIDDLE_HITS: Statistics.RightOfMiddleHits.self
		case .MIDDLE_HITS: Statistics.MiddleHits.self
		case .STRIKE_MIDDLE_HITS: Statistics.StrikeMiddleHits.self
		case .GAME_AVERAGE: Statistics.GameAverage.self
		case .HIGH_SINGLE_GAME: Statistics.HighSingle.self
		case .NUMBER_OF_GAMES: Statistics.NumberOfGames.self
		case .TOTAL_PIN_FALL: Statistics.TotalPinfall.self
		case .TOTAL_ROLLS: Statistics.TotalRolls.self
		case .AVERAGE_PINS_LEFT_ON_DECK: Statistics.AveragePinsLeftOnDeck.self
		case .TOTAL_PINS_LEFT_ON_DECK: Statistics.TotalPinsLeftOnDeck.self
		case .AVERAGE_FIRST_ROLL: Statistics.FirstRollAverage.self
		case .HIGH_SERIES_OF_3: Statistics.HighSeriesOf3.self
		}
	}
}
