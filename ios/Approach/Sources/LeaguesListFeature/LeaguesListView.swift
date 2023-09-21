import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
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
				store: store.scope(state: \.list, action: /LeaguesList.Action.InternalAction.list)
			) { league in
				Button { viewStore.send(.didTapLeague(id: league.id)) } label: {
					LabeledContent(league.name, value: format(average: league.average))
				}
				.buttonStyle(.navigation)
			} header: {
				if viewStore.isShowingWidgets {
					Section {
						StatisticsWidgetLayoutView(store: store.scope(state: \.widgets, action: { .internal(.widgets($0)) }))
					}
					.listRowSeparator(.hidden)
					.listRowInsets(EdgeInsets())
					.listRowBackground(Color.clear)
				}
			} footer: {
				PreferredGearView(
					store: store.scope(state: \.preferredGear, action: /LeaguesList.Action.InternalAction.preferredGear)
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
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /LeaguesList.Destination.State.editor,
			action: LeaguesList.Destination.Action.editor
		) { store in
			NavigationStack {
				LeagueEditorView(store: store)
			}
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /LeaguesList.Destination.State.filters,
			action: LeaguesList.Destination.Action.filters
		) { store in
			NavigationStack {
				LeaguesFilterView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /LeaguesList.Destination.State.sortOrder,
			action: LeaguesList.Destination.Action.sortOrder
		) { store in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /LeaguesList.Destination.State.series,
			action: LeaguesList.Destination.Action.series
		) { store in
			SeriesListView(store: store)
		}
	}
}
