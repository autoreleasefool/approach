import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

public struct AlleyLanesEditorView: View {
	let store: StoreOf<AlleyLanesEditor>

	struct ViewState: Equatable {
		let isLoadingInitialData: Bool
		let lanes: IdentifiedArrayOf<LaneEditor.State>

		init(state: AlleyLanesEditor.State) {
			self.lanes = state.lanes
			self.isLoadingInitialData = state.isLoadingInitialData
		}
	}

	enum ViewAction {
		case onAppear
		case addLaneButtonTapped
	}

	public init(store: StoreOf<AlleyLanesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleyLanesEditor.Action.init) { viewStore in
			List {
				Section {
					ForEachStore(
						store.scope(state: \.lanes, action: AlleyLanesEditor.Action.laneEditor(id:action:))
					) {
						LaneEditorView(store: $0)
					}
				} footer: {
					Text(Strings.Lane.Editor.Fields.IsAgainstWall.help)
				}

				Section {
					Button { viewStore.send(.addLaneButtonTapped) } label: {
						Label(Strings.Lane.List.add, systemImage: "plus.circle")
					}
				}
			}
			.navigationTitle(Strings.Lane.List.title)
			.onAppear { viewStore.send(.onAppear) }
		}
	}
}

extension AlleyLanesEditor.Action {
	init(action: AlleyLanesEditorView.ViewAction) {
		switch action {
		case .onAppear:
			self = .loadInitialData
		case .addLaneButtonTapped:
			self = .addLaneButtonTapped
		}
	}
}
