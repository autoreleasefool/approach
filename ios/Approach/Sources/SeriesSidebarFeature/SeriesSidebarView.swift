import ComposableArchitecture
import DateTimeLibrary
import FeatureActionLibrary
import GamesEditorFeature
import ResourceListLibrary
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

public struct SeriesSidebarView: View {
	let store: StoreOf<SeriesSidebar>

	struct ViewState: Equatable {
		let title: String
		let selection: Game.ID?

		init(state: SeriesSidebar.State) {
			self.title = state.series.date.longFormat
			self.selection = state.selection?.id
		}
	}

	enum ViewAction {
		case setNavigation(selection: Game.ID?)
	}

	public init(store: StoreOf<SeriesSidebar>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SeriesSidebar.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /SeriesSidebar.Action.InternalAction.list)
			) { game in
				NavigationLink(
					destination: IfLetStore(
						store.scope(
							state: \.selection?.value,
							action: /SeriesSidebar.Action.InternalAction.editor
						)
					) {
						GamesEditorView(store: $0)
					},
					tag: game.id,
					selection: viewStore.binding(
						get: \.selection,
						send: SeriesSidebarView.ViewAction.setNavigation(selection:)
					)
				) {
					Text(Strings.Game.title(game.ordinal))
				}
			}
			.navigationTitle(viewStore.title)
		}
	}
}

extension SeriesSidebar.Action {
	init(action: SeriesSidebarView.ViewAction) {
		switch action {
		case let .setNavigation(selection):
			self = .view(.setNavigation(selection: selection))
		}
	}
}
