import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

struct AlleysListRow: View {
	typealias ViewStore = ComposableArchitecture.ViewStore<AlleysListView.ViewState, AlleysListView.ViewAction>

	let viewStore: ViewStore
	let alley: Alley

	init(viewStore: ViewStore, alley: Alley) {
		self.viewStore = viewStore
		self.alley = alley
	}

	var body: some View {
		Text(alley.name)
			.swipeActions(allowsFullSwipe: true) {
				EditButton { viewStore.send(.swipeAction(alley, .edit)) }
				DeleteButton { viewStore.send(.swipeAction(alley, .delete)) }
			}
	}
}
