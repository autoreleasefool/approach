import ComposableArchitecture
import StatisticsLibrary

extension StatisticsSourcePicker.State {
	var source: TrackableFilter.Source? {
		if let game {
			return .game(game.id)
		} else if let series {
			return .series(series.id)
		} else if let league {
			return .league(league.id)
		} else if let bowler {
			return .bowler(bowler.id)
		} else {
			return nil
		}
	}
}
