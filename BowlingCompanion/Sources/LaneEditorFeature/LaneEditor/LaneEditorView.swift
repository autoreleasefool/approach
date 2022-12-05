import ComposableArchitecture
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ThemesLibrary

public struct LaneEditorView: View {
	let store: StoreOf<LaneEditor>

	struct ViewState: Equatable {
		@BindableState var label: String
		@BindableState var isAgainstWall: Bool
		let isShowingAgainstWallNotice: Bool
		let isValid: Bool

		init(state: LaneEditor.State) {
			self.label = state.label
			self.isAgainstWall = state.isAgainstWall
			self.isValid = state.isValid
			self.isShowingAgainstWallNotice = state.isShowingAgainstWallNotice
		}
	}

	enum ViewAction: BindableAction {
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<LaneEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LaneEditor.Action.init) { viewStore in
			Section {
				VStack {
					HStack {
						TextField(
							Strings.Lanes.Editor.Fields.label,
							text: viewStore.binding(\.$label)
						)
						.keyboardType(.numberPad)
						.submitLabel(.done)
						.if(!viewStore.isValid) { view in
							view.foregroundColor(.appDestructive)
						}

						if !viewStore.isValid {
							Image(systemName: "exclamationmark.triangle.fill")
								.resizable()
								.frame(width: .smallIcon, height: .smallIcon)
								.foregroundColor(.appDestructive)
						}
					}

					Toggle(
						Strings.Lanes.Editor.Fields.IsAgainstWall.title,
						isOn: viewStore.binding(\.$isAgainstWall)
					)
					.toggleStyle(SwitchToggleStyle())
				}
			} footer: {
				if (viewStore.isShowingAgainstWallNotice) {
					Text(Strings.Lanes.Editor.Fields.IsAgainstWall.help)
				}
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
					initialState: .init(id: UUID(), isShowingAgainstWallNotice: true),
					reducer: LaneEditor()
				)
			)

			LaneEditorView(
				store: .init(
					initialState: .init(id: UUID(), isShowingAgainstWallNotice: false),
					reducer: LaneEditor()
				)
			)
		}
	}
}
#endif
