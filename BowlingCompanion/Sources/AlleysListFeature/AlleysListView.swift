import AlleyEditorFeature
import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI

public struct AlleysListView: View {
	let store: StoreOf<AlleysList>

	struct ViewState: Equatable {
		let alleys: IdentifiedArrayOf<Alley>
		let isAlleyEditorPresented: Bool

		init(state: AlleysList.State) {
			self.alleys = state.alleys
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
			List(viewStore.alleys) { alley in
				Text(alley.name)
					.swipeActions(allowsFullSwipe: true) {
						Button {
							viewStore.send(.swipeAction(alley, .edit))
						} label: {
							Label("Edit", systemImage: "pencil")
						}
						.tint(.blue)

						Button(role: .destructive) {
							viewStore.send(.swipeAction(alley, .delete))
						} label: {
							Label("Delete", systemImage: "trash")
						}
					}
			}
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
