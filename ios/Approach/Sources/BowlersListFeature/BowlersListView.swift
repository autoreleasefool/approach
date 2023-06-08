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

	enum ViewAction {
		case didTapConfigureStatisticsButton
		case didTapSortOrderButton
		case didTapBowler(Bowler.ID)
	}

	public init(store: StoreOf<BowlersList>) {
		self.store = store
	}

	public var body: some View {
		ResourceListView(
			store: store.scope(state: \.list, action: /BowlersList.Action.InternalAction.list)
		) { bowler in
			Button { ViewStore(store.stateless).send(.view(.didTapBowler(bowler.id))) } label: {
				LabeledContent(bowler.name, value: format(average: bowler.average))
			}
			.buttonStyle(.navigation)
		} header: {
			Section {
				Button { ViewStore(store.stateless).send(.view(.didTapConfigureStatisticsButton)) } label: {
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
				SortButton(isActive: false) { ViewStore(store.stateless).send(.view(.didTapSortOrderButton)) }
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
