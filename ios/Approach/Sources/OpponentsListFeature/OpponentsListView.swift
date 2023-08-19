import AssetsLibrary
import BowlerEditorFeature
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import OpponentDetailsFeature
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct OpponentsListView: View {
	let store: StoreOf<OpponentsList>

	struct ViewState: Equatable {
		let isOpponentDetailsEnabled: Bool

		init(state: OpponentsList.State) {
			self.isOpponentDetailsEnabled = state.isOpponentDetailsEnabled
		}
	}

	public init(store: StoreOf<OpponentsList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /OpponentsList.Action.InternalAction.list)
			) { opponent in
				if viewStore.isOpponentDetailsEnabled {
					Button { store.send(.view(.didTapOpponent(opponent.id))) } label: {
						Text(opponent.name)
					}
					.buttonStyle(.navigation)
				} else {
					Text(opponent.name)
				}
			}
		})
		.navigationTitle(Strings.Opponent.List.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				SortButton(isActive: false) { store.send(.view(.didTapSortOrderButton)) }
			}
		}
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /OpponentsList.Destination.State.details,
			action: OpponentsList.Destination.Action.details
		) { (store: StoreOf<OpponentDetails>) in
			OpponentDetailsView(store: store)
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /OpponentsList.Destination.State.editor,
			action: OpponentsList.Destination.Action.editor
		) { (store: StoreOf<BowlerEditor>) in
			NavigationStack {
				BowlerEditorView(store: store)
			}
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /OpponentsList.Destination.State.sortOrder,
			action: OpponentsList.Destination.Action.sortOrder
		) { store in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}
}
