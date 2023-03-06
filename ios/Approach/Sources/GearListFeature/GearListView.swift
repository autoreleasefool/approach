import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import GearEditorFeature
import ResourceListLibrary
import SharedModelsLibrary
import SharedModelsViewsLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GearListView: View {
	let store: StoreOf<GearList>

	struct ViewState: Equatable {
		let isEditorPresented: Bool

		init(state: GearList.State) {
			self.isEditorPresented = state.editor != nil
		}
	}

	enum ViewAction {
		case setEditorSheet(isPresented: Bool)
	}

	public init(store: StoreOf<GearList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GearList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /GearList.Action.InternalAction.list)
			) { gear in
				GearRow(gear: gear)
			}
			.navigationTitle(Strings.Gear.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					SortOrderView(store: store.scope(state: \.sortOrder, action: /GearList.Action.InternalAction.sortOrder))
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isEditorPresented,
				send: ViewAction.setEditorSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.editor, action: /GearList.Action.InternalAction.editor)) { scopedStore in
					NavigationView {
						GearEditorView(store: scopedStore)
					}
				}
			}
		}
	}
}

extension GearList.Action {
	init(action: GearListView.ViewAction) {
		switch action {
		case let .setEditorSheet(isPresented):
			self = .view(.setEditorSheet(isPresented: isPresented))
		}
	}
}
