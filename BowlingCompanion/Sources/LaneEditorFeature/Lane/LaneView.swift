import ComposableArchitecture
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ThemesLibrary

public struct LaneView: View {
	let store: StoreOf<Lane>

	struct ViewState: Equatable {
		@BindableState var label: String
		@BindableState var isAgainstWall: Bool
		let isShowingAgainstWallNotice: Bool
		let isValid: Bool

		init(state: Lane.State) {
			self.label = state.label
			self.isAgainstWall = state.isAgainstWall
			self.isValid = state.isValid
			self.isShowingAgainstWallNotice = state.isShowingAgainstWallNotice
		}
	}

	enum ViewAction: BindableAction {
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<Lane>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: Lane.Action.init) { viewStore in
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

extension Lane.State {
	var view: LaneView.ViewState {
		get { .init(state: self) }
		set {
			self.label = newValue.label
			self.isAgainstWall = newValue.isAgainstWall
		}
	}
}

extension Lane.Action {
	init(action: LaneView.ViewAction) {
		switch action {
		case let .binding(action):
			self = .binding(action.pullback(\Lane.State.view))
		}
	}
}

#if DEBUG
struct LaneViewPreview: PreviewProvider {
	static var previews: some View {
		List {
			LaneView(
				store: .init(
					initialState: .init(id: UUID(), isShowingAgainstWallNotice: true),
					reducer: Lane()
				)
			)

			LaneView(
				store: .init(
					initialState: .init(id: UUID(), isShowingAgainstWallNotice: false),
					reducer: Lane()
				)
			)
		}
	}
}
#endif
