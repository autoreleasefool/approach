import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import GamesListFeature
import ModelsLibrary
import ResourceListLibrary
import SeriesEditorFeature
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct SeriesListView: View {
	let store: StoreOf<SeriesList>

	struct ViewState: Equatable {
		let leagueName: String
		let selection: Series.ID?

		init(state: SeriesList.State) {
			self.leagueName = state.league.name
			self.selection = state.selection?.id
		}
	}

	enum ViewAction {
		case setNavigation(selection: Series.ID?)
	}

	public init(store: StoreOf<SeriesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SeriesList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /SeriesList.Action.InternalAction.list)
			) { series in
				NavigationLink(
					destination: IfLetStore(
						store.scope(state: \.selection?.value, action: /SeriesList.Action.InternalAction.sidebar)
					) {
						GamesListView(store: $0)
					},
					tag: series.id,
					selection: viewStore.binding(
						get: \.selection,
						send: SeriesListView.ViewAction.setNavigation(selection:)
					)
				) {
					Text(series.name)
				}
			}
			.navigationTitle(viewStore.leagueName)
			.sheet(store: store.scope(state: \.$editor, action: { .internal(.editor($0)) })) { scopedStore in
				NavigationView {
					SeriesEditorView(store: scopedStore)
				}
			}
		}
	}
}

extension SeriesList.Action {
	init(action: SeriesListView.ViewAction) {
		switch action {
		case let .setNavigation(selection):
			self = .view(.setNavigation(selection: selection))
		}
	}
}
