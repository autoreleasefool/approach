import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import ScoringServiceInterface
import SwiftUI
import ViewsLibrary

public struct ScoreSheetView: View {
	let store: StoreOf<ScoreSheet>

	struct ViewState: Equatable {
		let steps: [ScoreStep]
		let currentFrameIndex: Int
		let currentRollIndex: Int

		init(state: ScoreSheet.State) {
			self.steps = state.steps
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
				Grid(horizontalSpacing: 0, verticalSpacing: 0) {
					GridRow {
						ForEach(viewStore.steps, id: \.index) { step in
							rollViews(
								forRolls: step.rolls,
								frameIndex: step.index
							) { rollIndex in
								viewStore.send(.didTapFrame(index: step.index, rollIndex: rollIndex))
							}
						}
					}
					Divider()
					GridRow {
						ForEach(viewStore.steps, id: \.index) { step in
							stepView(step) {
								viewStore.send(.didTapFrame(index: step.index, rollIndex: nil))
							}
							.gridCellColumns(Frame.NUMBER_OF_ROLLS)
						}
					}
					GridRow {
						ForEach(viewStore.steps, id: \.index) { step in
							Text(String(step.index + 1))
								.padding()
								.gridCellColumns(Frame.NUMBER_OF_ROLLS)
						}
					}
				}
			}
			.scrollIndicators(.hidden)
			.background(Color.appPrimaryLight.cornerRadius(.standardRadius))
			.padding()
		}
	}

	private func stepView(_ step: ScoreStep, action: @escaping () -> Void) -> some View {
		Button(action: action) {
			Text(step.display)
				.padding(.horizontal, .smallSpacing)
				.padding(.vertical, .tinySpacing)
		}
	}

	private func rollViews(
		forRolls: [ScoreStep.RollStep],
		frameIndex: Int,
		action: @escaping (Int) -> Void
	) -> some View {
		ForEach(forRolls, id: \.index) { roll in
			Button {
				action(roll.index)
			} label: {
				Text(roll.display)
					.padding(.horizontal, .smallSpacing)
					.padding(.vertical, .tinySpacing)
			}
		}
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

#if DEBUG
struct ScoreSheetViewPreviews: PreviewProvider {
	static var previews: some View {
		ScoreSheetView(store: .init(
			initialState: .init(
				steps: (0..<Game.NUMBER_OF_FRAMES).map {
					.init(
						index: $0,
						rolls: [
							.init(index: 0, display: "1", didFoul: false),
							.init(index: 0, display: "2", didFoul: false),
							.init(index: 0, display: "3", didFoul: false),
						],
						score: nil
					)
				},
				currentFrameIndex: 0,
				currentRollIndex: 0
			),
			reducer: ScoreSheet()
		))
	}
}
#endif
