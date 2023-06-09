import AssetsLibrary
import BowlerEditorFeature
import ComposableArchitecture
import LeaguesListFeature
import ModelsLibrary
import ResourceListLibrary
import SortOrderLibrary
import StatisticsWidgetsFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct BowlersListView: View {
	let store: StoreOf<BowlersList>

	struct ViewState: Equatable {
		public var ordering: Bowler.Ordering = .byRecentlyUsed
		init(state: BowlersList.State) {
			self.ordering = state.ordering
		}
	}

	enum ViewAction {
		case didTapConfigureStatisticsButton
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
				Section {
					Button { viewStore.send(.didTapConfigureStatisticsButton) } label: {
						PlaceholderWidget(size: .medium)
					}
					.buttonStyle(TappableElement())
				}
				.listRowSeparator(.hidden)
				.listRowInsets(EdgeInsets())
			}
			.navigationTitle(Strings.Bowler.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					SortButton(isActive: false) { viewStore.send(.didTapSortOrderButton) }
				}
			}
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /BowlersList.Destination.State.editor,
			action: BowlersList.Destination.Action.editor
		) { store in
			NavigationStack {
				BowlerEditorView(store: store)
			}
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /BowlersList.Destination.State.sortOrder,
			action: BowlersList.Destination.Action.sortOrder
		) { store in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /BowlersList.Destination.State.leagues,
			action: BowlersList.Destination.Action.leagues
		) { store in
			LeaguesListView(store: store)
		}
	}
}

extension BowlersList.Action {
	init(action: BowlersListView.ViewAction) {
		switch action {
		case .didTapConfigureStatisticsButton:
			self = .view(.didTapConfigureStatisticsButton)
		case .didTapSortOrderButton:
			self = .view(.didTapSortOrderButton)
		case let .didTapBowler(id):
			self = .view(.didTapBowler(id))
		}
	}
}
