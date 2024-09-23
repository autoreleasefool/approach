import ComposableArchitecture
import StringsLibrary

extension GamesEditor {
	func reduce(
		into state: inout State,
		duplicateLanesAction: GamesEditor.Destination.DuplicateLanesAlertAction
	) -> Effect<Action> {
		switch duplicateLanesAction {
		case .confirmDuplicateLanes:
			let currentGame = state.currentGameId
			let otherGames = state.bowlerGameIds.flatMap { $0.value }.filter { $0 != currentGame }
			return .run { _ in
				try await games.duplicateLanes(from: currentGame, toAllGames: otherGames)
			} catch: { error, send in
				await send(.internal(.didDuplicateLanes(.failure(error))))
			}

		case .didTapDismissButton:
			state.destination = .gameDetails(.init(
				gameId: state.currentGameId,
				seriesGames: state.currentBowlerGames,
				nextHeaderElement: state.nextHeaderElement,
				didChangeBowler: false
			))
			return .none
		}
	}
}

extension AlertState where Action == GamesEditor.Destination.DuplicateLanesAlertAction {
	static var duplicateLanes: Self {
		Self {
			TextState(Strings.Game.Editor.Fields.Alley.Lanes.Duplicate.title)
		} actions: {
			ButtonState(action: .confirmDuplicateLanes) {
				TextState(Strings.Game.Editor.Fields.Alley.Lanes.Duplicate.copyToAll)
			}
			
			ButtonState(role: .cancel, action: .didTapDismissButton) {
				TextState(Strings.Game.Editor.Fields.Alley.Lanes.Duplicate.dismiss)
			}
		} message: {
			TextState(Strings.Game.Editor.Fields.Alley.Lanes.Duplicate.message)
		}
	}
}
