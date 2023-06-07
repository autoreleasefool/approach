import AssetsLibrary
import ComposableArchitecture
import LeagueEditorFeature
import ModelsLibrary
import ResourceListLibrary
import SeriesListFeature
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct LeaguesListView: View {
	let store: StoreOf<LeaguesList>

	struct ViewState: Equatable {
		let bowlerName: String
		let isAnyFilterActive: Bool

		init(state: LeaguesList.State) {
			self.bowlerName = state.bowler.name
			self.isAnyFilterActive = state.filter != .init(bowler: state.bowler.id)
		}
	}

	enum ViewAction {
		case didTapFilterButton
		case didTapLeague(League.ID)
	}

	public init(store: StoreOf<LeaguesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LeaguesList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /LeaguesList.Action.InternalAction.list)
			) { league in
				Button { viewStore.send(.didTapLeague(league.id)) } label: {
					LabeledContent(league.name, value: format(average: league.average))
				}
				.buttonStyle(.navigation)
			}
			.navigationTitle(viewStore.bowlerName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					FilterButton(isActive: viewStore.isAnyFilterActive) {
						viewStore.send(.didTapFilterButton)
					}
				}
				ToolbarItem(placement: .navigationBarTrailing) {
					SortOrderView(store: store.scope(state: \.sortOrder, action: /LeaguesList.Action.InternalAction.sortOrder))
				}
			}
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
			.navigationDestination(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /LeaguesList.Destination.State.series,
				action: LeaguesList.Destination.Action.series
			) { store in
				SeriesListView(store: store)
			}
		}
	}
}

extension LeaguesList.Action {
	init(action: LeaguesListView.ViewAction) {
		switch action {
		case .didTapFilterButton:
			self = .view(.didTapFilterButton)
		case let .didTapLeague(id):
			self = .view(.didTapLeague(id: id))
		}
	}
}
