import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import ExtensionsLibrary
import LeagueEditorFeature
import ModelsLibrary
import ResourceListLibrary
import SeriesListFeature
import SortOrderLibrary
import StatisticsWidgetsLayoutFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct LeaguesListView: View {
	let store: StoreOf<LeaguesList>

	struct ViewState: Equatable {
		let bowlerName: String
		let isAnyFilterActive: Bool
		let isShowingWidgets: Bool

		init(state: LeaguesList.State) {
			self.bowlerName = state.bowler.name
			self.isAnyFilterActive = state.filter != .init(bowler: state.bowler.id)
			self.isShowingWidgets = state.isShowingWidgets
		}
	}

	public init(store: StoreOf<LeaguesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: \.internal.list)
			) { league in
				Button { viewStore.send(.didTapLeague(id: league.id)) } label: {
					LabeledContent(league.name, value: format(average: league.average))
				}
				.buttonStyle(.navigation)
			} header: {
				if viewStore.isShowingWidgets {
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
			.navigationTitle(viewStore.bowlerName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					FilterButton(isActive: viewStore.isAnyFilterActive) {
						viewStore.send(.didTapFilterButton)
					}
				}
				ToolbarItem(placement: .navigationBarTrailing) {
					SortButton(isActive: false) { viewStore.send(.didTapSortOrderButton) }
				}
			}
			.task { viewStore.send(.didStartTask) }
			.onAppear { viewStore.send(.onAppear) }
		})
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.leagueEditor(store.scope(state: \.$destination.editor, action: \.internal.destination.editor))
		.leaguesFilter(store.scope(state: \.$destination.filters, action: \.internal.destination.filters))
		.sortOrder(store.scope(state: \.$destination.sortOrder, action: \.internal.destination.sortOrder))
		.seriesList(store.scope(state: \.$destination.series, action: \.internal.destination.series))
	}
}

@MainActor extension View {
	fileprivate func leagueEditor(_ store: PresentationStoreOf<LeagueEditor>) -> some View {
		sheet(store: store) { (store: StoreOf<LeagueEditor>) in
			NavigationStack {
				LeagueEditorView(store: store)
			}
		}
	}

	fileprivate func leaguesFilter(_ store: PresentationStoreOf<LeaguesFilter>) -> some View {
		sheet(store: store) { (store: StoreOf<LeaguesFilter>) in
			NavigationStack {
				LeaguesFilterView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
	}

	fileprivate func sortOrder(_ store: PresentationStoreOf<SortOrderLibrary.SortOrder<League.Ordering>>) -> some View {
		sheet(store: store) { (store: StoreOf<SortOrderLibrary.SortOrder<League.Ordering>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}

	fileprivate func seriesList(_ store: PresentationStoreOf<SeriesList>) -> some View {
		navigationDestination(store: store) { (store: StoreOf<SeriesList>) in
			SeriesListView(store: store)
		}
	}
}
