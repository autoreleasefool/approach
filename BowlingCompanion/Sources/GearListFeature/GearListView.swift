import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct GearListView: View {
	let store: StoreOf<GearList>

	struct ViewState: Equatable {
		let listState: ListContentState<Gear, ListErrorContent>

		init(state: GearList.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let gear = state.gear {
				self.listState = .loaded(gear)
			} else {
				self.listState = .loading
			}
		}
	}

	enum ViewAction {
		case subscribeToGear
		case addButtonTapped
		case errorButtonTapped
		case swipeAction(Gear, GearList.SwipeAction)
		case setEditorFormSheet(isPresented: Bool)
	}

	public init(store: StoreOf<GearList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GearList.Action.init) { viewStore in
			ListContent(viewStore.listState) { gear in
				ForEach(gear) { gear in
					GearListRow(viewStore: viewStore, gear: gear)
				}
			} empty: {
				ListEmptyContent(
					.emptyGear,
					title: "No gear found",
					message: "You haven't added any gear yet. Track usage stats for your shoes, balls, or more."
				) {
					EmptyContentAction(title: "Add Gear") { viewStore.send(.addButtonTapped) }
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
		case .errorButtonTapped:
			self = .errorButtonTapped
		case .addButtonTapped:
			self = .setEditorFormSheet(isPresented: true)
		case let .swipeAction(gear, swipeAction):
			self = .swipeAction(gear, swipeAction)
		case let .setEditorFormSheet(isPresented):
			self = .setEditorFormSheet(isPresented: isPresented)
		}
	}
}
