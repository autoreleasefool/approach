import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

struct GearListRow: View {
	typealias ViewStore = ComposableArchitecture.ViewStore<GearListView.ViewState, GearListView.ViewAction>

	let viewStore: ViewStore
	let gear: Gear

	init(viewStore: ViewStore, gear: Gear) {
		self.viewStore = viewStore
		self.gear = gear
	}

	var body: some View {
		Text(gear.name)
			.swipeActions(allowsFullSwipe: true) {
				EditButton { viewStore.send(.swipeAction(gear, .edit)) }
				DeleteButton { viewStore.send(.swipeAction(gear, .delete)) }
			}
	}
}
