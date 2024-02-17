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

@ViewAction(for: SeriesList.self)
public struct SeriesListView: View {
	@Perception.Bindable public var store: StoreOf<SeriesList>

	public init(store: StoreOf<SeriesList>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			SectionResourceListView(
				store: store.scope(state: \.list, action: \.internal.list)
			) { _, series in
				Button { send(.didTapSeries(series.id)) } label: {
					SeriesListItem(series: series)
				}
				.buttonStyle(.plain)
				.listRowInsets(EdgeInsets())
				.alignmentGuide(.listRowSeparatorLeading) { d in
						d[.leading]
				}
			}
			.navigationTitle(store.league.name)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					EditButton { send(.didTapEditButton) }
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					SortButton(isActive: false) { send(.didTapSortOrderButton) }
				}
			}
			.onAppear { send(.onAppear) }
		}
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.seriesEditor($store.scope(state: \.destination?.seriesEditor, action: \.internal.destination.seriesEditor))
		.leagueEditor($store.scope(state: \.destination?.leagueEditor, action: \.internal.destination.leagueEditor))
		.sortOrder($store.scope(state: \.destination?.sortOrder, action: \.internal.destination.sortOrder))
		.gamesList($store.scope(state: \.destination?.games, action: \.internal.destination.games))
	}
}

@MainActor extension View {
	fileprivate func seriesEditor(_ store: Binding<StoreOf<SeriesEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<SeriesEditor>) in
			NavigationStack {
				SeriesEditorView(store: store)
			}
		}
	}

	fileprivate func leagueEditor(_ store: Binding<StoreOf<LeagueEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<LeagueEditor>) in
			NavigationStack {
				LeagueEditorView(store: store)
			}
		}
	}

	fileprivate func sortOrder(_ store: Binding<StoreOf<SortOrderLibrary.SortOrder<Series.Ordering>>?>) -> some View {
		sheet(item: store) { (store: StoreOf<SortOrderLibrary.SortOrder<Series.Ordering>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}

	fileprivate func gamesList(_ store: Binding<StoreOf<GamesList>?>) -> some View {
		navigationDestinationWrapper(item: store) { (store: StoreOf<GamesList>) in
			GamesListView(store: store)
		}
	}
}
