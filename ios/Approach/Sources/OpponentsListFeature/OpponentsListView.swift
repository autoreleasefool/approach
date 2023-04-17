import AssetsLibrary
import BowlerEditorFeature
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct OpponentsListView: View {
	let store: StoreOf<OpponentsList>

	struct ViewState: Equatable {
		let selection: Bowler.ID?

		init(state: OpponentsList.State) {
			self.selection = state.selection?.id
		}
	}

	enum ViewAction {
		case setNavigation(selection: Bowler.ID?)
	}

	public init(store: StoreOf<OpponentsList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: OpponentsList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /OpponentsList.Action.InternalAction.list)
			) { opponent in
				NavigationLink(
					destination: EmptyView(),
					tag: opponent.id,
					selection: viewStore.binding(
						get: \.selection,
						send: OpponentsListView.ViewAction.setNavigation(selection:)
					)
				) {
					Text(opponent.name)
				}
			}
			.navigationTitle(Strings.Opponent.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					SortOrderView(store: store.scope(state: \.sortOrder, action: /OpponentsList.Action.InternalAction.sortOrder))
				}
			}
			.sheet(store: store.scope(state: \.$editor, action: { .internal(.editor($0)) })) { scopedStore in
				NavigationView {
					BowlerEditorView(store: scopedStore)
				}
			}
		}
	}
}

extension OpponentsList.Action {
	init(action: OpponentsListView.ViewAction) {
		switch action {
		case let .setNavigation(selection):
			self = .view(.setNavigation(selection: selection))
		}
	}
}
