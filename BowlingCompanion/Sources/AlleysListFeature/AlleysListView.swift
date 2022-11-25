import AlleyEditorFeature
import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI
import StatisticsWidgetsFeature
import ThemesLibrary
import ViewsLibrary

public struct AlleysListView: View {
	let store: StoreOf<AlleysList>

	struct ViewState: Equatable {
		let listState: ListContentState<Alley, AlleysList.ErrorContent>
		let isAlleyEditorPresented: Bool

		init(state: AlleysList.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let alleys = state.alleys {
				self.listState = .loaded(alleys)
			} else {
				self.listState = .loading
			}
			self.isAlleyEditorPresented = state.alleyEditor != nil
		}
	}

	enum ViewAction {
		case subscribeToAlleys
		case setFormSheet(isPresented: Bool)
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
					Theme.Images.EmptyState.alleys,
					title: "No alleys found",
					message: "You haven't added any alleys yet."
				) {
					EmptyContentAction(title: "Add Alley") { viewStore.send(.subscribeToAlleys) }
				}
			} error: { error in
				ListEmptyContent(
					Theme.Images.Error.notFound,
					title: error.title,
					message: error.message,
					style: .error
				) {
					EmptyContentAction(title: error.action) { viewStore.send(.subscribeToAlleys) }
				}
			}
			.scrollContentBackground(.hidden)
			.navigationTitle("Alleys")
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button {
						viewStore.send(.setFormSheet(isPresented: true))
					} label: {
						Image(systemName: "plus")
					}
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isAlleyEditorPresented,
				send: ViewAction.setFormSheet(isPresented:)
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
			.task { await viewStore.send(.subscribeToAlleys).finish() }
		}
	}
}

extension AlleysList.Action {
	init(action: AlleysListView.ViewAction) {
		switch action {
		case .subscribeToAlleys:
			self = .subscribeToAlleys
		case let .setFormSheet(isPresented):
			self = .setFormSheet(isPresented: isPresented)
		case let .swipeAction(alley, swipeAction):
			self = .swipeAction(alley, swipeAction)
		}
	}
}
