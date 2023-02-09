import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import OpponentEditorFeature
import ResourceListLibrary
import SharedModelsLibrary
import SharedModelsViewsLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct OpponentsListView: View {
	let store: StoreOf<OpponentsList>

	struct ViewState: Equatable {
		let selection: Opponent.ID?
		let isEditorPresented: Bool

		init(state: OpponentsList.State) {
			self.selection = state.selection?.id
			self.isEditorPresented = state.editor != nil
		}
	}

	enum ViewAction {
		case setEditorSheet(isPresented: Bool)
		case setNavigation(selection: Opponent.ID?)
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
					OpponentRow(opponent: opponent)
				}
			}
			.navigationTitle(Strings.Opponent.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					SortOrderView(store: store.scope(state: \.sortOrder, action: /OpponentsList.Action.InternalAction.sortOrder))
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isEditorPresented,
				send: ViewAction.setEditorSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.editor, action: /OpponentsList.Action.InternalAction.editor)) { scopedStore in
					NavigationView {
						OpponentEditorView(store: scopedStore)
					}
				}
			}
		}
	}
}

extension OpponentsList.Action {
	init(action: OpponentsListView.ViewAction) {
		switch action {
		case let .setEditorSheet(isPresented):
			self = .view(.setEditorSheet(isPresented: isPresented))
		case let .setNavigation(selection):
			self = .view(.setNavigation(selection: selection))
		}
	}
}
