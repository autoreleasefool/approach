import ComposableArchitecture
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ThemesLibrary

public struct LaneView: View {
	let store: StoreOf<Lane>

	struct ViewState: Equatable {
		@BindableState var label: String
		let isValid: Bool

		init(state: Lane.State) {
			self.label = state.label
			self.isValid = state.isValid
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
			HStack {
				TextField(
					Strings.Lanes.Editor.placeholder,
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
		}
	}
}

extension Lane.State {
	var view: LaneView.ViewState {
		get { .init(state: self) }
		set {
			self.label = newValue.label
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
					initialState: .init(id: UUID()),
					reducer: Lane()
				)
			)
		}
	}
}
#endif
