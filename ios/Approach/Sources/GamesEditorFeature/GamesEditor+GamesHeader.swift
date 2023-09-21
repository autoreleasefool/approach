import ComposableArchitecture

extension GamesEditor.State {
	var gamesHeader: GamesHeader.State {
		get {
			var gamesHeader = _gamesHeader
			gamesHeader.currentGameIndex = currentGameIndex
			return gamesHeader
		}
		set {
			_gamesHeader = newValue
		}
	}
}

extension GamesEditor {
	func reduce(into state: inout State, gamesHeaderAction: GamesHeader.Action) -> Effect<Action> {
		switch gamesHeaderAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didCloseEditor:
				return .run { _ in await dismiss() }

			case .didOpenSettings:
				guard let bowlers = state.bowlers else { return .none }
				state.destination = .sheets(.settings(.init(
					bowlers: bowlers,
					currentBowlerId: state.currentBowlerId,
					numberOfGames: state.numberOfGames,
					gameIndex: state.currentGameIndex
				)))
				return .none

			case .didShareGame:
				state.destination = .sheets(.sharing(.init(dataSource: .games([state.currentGameId]))))
				return .none
			}

		case .view, .internal:
			return .none
		}
	}
}
