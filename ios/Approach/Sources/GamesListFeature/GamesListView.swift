import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import ErrorsFeature
import FeatureActionLibrary
import GamesEditorFeature
import ModelsLibrary
import ResourceListLibrary
import SharingFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct GamesListView: View {
	let store: StoreOf<GamesList>

	struct ViewState: Equatable {
		let title: String
		let scores: [GamesListHeaderView.Score]
		let isSeriesSharingEnabled: Bool

		init(state: GamesList.State) {
			self.title = state.series.date.longFormat
			self.scores = state.list.resources?.map { .init(index: $0.index, score: $0.score) } ?? []
			self.isSeriesSharingEnabled = state.isSeriesSharingEnabled
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
			.toolbar {
				if viewStore.isSeriesSharingEnabled {
					ToolbarItem(placement: .navigationBarTrailing) {
						Button { viewStore.send(.didTapShareButton) } label: {
							Image(systemSymbol: .squareAndArrowUp)
						}
					}
				}
			}
			.navigationTitle(viewStore.title)
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GamesList.Destination.State.sharing,
			action: GamesList.Destination.Action.sharing,
			content: { store in
				SharingView(store: store)
			}
		)
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GamesList.Destination.State.editor,
			action: GamesList.Destination.Action.editor
		) { store in
			GamesEditorView(store: store)
		}
	}
}
