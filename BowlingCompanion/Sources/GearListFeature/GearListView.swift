import ComposableArchitecture
import GearEditorFeature
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct GearListView: View {
	let store: StoreOf<GearList>

	struct ViewState: Equatable {
		let listState: ListContentState<Gear, ListErrorContent>
		let isGearEditorPresented: Bool

		init(state: GearList.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let gear = state.gear {
				self.listState = .loaded(gear)
			} else {
				self.listState = .loading
			}
			self.isGearEditorPresented = state.gearEditor != nil
		}
	}

	enum ViewAction {
		case refreshList
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
					GearRow(
						gear: gear,
						onEdit: { viewStore.send(.swipeAction(gear, .edit)) },
						onDelete: { viewStore.send(.swipeAction(gear, .delete)) }
					)
				}
			} empty: {
				ListEmptyContent(
					.emptyGear,
					title: Strings.Gear.Error.Empty.title,
					message: Strings.Gear.Error.Empty.message
				) {
					EmptyContentAction(title: Strings.Gear.List.add) { viewStore.send(.addButtonTapped) }
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
			.navigationTitle(Strings.Gear.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					AddButton { viewStore.send(.addButtonTapped) }
				}
			}
			.sheet(isPresented: viewStore.binding(
				get: \.isGearEditorPresented,
				send: ViewAction.setEditorFormSheet(isPresented:)
			)) {
				IfLetStore(store.scope(state: \.gearEditor, action: GearList.Action.gearEditor)) { scopedStore in
					NavigationView {
						GearEditorView(store: scopedStore)
					}
				}
			}
			.onAppear { viewStore.send(.refreshList) }
		}
	}
}

extension GearList.Action {
	init(action: GearListView.ViewAction) {
		switch action {
		case .refreshList:
			self = .refreshList
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
