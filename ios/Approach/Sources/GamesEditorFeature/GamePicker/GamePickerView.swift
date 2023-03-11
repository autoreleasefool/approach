import AssetsLibrary
import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GamePickerView: View {
	let store: StoreOf<GamePicker>

	struct ViewState: Equatable {
		let games: IdentifiedArrayOf<Game>
		let selected: Game.ID

		init(state: GamePicker.State) {
			self.games = state.games
			self.selected = state.selected
		}
	}

	enum ViewAction {
		case didTapGame(Game.ID)
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamePicker.Action.init) { viewStore in
			ScrollView(.horizontal, showsIndicators: false) {
				HStack(alignment: .center, spacing: .standardSpacing) {
					ForEach(viewStore.games) { game in
						Button { viewStore.send(.didTapGame(game.id)) } label: {
							Text(Strings.Game.title(game.ordinal))
								.font(.caption)
						}
						.buttonStyle(TappableElement())
					}
				}
			}
		}
	}
}

extension GamePicker.Action {
	init(action: GamePickerView.ViewAction) {
		switch action {
		case let .didTapGame(id):
			self = .view(.didTapGame(id))
		}
	}
}
