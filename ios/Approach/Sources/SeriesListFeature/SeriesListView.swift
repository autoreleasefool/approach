import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import ErrorsFeature
import ExtensionsLibrary
import GamesListFeature
import LeagueEditorFeature
import ModelsLibrary
import ResourceListLibrary
import SeriesEditorFeature
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct SeriesListView: View {
	let store: StoreOf<SeriesList>

	struct ViewState: Equatable {
		let leagueName: String
		let ordering: Series.Ordering

		init(state: SeriesList.State) {
			self.leagueName = state.league.name
			self.ordering = state.ordering
		}
	}

	public init(store: StoreOf<SeriesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			SectionResourceListView(
				store: store.scope(state: \.list, action: \.internal.list)
			) { _, series in
				Button { viewStore.send(.didTapSeries(series.id)) } label: {
					SeriesListItem(series: series)
				}
				.buttonStyle(.plain)
				.listRowInsets(EdgeInsets())
				.alignmentGuide(.listRowSeparatorLeading) { d in
						d[.leading]
				}
			}
			.navigationTitle(viewStore.leagueName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					EditButton { viewStore.send(.didTapEditButton) }
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					SortButton(isActive: false) { viewStore.send(.didTapSortOrderButton) }
				}
			}
			.onAppear { viewStore.send(.onAppear) }
		})
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.seriesEditor(store.scope(state: \.$destination.seriesEditor, action: \.internal.destination.seriesEditor))
		.leagueEditor(store.scope(state: \.$destination.leagueEditor, action: \.internal.destination.leagueEditor))
		.sortOrder(store.scope(state: \.$destination.sortOrder, action: \.internal.destination.sortOrder))
		.gamesList(store.scope(state: \.$destination.games, action: \.internal.destination.games))
	}
}

@MainActor extension View {
	fileprivate func seriesEditor(_ store: PresentationStoreOf<SeriesEditor>) -> some View {
		sheet(store: store) { (store: StoreOf<SeriesEditor>) in
			NavigationStack {
				SeriesEditorView(store: store)
			}
		}
	}

	fileprivate func leagueEditor(_ store: PresentationStoreOf<LeagueEditor>) -> some View {
		sheet(store: store) { (store: StoreOf<LeagueEditor>) in
			NavigationStack {
				LeagueEditorView(store: store)
			}
		}
	}

	fileprivate func sortOrder(_ store: PresentationStoreOf<SortOrderLibrary.SortOrder<Series.Ordering>>) -> some View {
		sheet(store: store) { (store: StoreOf<SortOrderLibrary.SortOrder<Series.Ordering>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}

	fileprivate func gamesList(_ store: PresentationStoreOf<GamesList>) -> some View {
		navigationDestination(store: store) { (store: StoreOf<GamesList>) in
			GamesListView(store: store)
		}
	}
}
