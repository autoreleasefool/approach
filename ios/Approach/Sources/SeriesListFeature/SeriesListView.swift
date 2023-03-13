import ComposableArchitecture
import DateTimeLibrary
import GamesListFeature
import ResourceListLibrary
import SeriesEditorFeature
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI
import AssetsLibrary
import ViewsLibrary

public struct SeriesListView: View {
	let store: StoreOf<SeriesList>

	struct ViewState: Equatable {
		let leagueName: String
		let selection: Series.ID?
		let isEditorPresented: Bool

		init(state: SeriesList.State) {
			self.leagueName = state.league.name
			self.selection = state.selection?.id
			self.isEditorPresented = state.editor != nil
		}
	}

	enum ViewAction {
		case setNavigation(selection: Series.ID?)
		case setEditorSheet(isPresented: Bool)
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
					SeriesRow(series: series)
				}
			}
			.navigationTitle(viewStore.leagueName)
			.sheet(isPresented: viewStore.binding(
				get: \.isEditorPresented,
				send: ViewAction.setEditorSheet(isPresented: false)
			)) {
				IfLetStore(store.scope(state: \.editor, action: /SeriesList.Action.InternalAction.editor)) { scopedStore in
					NavigationView {
						SeriesEditorView(store: scopedStore)
					}
				}
			}
		}
	}
}

extension SeriesList.Action {
	init(action: SeriesListView.ViewAction) {
		switch action {
		case let .setEditorSheet(isPresented):
			self = .view(.setEditorSheet(isPresented: isPresented))
		case let .setNavigation(selection):
			self = .view(.setNavigation(selection: selection))
		}
	}
}
