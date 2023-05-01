import ComposableArchitecture
import DateTimeLibrary
import FeatureActionLibrary
import GamesEditorFeature
import ModelsLibrary
import ResourceListLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GamesListView: View {
	let store: StoreOf<GamesList>

	struct ViewState: Equatable {
		let title: String
		let selection: Game.ID?
		let isLoadingGameDetails: Bool

		init(state: GamesList.State) {
			self.title = state.series.date.longFormat
			self.selection = state.selection?.id
			self.isLoadingGameDetails = state.isLoadingGameDetails
		}
	}

	enum ViewAction {
		case setNavigation(selection: Game.ID?)
	}

	public init(store: StoreOf<GamesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamesList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /GamesList.Action.InternalAction.list)
			) { game in
				NavigationLink(
					destination: IfLetStore(
						store.scope(
							state: \.selection?.value,
							action: /GamesList.Action.InternalAction.editor
						)
					) {
						GamesEditorView(store: $0)
					},
					tag: game.id,
					selection: viewStore.binding(
						get: \.selection,
						send: GamesListView.ViewAction.setNavigation(selection:)
					)
				) {
					Text(Strings.Game.title(game.index + 1))
				}
			} header: {
				if viewStore.isLoadingGameDetails {
					ListProgressView()
				}
			}
			.navigationTitle(viewStore.title)
		}
	}
}

extension GamesList.Action {
	init(action: GamesListView.ViewAction) {
		switch action {
		case let .setNavigation(selection):
			self = .view(.setNavigation(selection: selection))
		}
	}
}
