import ComposableArchitecture
import DateTimeLibrary
import FeatureActionLibrary
import GamesEditorFeature
import ModelsLibrary
import ResourceListLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct GamesListView: View {
	let store: StoreOf<GamesList>

	struct ViewState: Equatable {
		let title: String

		init(state: GamesList.State) {
			self.title = state.series.date.longFormat
		}
	}

	enum ViewAction {
		case didTapGame(Game.ID)
	}

	public init(store: StoreOf<GamesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamesList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /GamesList.Action.InternalAction.list)
			) { game in
				Button { viewStore.send(.didTapGame(game.id)) } label: {
					LabeledContent(Strings.Game.titleWithOrdinal(game.index + 1), value: "\(game.score)")
				}
				.buttonStyle(.navigation)
			}
			.navigationTitle(viewStore.title)
		}
		.navigationDestination(store: store.scope(state: \.$editor, action: { .internal(.editor($0)) })) { store in
			GamesEditorView(store: store)
		}
	}
}

extension GamesList.Action {
	init(action: GamesListView.ViewAction) {
		switch action {
		case let .didTapGame(id):
			self = .view(.didTapGame(id))
		}
	}
}
