import AssetsLibrary
import BowlerEditorFeature
import ComposableArchitecture
import LeaguesListFeature
import ModelsLibrary
import ResourceListLibrary
import SortOrderLibrary
import StatisticsWidgetsLayoutFeature
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		public let ordering: Bowler.Ordering
		public let isShowingWidgets: Bool

		init(state: BowlersList.State) {
			self.ordering = state.ordering
			self.isShowingWidgets = state.isShowingWidgets
		}
	}

	enum ViewAction {
		case didStartObserving
		case didTapSortOrderButton
		case didTapBowler(Bowler.ID)
	}

	public init(store: StoreOf<BowlersList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlersList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /BowlersList.Action.InternalAction.list)
			) { bowler in
				Button { viewStore.send(.didTapBowler(bowler.id)) } label: {
					LabeledContent(bowler.name, value: format(average: bowler.average))
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
			}
			.navigationTitle(Strings.Bowler.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					SortButton(isActive: false) { viewStore.send(.didTapSortOrderButton) }
				}
			}
			.task { viewStore.send(.didStartObserving) }
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /BowlersList.Destination.State.editor,
			action: BowlersList.Destination.Action.editor
		) { (store: StoreOf<BowlerEditor>) in
			NavigationStack {
				BowlerEditorView(store: store)
			}
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /BowlersList.Destination.State.sortOrder,
			action: BowlersList.Destination.Action.sortOrder
		) { (store: StoreOf<SortOrderLibrary.SortOrder<Bowler.Ordering>>) in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /BowlersList.Destination.State.leagues,
			action: BowlersList.Destination.Action.leagues
		) { (store: StoreOf<LeaguesList>) in
			LeaguesListView(store: store)
		}
	}
}

extension BowlersList.Action {
	init(action: BowlersListView.ViewAction) {
		switch action {
		case .didTapSortOrderButton:
			self = .view(.didTapSortOrderButton)
		case let .didTapBowler(id):
			self = .view(.didTapBowler(id))
		case .didStartObserving:
			self = .view(.didStartObserving)
		}
	}
}
