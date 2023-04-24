import ComposableArchitecture
import ModelsLibrary
import SwiftUI
import ViewsLibrary

public struct ScoreSheetView: View {
	let store: StoreOf<ScoreSheet>

	struct ViewState: Equatable {
		let data: ScoreSheet.DataSource
		let currentFrameIndex: Int
		let currentRollIndex: Int

		init(state: ScoreSheet.State) {
			self.data = state.data
			self.currentFrameIndex = state.currentFrameIndex
			self.currentRollIndex = state.currentRollIndex
		}
	}

	enum ViewAction {
		case didTapFrame(index: Int, rollIndex: Int?)
	}

	public init(store: StoreOf<ScoreSheet>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: ScoreSheet.Action.init) { viewStore in
			ScrollView(.horizontal) {
				HStack {
					switch viewStore.data {
					case let .edits(frames):
						ForEach(frames) { frame in
							frameView(ordinal: frame.ordinal, rolls: frame.rolls, viewStore: viewStore)
						}
					case let .summaries(frames):
						ForEach(frames) { frame in
							frameView(ordinal: frame.ordinal, rolls: frame.rolls, viewStore: viewStore)
						}
					}
				}
			}
		}
	}

	@ViewBuilder private func frameView(
		ordinal: Int,
		rolls: [Frame.Roll],
		viewStore: ViewStore<ViewState, ViewAction>
	) -> some View {
		Button { viewStore.send(.didTapFrame(index: ordinal - 1, rollIndex: nil)) } label: {
			VStack {
				HStack {
					Button { viewStore.send(.didTapFrame(index: ordinal - 1, rollIndex: 0)) } label: {
						Text("5")
					}
				}
			}
		}
		.buttonStyle(TappableElement())
	}
}

extension ScoreSheet.Action {
	init(action: ScoreSheetView.ViewAction) {
		switch action {
		case let .didTapFrame(frameIndex, rollIndex):
			self = .view(.didTapFrame(index: frameIndex, rollIndex: rollIndex))
		}
	}
}
