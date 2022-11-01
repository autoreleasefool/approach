import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI

public struct ScoreSheetView: View {
	let store: StoreOf<ScoreSheet>

	struct ViewState: Equatable {
		let frames: IdentifiedArrayOf<Frame>
		let selection: Frame.ID?

		init(state: ScoreSheet.State) {
			self.frames = state.frames
			self.selection = state.selection
		}
	}

	enum ViewAction {
		case setFrame(id: Frame.ID?)
	}

	public init(store: StoreOf<ScoreSheet>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: ScoreSheet.Action.init) { viewStore in
			HStack {
				ForEach(viewStore.frames) { frame in
					VStack {
						HStack {
							ball(frame.firstBall)
							ball(frame.secondBall)
							ball(frame.thirdBall)
						}

						Text("148")
					}
				}
			}
		}
	}

	private func ball(_ ball: Frame.Ball?) -> some View {
		Text(ball?.deck.displayValue ?? "N/A")
	}
}

extension ScoreSheet.Action {
	init(action: ScoreSheetView.ViewAction) {
		switch action {
		case .setFrame(let id):
			self = .setFrame(id: id)
		}
	}
}

#if DEBUG
struct ScoreSheetViewPreviews: PreviewProvider {
	static var previews: some View {
		ScoreSheetView(
			store: .init(
				initialState: .init(),
				reducer: ScoreSheet()
			)
		)
	}
}
#endif
