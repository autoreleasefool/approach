import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import ExtensionsPackageLibrary
import GamesListFeature
import LeagueEditorFeature
import ModelsLibrary
import ResourceListLibrary
import SeriesListFeature
import SortOrderLibrary
import StatisticsWidgetsLayoutFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@ViewAction(for: LeaguesList.self)
public struct LeaguesListView: View {
	@Bindable public var store: StoreOf<LeaguesList>

	public init(store: StoreOf<LeaguesList>) {
		self.store = store
	}

	public var body: some View {
		SectionResourceListView(
			store: store.scope(state: \.list, action: \.internal.list)
		) { _, league in
			Button { send(.didTapLeague(id: league.id)) } label: {
				LabeledContent(league.name, value: format(average: league.average))
			}
			.buttonStyle(.navigation)
		} header: {
			if store.isShowingWidgets {
				Section {
					StatisticsWidgetLayoutView(store: store.scope(state: \.widgets, action: \.internal.widgets))
				}
				.listRowSeparator(.hidden)
				.listRowInsets(EdgeInsets())
				.listRowBackground(Color.clear)
			}
		} footer: {
			PreferredGearView(
				store: store.scope(state: \.preferredGear, action: \.internal.preferredGear)
			)
		}
		.navigationTitle(store.bowler.name)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				FilterButton(isActive: store.isAnyFilterActive) {
					send(.didTapFilterButton)
				}
			}
			ToolbarItem(placement: .navigationBarTrailing) {
				SortButton(isActive: false) { send(.didTapSortOrderButton) }
			}
		}
		.task { await send(.didStartTask).finish() }
		.onAppear { send(.onAppear) }
		.modifier(DestinationModifier(store: store))
	}
}

private struct DestinationModifier: ViewModifier {
	@Bindable var store: StoreOf<LeaguesList>

	func body(content: Content) -> some View {
		content
			.errors(store: store.scope(state: \.errors, action: \.internal.errors))
			.leagueEditor($store.scope(state: \.destination?.editor, action: \.internal.destination.editor))
			.leaguesFilter($store.scope(state: \.destination?.filters, action: \.internal.destination.filters))
			.sortOrder($store.scope(state: \.destination?.sortOrder, action: \.internal.destination.sortOrder))
			.seriesList($store.scope(state: \.destination?.series, action: \.internal.destination.series))
			.gamesList($store.scope(state: \.destination?.games, action: \.internal.destination.games))
	}
}

extension View {
	fileprivate func leagueEditor(_ store: Binding<StoreOf<LeagueEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<LeagueEditor>) in
			NavigationStack {
				LeagueEditorView(store: store)
			}
		}
	}

	fileprivate func leaguesFilter(_ store: Binding<StoreOf<LeaguesFilter>?>) -> some View {
		sheet(item: store) { (store: StoreOf<LeaguesFilter>) in
			NavigationStack {
				LeaguesFilterView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
	}

	fileprivate func sortOrder(_ store: Binding<StoreOf<SortOrderLibrary.SortOrder<League.Ordering>>?>) -> some View {
		sheet(item: store) { (store: StoreOf<SortOrderLibrary.SortOrder<League.Ordering>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}

	fileprivate func seriesList(_ store: Binding<StoreOf<SeriesList>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<SeriesList>) in
			SeriesListView(store: store)
		}
	}

	fileprivate func gamesList(_ store: Binding<StoreOf<GamesList>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<GamesList>) in
			GamesListView(store: store)
		}
	}
}
