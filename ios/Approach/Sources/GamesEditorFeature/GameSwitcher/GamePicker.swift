import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

public struct GamePicker: ReducerProtocol {
	public struct State: Equatable {
		public let games: IdentifiedArrayOf<Game>
		public var selected: Game.ID

		init(games: IdentifiedArrayOf<Game>, selected: Game.ID) {
			self.games = games
			self.selected = selected
		}
	}

	public enum Action: Equatable {
		public enum ViewAction: Equatable {
			case didTapGame(Game.ID)
			case didTapCancelButton
		}
		public enum DelegateAction: Equatable {
			case didFinish
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	init() {}

	public var body: some ReducerProtocol<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapGame(id):
					state.selected = id
					return .task { .delegate(.didFinish) }

				case .didTapCancelButton:
					return .task { .delegate(.didFinish) }
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

struct GamePickerView: View {
	let store: StoreOf<GamePicker>

	enum ViewAction {
		case didTapGame(Game.ID)
		case didTapCancelButton
	}

	init(store: StoreOf<GamePicker>) {
		self.store = store
	}

	var body: some View {
		WithViewStore(store, observe: { $0 }, send: GamePicker.Action.init) { viewStore in
			List(viewStore.games) { game in
				Button(Strings.Game.title(game.ordinal)) { viewStore.send(.didTapGame(game.id)) }
			}
			.navigationTitle(Strings.Game.Editor.Picker.switch)
			.toolbar {
				ToolbarItem(placement: .navigationBarLeading) {
					Button("Cancel") { viewStore.send(.didTapCancelButton) }
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
		case .didTapCancelButton:
			self = .view(.didTapCancelButton)
		}
	}
}
