import ComposableArchitecture
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct GamesSettings: Reducer {
	public struct State: Equatable {
		public var game: Game.Edit

		init(game: Game.Edit) {
			self.game = game
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {}
		public enum DelegateAction: Equatable {
			case didFinish
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .never:
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

// MARK: - View

public struct GamesSettingsView: View {
	let store: StoreOf<GamesSettings>

	struct ViewState: Equatable {
		init(state: GamesSettings.State) {}
	}

	enum ViewAction {
		case didTapDone
	}

	init(store: StoreOf<GamesSettings>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamesSettings.Action.init) { viewStore in
			EmptyView()
				.navigationTitle(Strings.Game.Settings.title)
				.toolbar {
					ToolbarItem(placement: .navigationBarLeading) {
						Button(Strings.Action.done) { viewStore.send(.didTapDone) }
					}
				}
		}
	}
}

extension GamesSettings.Action {
	init(action: GamesSettingsView.ViewAction) {
		switch action {
		case .didTapDone:
			self = .delegate(.didFinish)
		}
	}
}
