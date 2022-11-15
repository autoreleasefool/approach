import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI

public struct GearListView: View {
	let store: StoreOf<GearList>

	struct ViewState: Equatable {
		let gear: IdentifiedArrayOf<Gear>

		init(state: GearList.State) {
			self.gear = state.gear
		}
	}

	enum ViewAction {
		case subscribeToGear
		case swipeAction(Gear, GearList.SwipeAction)
	}

	public init(store: StoreOf<GearList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GearList.Action.init) { viewStore in
			List(viewStore.gear) { gear in
				Text(gear.name)
					.swipeActions(allowsFullSwipe: true) {
						Button {
							viewStore.send(.swipeAction(gear, .edit))
						} label: {
							Label("Edit", systemImage: "pencil")
						}
						.tint(.blue)

						Button(role: .destructive) {
							viewStore.send(.swipeAction(gear, .delete))
						} label: {
							Label("Delete", systemImage: "trash")
						}
					}
			}
			.navigationTitle("Gear")
			.task { await viewStore.send(.subscribeToGear).finish() }
		}
	}
}

extension GearList.Action {
	init(action: GearListView.ViewAction) {
		switch action {
		case .subscribeToGear:
			self = .subscribeToGear
		case let .swipeAction(gear, swipeAction):
			self = .swipeAction(gear, swipeAction)
		}
	}
}
