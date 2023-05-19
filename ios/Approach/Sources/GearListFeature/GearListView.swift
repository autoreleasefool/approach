import AssetsLibrary
import ComposableArchitecture
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
		let isFiltersPresented: Bool
		let isAnyFilterActive: Bool

		init(state: GearList.State) {
			self.isFiltersPresented = state.isFiltersPresented
			self.isAnyFilterActive = state.kindFilter != nil
		}
	}

	enum ViewAction {
		case didTapFilterButton
		case setFilterSheet(isPresented: Bool)
	}

	public init(store: StoreOf<GearList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GearList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /GearList.Action.InternalAction.list)
			) {
				Gear.View(gear: $0)
			}
			.navigationTitle(Strings.Gear.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					FilterButton(isActive: viewStore.isAnyFilterActive) {
						viewStore.send(.didTapFilterButton)
					}
				}
				ToolbarItem(placement: .navigationBarTrailing) {
					SortOrderView(store: store.scope(state: \.sortOrder, action: /GearList.Action.InternalAction.sortOrder))
				}
			}
			.sheet(store: store.scope(state: \.$editor, action: { .internal(.editor($0)) })) { scopedStore in
				NavigationView {
					GearEditorView(store: scopedStore)
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isFiltersPresented,
				send: ViewAction.setFilterSheet(isPresented:)
			)) {
				NavigationView {
					GearFilterView(store: store.scope(state: \.filters, action: /GearList.Action.InternalAction.filters))
				}
				.presentationDetents([.medium, .large])
			}
		}
	}
}

extension GearList.Action {
	init(action: GearListView.ViewAction) {
		switch action {
		case .didTapFilterButton:
			self = .view(.setFilterSheet(isPresented: true))
		case let .setFilterSheet(isPresented):
			self = .view(.setFilterSheet(isPresented: isPresented))
		}
	}
}
