import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import ErrorsFeature
import ExtensionsPackageLibrary
import GamesListFeature
import LeagueEditorFeature
import ModelsLibrary
import ResourceListLibrary
import SeriesEditorFeature
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@ViewAction(for: SeriesList.self)
public struct SeriesListView: View {
	@Bindable public var store: StoreOf<SeriesList>

	public init(store: StoreOf<SeriesList>) {
		self.store = store
	}

	public var body: some View {
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
		} header: {
			if store.hasPreBowls {
				Section {
					Button { send(.didTapUpdatePreBowlsButton) } label: {
						Text(Strings.Series.List.PreBowl.usedAPreBowl)
					}
					.buttonStyle(.navigation)
				}
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
		.modifier(DestinationModifier(store: store))
	}
}

private struct DestinationModifier: ViewModifier {
	@Bindable var store: StoreOf<SeriesList>

	func body(content: Content) -> some View {
		content
			.errors(store: store.scope(state: \.errors, action: \.internal.errors))
			.seriesEditor($store.scope(state: \.destination?.seriesEditor, action: \.internal.destination.seriesEditor))
			.leagueEditor($store.scope(state: \.destination?.leagueEditor, action: \.internal.destination.leagueEditor))
			.sortOrder($store.scope(state: \.destination?.sortOrder, action: \.internal.destination.sortOrder))
			.gamesList($store.scope(state: \.destination?.games, action: \.internal.destination.games))
			.preBowl($store.scope(state: \.destination?.preBowl, action: \.internal.destination.preBowl))
	}
}

extension View {
	fileprivate func seriesEditor(_ store: Binding<StoreOf<SeriesEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<SeriesEditor>) in
			NavigationStack {
				SeriesEditorView(store: store)
			}
		}
	}

	fileprivate func preBowl(_ store: Binding<StoreOf<SeriesPreBowlEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<SeriesPreBowlEditor>) in
			NavigationStack {
				SeriesPreBowlEditorView(store: store)
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
		navigationDestination(item: store) { (store: StoreOf<GamesList>) in
			GamesListView(store: store)
		}
	}
}
