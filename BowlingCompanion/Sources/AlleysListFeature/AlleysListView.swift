import AlleyEditorFeature
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ResourceListLibrary
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct AlleysListView: View {
	let store: StoreOf<AlleysList>

	struct ViewState: Equatable {
		let isEditorPresented: Bool
		let isFiltersPresented: Bool
		let isFilterActive: Bool

		init(state: AlleysList.State) {
			self.isEditorPresented = state.editor != nil
			self.isFiltersPresented = state.isFiltersPresented
			self.isFilterActive = state.filters.filter != nil
		}
	}

	enum ViewAction {
		case filterButtonTapped
		case setFilterSheet(isPresented: Bool)
		case setEditorSheet(isPresented: Bool)
	}

	public init(store: StoreOf<AlleysList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleysList.Action.init) { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: /AlleysList.Action.InternalAction.list)
			) {
				AlleyRow(alley: $0)
			}
			.navigationTitle(Strings.Alley.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					FilterButton(isActive: viewStore.isFilterActive) {
						viewStore.send(.filterButtonTapped)
					}
					.disabled(viewStore.isFiltersPresented)
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isFiltersPresented,
				send: ViewAction.setFilterSheet(isPresented:)
			)) {
				NavigationView {
					AlleysFilterView(store: store.scope(state: \.filters, action: /AlleysList.Action.InternalAction.filters))
				}
				.presentationDetents(undimmed: [.medium, .large])
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isEditorPresented,
				send: ViewAction.setEditorSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.editor, action: /AlleysList.Action.InternalAction.editor)) { scopedStore in
					NavigationView {
						AlleyEditorView(store: scopedStore)
					}
				}
			}
		}
	}
}

extension AlleysList.Action {
	init(action: AlleysListView.ViewAction) {
		switch action {
		case .filterButtonTapped:
			self = .view(.setFilterSheet(isPresented: true))
		case let .setFilterSheet(isPresented):
			self = .view(.setFilterSheet(isPresented: isPresented))
		case let .setEditorSheet(isPresented):
			self = .view(.setEditorSheet(isPresented: isPresented))
		}
	}
}
