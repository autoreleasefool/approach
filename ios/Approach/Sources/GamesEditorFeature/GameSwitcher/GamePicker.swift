import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GameIndex: Hashable, Identifiable {
	public let id: Game.ID
	public let index: Int
}

public struct GamePicker: Reducer {
	public struct State: Equatable {
		public var selected: Game.ID
		public let indexedGames: [GameIndex]

		init(games: [Game.ID], selected: Game.ID) {
			self.selected = selected
			self.indexedGames = games.enumerated().map { .init(id: $0.element, index: $0.offset) }
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

	public var body: some Reducer<State, Action> {
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
		WithViewStore(store, observe: { $0 }, send: GamePicker.Action.init, content: { viewStore in
			List(viewStore.indexedGames) { indexedGame in
				Button { viewStore.send(.didTapGame(indexedGame.id)) } label: {
					HStack {
						Label(
							Strings.Game.title(indexedGame.index + 1),
							systemImage: viewStore.selected == indexedGame.id ? "smallcircle.filled.circle" : "circle"
						)
						.foregroundColor(.appAction)
						Spacer()
						Text("156") // TODO: show game score
					}
					.contentShape(Rectangle())
				}
				.buttonStyle(TappableElement())
			}
			.navigationTitle(Strings.Game.Editor.Picker.switch)
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarLeading) {
					Button(Strings.Action.cancel) { viewStore.send(.didTapCancelButton) }
				}
			}
		})
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
