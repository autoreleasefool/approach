import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI

public struct AlleysListView: View {
	let store: StoreOf<AlleysList>

	struct ViewState: Equatable {
		let alleys: IdentifiedArrayOf<Alley>

		init(state: AlleysList.State) {
			self.alleys = state.alleys
		}
	}

	enum ViewAction {
		case subscribeToAlleys
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
			.task { await viewStore.send(.subscribeToAlleys).finish() }
		}
	}
}

extension AlleysList.Action {
	init(action: AlleysListView.ViewAction) {
		switch action {
		case .subscribeToAlleys:
			self = .subscribeToAlleys
		case let .swipeAction(alley, swipeAction):
			self = .swipeAction(alley, swipeAction)
		}
	}
}
