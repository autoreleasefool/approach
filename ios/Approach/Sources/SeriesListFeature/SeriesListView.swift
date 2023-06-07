import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import GamesListFeature
import ModelsLibrary
import ResourceListLibrary
import SeriesEditorFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct SeriesListView: View {
	let store: StoreOf<SeriesList>

	struct ViewState: Equatable {
		let leagueName: String

		init(state: SeriesList.State) {
			self.leagueName = state.league.name
		}
	}

	enum ViewAction {
		case didTapSeres(Series.ID)
	}

	public init(store: StoreOf<SeriesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SeriesList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /SeriesList.Action.InternalAction.list)
			) { series in
				Button { viewStore.send(.didTapSeres(series.id)) } label: {
					Text(series.name)
				}
				.buttonStyle(.navigation)
			}
			.navigationTitle(viewStore.leagueName)
			.sheet(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /SeriesList.Destination.State.editor,
				action: SeriesList.Destination.Action.editor
			) { store in
				NavigationStack {
					SeriesEditorView(store: store)
				}
			}
			.navigationDestination(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /SeriesList.Destination.State.games,
				action: SeriesList.Destination.Action.games
			) { store in
				GamesListView(store: store)
			}
		}
	}
}

extension SeriesList.Action {
	init(action: SeriesListView.ViewAction) {
		switch action {
		case let .didTapSeres(id):
			self = .view(.didTapSeries(id))
		}
	}
}
