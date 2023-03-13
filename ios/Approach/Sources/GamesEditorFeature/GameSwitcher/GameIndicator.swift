import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GameIndicator: ReducerProtocol {
	public struct State: Equatable {
		public let games: [Game.ID]
		public var selected: Game.ID

		var selectedOrdinal: Int { games.firstIndex(of: selected)! + 1 }

		init(games: [Game.ID], selected: Game.ID) {
			self.games = games
			self.selected = selected
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapIndicator
			case didTapGame(Game.ID)
		}
		public enum DelegateAction: Equatable {
			case didRequestGamePicker
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
				case .didTapIndicator:
					return .task { .delegate(.didRequestGamePicker) }

				case let .didTapGame(id):
					state.selected = id
					return .none
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

struct GameIndicatorView: View {
	let store: StoreOf<GameIndicator>

	enum ViewAction {
		case didTapIndicator
		case didTapGame(Game.ID)
	}

	init(store: StoreOf<GameIndicator>) {
		self.store = store
	}

	var body: some View {
		WithViewStore(store, observe: { $0 }, send: GameIndicator.Action.init) { viewStore in
			Button { viewStore.send(.didTapIndicator) } label: {
				HStack(alignment: .center, spacing: .smallSpacing) {
					Text(Strings.Game.title(viewStore.selectedOrdinal))
						.font(.caption)

					if viewStore.games.count > 1 {
						Image(systemName: "chevron.down.circle.fill")
							.resizable()
							.aspectRatio(contentMode: .fit)
							.frame(width: .extraTinyIcon, height: .extraTinyIcon)
					}
				}
			}
			.buttonStyle(TappableElement())
			.disabled(viewStore.games.count == 1)
		}
	}
}

extension GameIndicator.Action {
	init(action: GameIndicatorView.ViewAction) {
		switch action {
		case .didTapIndicator:
			self = .view(.didTapIndicator)
		case let .didTapGame(id):
			self = .view(.didTapGame(id))
		}
	}
}
