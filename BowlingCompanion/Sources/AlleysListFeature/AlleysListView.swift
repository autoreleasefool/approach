import AlleyEditorFeature
import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import AssetsLibrary
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
			self.isAlleyFilterActive = state.alleyFilters.filter != nil
		}
	}

	enum ViewAction {
		case observeAlleys
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
					AlleyRow(
						alley: alley,
						onEdit: { viewStore.send(.swipeAction(alley, .edit)) },
						onDelete: { viewStore.send(.swipeAction(alley, .delete)) }
					)
				}
				.listRowSeparator(.hidden)
			} empty: {
				ListEmptyContent(
					.emptyAlleys,
					title: Strings.Alley.Error.Empty.title,
					message: viewStore.isAlleyFilterActive
						? Strings.Alley.Error.Empty.Filter.message
						: Strings.Alley.Error.Empty.message
				) {
					EmptyContentAction(title: Strings.Alley.List.add) { viewStore.send(.addButtonTapped) }
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
			.navigationTitle(Strings.Alley.List.title)
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
			.task { await viewStore.send(.observeAlleys).finish() }
		}
	}
}

extension AlleysList.Action {
	init(action: AlleysListView.ViewAction) {
		switch action {
		case .observeAlleys:
			self = .observeAlleys
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
