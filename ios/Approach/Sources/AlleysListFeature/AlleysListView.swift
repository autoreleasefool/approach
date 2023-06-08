import AlleyEditorFeature
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ResourceListLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct AlleysListView: View {
	let store: StoreOf<AlleysList>

	struct ViewState: Equatable {
		let isAnyFilterActive: Bool

		init(state: AlleysList.State) {
			self.isAnyFilterActive = state.filter != .init()
		}
	}

	enum ViewAction {
		case didTapFiltersButton
	}

	public init(store: StoreOf<AlleysList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleysList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /AlleysList.Action.InternalAction.list)
			) {
				Alley.View(alley: $0)
			}
			.navigationTitle(Strings.Alley.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					FilterButton(isActive: viewStore.isAnyFilterActive) {
						viewStore.send(.didTapFiltersButton)
					}
				}
			}
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /AlleysList.Destination.State.editor,
			action: AlleysList.Destination.Action.editor
		) { store in
			NavigationStack {
				AlleyEditorView(store: store)
			}
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /AlleysList.Destination.State.filters,
			action: AlleysList.Destination.Action.filters
		) { store in
			NavigationStack {
				AlleysFilterView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
	}
}

extension AlleysList.Action {
	init(action: AlleysListView.ViewAction) {
		switch action {
		case .didTapFiltersButton:
			self = .view(.didTapFiltersButton)
		}
	}
}
