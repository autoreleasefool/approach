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
		let isFiltersPresented: Bool
		let isAnyFilterActive: Bool

		init(state: AlleysList.State) {
			self.isFiltersPresented = state.isFiltersPresented
			self.isAnyFilterActive = state.filter != .init()
		}
	}

	enum ViewAction {
		case filterButtonTapped
		case setFilterSheet(isPresented: Bool)
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
				.presentationDetents([.medium, .large])
			}
			.sheet(store: store.scope(state: \.$editor, action: { .internal(.editor($0)) })) { scopedStore in
				NavigationView {
					AlleyEditorView(store: scopedStore)
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
		}
	}
}
