import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct RollEditorView: View {
	let store: StoreOf<RollEditor>

	struct ViewState: Equatable {
		let ballRolled: Gear.Rolled?
		let didFoul: Bool

		init(state: RollEditor.State) {
			self.ballRolled = state.ballRolled
			self.didFoul = state.didFoul
		}
	}

	enum ViewAction {
		case didTapBall
		case didToggleFoul
	}

	init(store: StoreOf<RollEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: RollEditor.Action.init) { viewStore in
			HStack(alignment: .bottom) {
				Button { viewStore.send(.didTapBall) } label: {
					VStack(alignment: .leading, spacing: .tinySpacing) {
						Text(Strings.Roll.Properties.Ball.title)
							.font(.caption)
							.italic()
							.foregroundColor(.white)
						Text(viewStore.ballRolled?.name ?? Strings.Roll.Properties.Ball.noneSelected)
							.foregroundColor(.white)
					}
				}
				.buttonStyle(TappableElement())

				Spacer()

				Button { viewStore.send(.didToggleFoul) } label: {
					HStack(spacing: .smallSpacing) {
						Text(Strings.Roll.Properties.Foul.title)
							.foregroundColor(viewStore.didFoul ? .appError : .white)
						Image(systemName: viewStore.didFoul ? "f.cursive.circle.fill" : "f.cursive.circle")
							.resizable()
							.frame(width: .smallIcon, height: .smallIcon)
							.foregroundColor(viewStore.didFoul ? .appError : .white)
					}
				}
				.buttonStyle(TappableElement())
			}
		}
	}
}

extension RollEditor.Action {
	init(action: RollEditorView.ViewAction) {
		switch action {
		case .didToggleFoul:
			self = .view(.didToggleFoul)
		case .didTapBall:
			self = .delegate(.didTapBall)
		}
	}
}
