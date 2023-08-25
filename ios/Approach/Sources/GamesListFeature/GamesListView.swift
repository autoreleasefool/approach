import ComposableArchitecture
import DateTimeLibrary
import ErrorsFeature
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
		let scores: [GamesListHeaderView.Score]

		init(state: GamesList.State) {
			self.title = state.series.date.longFormat
			self.scores = state.list.resources?.map { .init(index: $0.index, score: $0.score) } ?? []
		}
	}

	public init(store: StoreOf<GamesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /GamesList.Action.InternalAction.list)
			) { game in
				Button { viewStore.send(.didTapGame(game.id)) } label: {
					LabeledContent(Strings.Game.titleWithOrdinal(game.index + 1), value: "\(game.score)")
				}
				.buttonStyle(.navigation)
			} header: {
				GamesListHeaderView(scores: viewStore.scores)
			}
			.navigationTitle(viewStore.title)
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.navigationDestination(store: store.scope(state: \.$editor, action: { .internal(.editor($0)) })) { store in
			GamesEditorView(store: store)
		}
	}
}
