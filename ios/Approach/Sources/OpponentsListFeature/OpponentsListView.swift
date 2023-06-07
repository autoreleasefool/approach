import AssetsLibrary
import BowlerEditorFeature
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct OpponentsListView: View {
	let store: StoreOf<OpponentsList>

	struct ViewState: Equatable {
		init(state: OpponentsList.State) {}
	}

	public init(store: StoreOf<OpponentsList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /OpponentsList.Action.InternalAction.list)
			) { opponent in
				Text(opponent.name)
			}
			.navigationTitle(Strings.Opponent.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					SortOrderView(store: store.scope(state: \.sortOrder, action: /OpponentsList.Action.InternalAction.sortOrder))
				}
			}
			.sheet(store: store.scope(state: \.$editor, action: { .internal(.editor($0)) })) { scopedStore in
				NavigationStack {
					BowlerEditorView(store: scopedStore)
				}
			}
		}
	}
}
