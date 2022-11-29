import AlleyEditorFeature
import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ThemesLibrary
import ViewsLibrary

public struct AlleysListView: View {
	let store: StoreOf<AlleysList>

	struct ViewState: Equatable {
		let listState: ListContentState<Alley, ListErrorContent>
		let isAlleyEditorPresented: Bool
		let isAlleyFiltersPresented: Bool
		let isAlleyFilterActive: Bool

		init(state: AlleysList.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let alleys = state.alleys {
				self.listState = .loaded(alleys)
			} else {
				self.listState = .loading
			}
			self.isAlleyEditorPresented = state.alleyEditor != nil
			self.isAlleyFiltersPresented = state.isAlleyFiltersPresented
			self.isAlleyFilterActive = state.alleyFilters.filters.count > 0
		}
	}

	enum ViewAction {
		case refreshList
		case filterButtonTapped
		case addButtonTapped
		case errorButtonTapped
		case setFilterSheet(isPresented: Bool)
		case setEditorFormSheet(isPresented: Bool)
		case swipeAction(Alley, AlleysList.SwipeAction)
	}

	public init(store: StoreOf<AlleysList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleysList.Action.init) { viewStore in
			ListContent(viewStore.listState) { alleys in
				ForEach(alleys) { alley in
					AlleysListRow(viewStore: viewStore, alley: alley)
				}
				.listRowSeparator(.hidden)
			} empty: {
				ListEmptyContent(
					.emptyAlleys,
					title: Strings.Alleys.Errors.Empty.title,
					message: viewStore.isAlleyFilterActive
						? Strings.Alleys.Errors.Empty.Filter.message
						: Strings.Alleys.Errors.Empty.message
				) {
					EmptyContentAction(title: Strings.Alleys.List.add) { viewStore.send(.addButtonTapped) }
				}
			} error: { error in
				ListEmptyContent(
					.errorNotFound,
					title: error.title,
					message: error.message,
					style: .error
				) {
					EmptyContentAction(title: error.action) { viewStore.send(.errorButtonTapped) }
				}
			}
			.scrollContentBackground(.hidden)
			.navigationTitle(Strings.Alleys.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					FilterButton(isActive: viewStore.isAlleyFilterActive) {
						viewStore.send(.filterButtonTapped)
					}
					.disabled(viewStore.isAlleyFiltersPresented)
				}
				ToolbarItem(placement: .navigationBarTrailing) {
					AddButton { viewStore.send(.addButtonTapped) }
						.disabled(viewStore.isAlleyFiltersPresented)
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isAlleyFiltersPresented,
				send: ViewAction.setFilterSheet(isPresented:)
			)) {
				NavigationView {
					AlleysFilterView(store: store.scope(state: \.alleyFilters, action: AlleysList.Action.alleysFilter))
				}
				.presentationDetents(undimmed: [.medium, .large])
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isAlleyEditorPresented,
				send: ViewAction.setEditorFormSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.alleyEditor, action: AlleysList.Action.alleyEditor)) { scopedStore in
					NavigationView {
						AlleyEditorView(store: scopedStore)
					}
				}
			}
			.alert(
				self.store.scope(state: \.alert, action: AlleysList.Action.alert),
				dismiss: .dismissed
			)
			.onAppear { viewStore.send(.refreshList) }
		}
	}
}

extension AlleysList.Action {
	init(action: AlleysListView.ViewAction) {
		switch action {
		case .refreshList:
			self = .refreshList
		case .filterButtonTapped:
			self = .setFilterSheet(isPresented: true)
		case let .setFilterSheet(isPresented):
			self = .setFilterSheet(isPresented: isPresented)
		case .addButtonTapped:
			self = .setEditorFormSheet(isPresented: true)
		case .errorButtonTapped:
			self = .errorButtonTapped
		case let .setEditorFormSheet(isPresented):
			self = .setEditorFormSheet(isPresented: isPresented)
		case let .swipeAction(alley, swipeAction):
			self = .swipeAction(alley, swipeAction)
		}
	}
}
