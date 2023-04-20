import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct LaneEditorView: View {
	let store: StoreOf<LaneEditor>

	struct ViewState: Equatable {
		@BindingState var label: String
		@BindingState var position: Lane.Position

		init(state: LaneEditor.State) {
			self.label = state.label
			self.position = state.position
		}
	}

	enum ViewAction: BindableAction {
		case didSwipe(LaneEditor.SwipeAction)
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<LaneEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LaneEditor.Action.init) { viewStore in
			VStack {
				HStack {
					TextField(
						Strings.Lane.Properties.label,
						text: viewStore.binding(\.$label)
					)
				}

				Picker(
					Strings.Lane.Properties.position,
					selection: viewStore.binding(\.$position)
				) {
					ForEach(Lane.Position.allCases) {
						Text(String(describing: $0)).tag($0)
					}
				}
			}
			.swipeActions(allowsFullSwipe: true) {
				DeleteButton { viewStore.send(.didSwipe(.delete)) }
			}
		}
	}
}

extension LaneEditor.State {
	var view: LaneEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.label = newValue.label
			self.position = newValue.position
		}
	}
}

extension LaneEditor.Action {
	init(action: LaneEditorView.ViewAction) {
		switch action {
		case let .didSwipe(swipeAction):
			self = .view(.didSwipe(swipeAction))
		case let .binding(action):
			self = .binding(action.pullback(\LaneEditor.State.view))
		}
	}
}
