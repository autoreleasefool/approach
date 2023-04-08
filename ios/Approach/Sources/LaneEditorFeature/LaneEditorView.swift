import AssetsLibrary
import ComposableArchitecture
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct LaneEditorView: View {
	let store: StoreOf<LaneEditor>

	struct ViewState: Equatable {
		@BindingState var label: String
		@BindingState var isAgainstWall: Bool

		init(state: LaneEditor.State) {
			self.label = state.label
			self.isAgainstWall = state.isAgainstWall
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

				Toggle(
					Strings.Lane.Properties.isAgainstWall,
					isOn: viewStore.binding(\.$isAgainstWall)
				)
				.toggleStyle(SwitchToggleStyle())
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
			self.isAgainstWall = newValue.isAgainstWall
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

#if DEBUG
struct LaneEditorViewPreview: PreviewProvider {
	static var previews: some View {
		List {
			LaneEditorView(
				store: .init(
					initialState: .init(id: UUID()),
					reducer: LaneEditor()
				)
			)

			LaneEditorView(
				store: .init(
					initialState: .init(id: UUID()),
					reducer: LaneEditor()
				)
			)
		}
	}
}
#endif
