import ComposableArchitecture
import MatchPlaysRepositoryInterface
import ModelsLibrary

extension GamesEditor {
	func reduce(into state: inout State, gameDetailsAction: GameDetails.Action) -> Effect<Action> {
		switch gameDetailsAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didRequestOpponentPicker:
				let opponent = Set([state.game?.matchPlay?.opponent?.id].compactMap { $0 })
				state.destination = .opponentPicker(.init(
					selected: opponent,
					query: .init(()),
					limit: 1,
					showsCancelHeaderButton: false
				))
				return .none

			case .didRequestGearPicker:
				let gear = Set(state.game?.gear.map(\.id) ?? [])
				state.destination = .gearPicker(.init(
					selected: gear,
					query: .init(())
				))
				return .none

			case let .didEditMatchPlay(matchPlay):
				state.game?.matchPlay = matchPlay
				return save(matchPlay: state.game?.matchPlay)

			case let .didEditGame(game):
				state.game = game
				return save(game: state.game)

			case .didClearManualScore:
				return updateScoreSheet(from: state)
			}

		case .internal, .view:
			return .none
		}
	}
}
