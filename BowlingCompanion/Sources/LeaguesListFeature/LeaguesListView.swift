import AssetsLibrary
import ComposableArchitecture
import LeagueEditorFeature
import ResourceListLibrary
import SeriesListFeature
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct LeaguesListView: View {
	let store: StoreOf<LeaguesList>

	struct ViewState: Equatable {
		let bowlerName: String
		let selection: League.ID?
		let isEditorPresented: Bool
		let isFiltersPresented: Bool
		let isAnyFilterActive: Bool

		init(state: LeaguesList.State) {
			self.selection = state.selection?.id
			self.bowlerName = state.bowler.name
			self.isEditorPresented = state.editor != nil
			self.isFiltersPresented = state.isFiltersPresented
			self.isAnyFilterActive = state.filters.hasFilters
		}
	}

	enum ViewAction {
		case didTapFilterButton
		case setEditorSheet(isPresented: Bool)
		case setFilterSheet(isPresented: Bool)
		case setNavigation(selection: League.ID?)
	}

	public init(store: StoreOf<LeaguesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LeaguesList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /LeaguesList.Action.InternalAction.list)
			) { league in
				NavigationLink(
					destination: IfLetStore(
						store.scope(state: \.selection?.value, action: /LeaguesList.Action.InternalAction.series)
					) {
						SeriesListView(store: $0)
					},
					tag: league.id,
					selection: viewStore.binding(
						get: \.selection,
						send: LeaguesListView.ViewAction.setNavigation(selection:)
					)
				) {
					LeagueRow(league: league)
				}
			}
			.navigationTitle(viewStore.bowlerName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					FilterButton(isActive: viewStore.isAnyFilterActive) {
						viewStore.send(.didTapFilterButton)
					}
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isFiltersPresented,
				send: ViewAction.setFilterSheet(isPresented:)
			)) {
				NavigationView {
					LeaguesFilterView(store: store.scope(state: \.filters, action: /LeaguesList.Action.InternalAction.filters))
				}
				.presentationDetents([.medium, .large])
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isEditorPresented,
				send: ViewAction.setEditorSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.editor, action: /LeaguesList.Action.InternalAction.editor)) { scopedStore in
					NavigationView {
						LeagueEditorView(store: scopedStore)
					}
				}
			}
		}
	}
}

extension LeaguesList.Action {
	init(action: LeaguesListView.ViewAction) {
		switch action {
		case .didTapFilterButton:
			self = .view(.setFilterSheet(isPresented: true))
		case let .setFilterSheet(isPresented):
			self = .view(.setFilterSheet(isPresented: isPresented))
		case let .setEditorSheet(isPresented):
			self = .view(.setEditorSheet(isPresented: isPresented))
		case let .setNavigation(selection):
			self = .view(.setNavigation(selection: selection))
		}
	}
}
