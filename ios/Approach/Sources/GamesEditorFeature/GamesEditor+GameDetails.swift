import ComposableArchitecture
import MatchPlaysRepositoryInterface
import ModelsLibrary
import StringsLibrary

extension GamesEditor {
	func reduce(into state: inout State, gameDetailsAction: GameDetails.Action) -> Effect<Action> {
		switch gameDetailsAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didRequestOpponentPicker:
				guard state.isEditable else { return .none }
				let opponent = Set([state.game?.matchPlay?.opponent?.id].compactMap { $0 })
				state.destination = .opponentPicker(.init(
					selected: opponent,
					query: .init(()),
					limit: 1,
					showsCancelHeaderButton: false
				))
				return .none

			case .didRequestGearPicker:
				guard state.isEditable else { return .none }
				let gear = Set(state.game?.gear.map(\.id) ?? [])
				state.destination = .gearPicker(.init(
					selected: gear,
					query: .init(())
				))
				return .none

			case .didRequestLanePicker:
				guard state.isEditable else { return .none }
				guard let alleyId = state.game?.series.alley?.id else { return .none }
				let lanes = Set(state.game?.lanes.map(\.id) ?? [])
				state.destination = .lanePicker(.init(
					selected: lanes,
					query: alleyId
				))
				return .none

			case let .didEditMatchPlay(.success(matchPlay)):
				state.game?.matchPlay = matchPlay
				return save(matchPlay: state.game?.matchPlay)

			case let .didEditMatchPlay(.failure(error)):
				return state.errors
					.enqueue(.failedToSaveMatchPlay, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
					.map { .internal(.errors($0)) }

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
