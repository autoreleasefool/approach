import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GearEditorFeature
import ModelsLibrary
import ModelsViewsLibrary
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GearListView: View {
	let store: StoreOf<GearList>

	struct ViewState: Equatable {
		let isAnyFilterActive: Bool

		init(state: GearList.State) {
			self.isAnyFilterActive = state.kindFilter != nil
		}
	}

	public init(store: StoreOf<GearList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /GearList.Action.InternalAction.list)
			) {
				Gear.ViewWithAvatar($0)
			}
			.navigationTitle(Strings.Gear.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					FilterButton(isActive: viewStore.isAnyFilterActive) {
						viewStore.send(.didTapFilterButton)
					}
				}
				ToolbarItem(placement: .navigationBarTrailing) {
					SortButton(isActive: false) { viewStore.send(.didTapSortOrderButton) }
				}
			}
			.onAppear { viewStore.send(.onAppear) }
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GearList.Destination.State.editor,
			action: GearList.Destination.Action.editor
		) { store in
			NavigationStack {
				GearEditorView(store: store)
			}
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GearList.Destination.State.filters,
			action: GearList.Destination.Action.filters
		) { store in
			NavigationStack {
				GearFilterView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GearList.Destination.State.sortOrder,
			action: GearList.Destination.Action.sortOrder
		) { store in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}
}
