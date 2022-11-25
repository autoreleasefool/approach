import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct GearListView: View {
	let store: StoreOf<GearList>

	struct ViewState: Equatable {
		let listState: ListContentState<Gear, GearList.ErrorContent>

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
		case errorButtonTapped
		case swipeAction(Gear, GearList.SwipeAction)
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
					Theme.Images.EmptyState.gear,
					title: "No gear found",
					message: "You haven't added any gear yet. Track usage stats for your shoes, balls, or more."
				) {
					EmptyContentAction(title: "Add Gear") { viewStore.send(.subscribeToGear) }
				}
			} error: { error in
				ListEmptyContent(
					Theme.Images.Error.notFound,
					title: error.title,
					message: error.message,
					style: .error
				) {
					EmptyContentAction(title: error.action) { viewStore.send(.subscribeToGear) }
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
		case let .swipeAction(gear, swipeAction):
			self = .swipeAction(gear, swipeAction)
		}
	}
}
