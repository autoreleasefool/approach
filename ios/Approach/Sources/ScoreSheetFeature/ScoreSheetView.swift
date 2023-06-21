import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import ScoringServiceInterface
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct ScoreSheetView: View {
	let store: StoreOf<ScoreSheet>

	@State private var contentSize: CGSize = .zero

	struct ViewState: Equatable {
		let steps: [ScoreStep]
		let currentFrameIndex: Int
		let currentRollIndex: Int

		var rollId: RollID {
			.init(
				frameIndex: currentFrameIndex > 0 ? currentFrameIndex - 1 : 0,
				rollIndex: currentFrameIndex == 0 ? 0 : 1
			)
		}

		init(state: ScoreSheet.State) {
			self.steps = state.steps
			self.currentFrameIndex = state.currentFrameIndex
			self.currentRollIndex = state.currentRollIndex
		}
	}

	enum ViewAction {
		case didTapFrame(index: Int, rollIndex: Int?)
	}

	struct FrameID: Hashable {
		let index: Int
	}

	struct RollID: Hashable {
		let frameIndex: Int
		let rollIndex: Int
	}

	public init(store: StoreOf<ScoreSheet>) {
		self.store = store
	}

	public var body: some View {
		ScrollViewReader { proxy in
			WithViewStore(store, observe: ViewState.init, send: ScoreSheet.Action.init) { viewStore in
				ScrollView(.horizontal) {
					Grid(horizontalSpacing: 0, verticalSpacing: 0) {
						GridRow {
							ForEach(viewStore.steps, id: \.index) { step in
								rollViews(
									forRolls: step.rolls,
									frameIndex: step.index,
									highlightRollIndex: viewStore.currentFrameIndex == step.index ? viewStore.currentRollIndex : nil
								) { rollIndex in
									viewStore.send(.didTapFrame(index: step.index, rollIndex: rollIndex))
								}
							}
						}
						Divider()
						GridRow {
							ForEach(viewStore.steps, id: \.index) { step in
								stepView(step, highlighted: viewStore.currentFrameIndex == step.index) {
									viewStore.send(.didTapFrame(index: step.index, rollIndex: nil))
								}
								.id(FrameID(index: step.index))
								.gridCellColumns(Frame.NUMBER_OF_ROLLS)
							}
						}

						GridRow {
							ForEach(viewStore.steps, id: \.index) { step in
								Text(String(step.index + 1))
									.font(.caption2)
									.foregroundColor(step.index == viewStore.currentFrameIndex ? .black : .gray)
									.padding(.bottom, .tinySpacing)
									.gridCellColumns(Frame.NUMBER_OF_ROLLS)
							}
						}
					}
				}
				.background(Asset.Colors.Primary.light)
				.cornerRadius(.standardRadius)
				.scrollIndicators(.hidden)
				.onChange(of: viewStore.rollId) { rollId in
					withAnimation(.easeInOut(duration: 300)) {
						proxy.scrollTo(rollId, anchor: .leading)
					}
				}
			}
			.measure(key: ContentSizeKey.self, to: $contentSize)
		}
	}

	private func stepView(_ step: ScoreStep, highlighted: Bool, action: @escaping () -> Void) -> some View {
		Button(action: action) {
			Text(step.display ?? " ")
				.frame(maxWidth: .infinity)
				.padding(.horizontal, .smallSpacing)
				.padding(.vertical, .smallSpacing)
				.background(highlighted ? Asset.Colors.Primary.default : Asset.Colors.Primary.light)
		}
		.contentShape(Rectangle())
		.buttonStyle(TappableElement())
	}

	private func rollViews(
		forRolls: [ScoreStep.RollStep],
		frameIndex: Int,
		highlightRollIndex: Int?,
		action: @escaping (Int) -> Void
	) -> some View {
		ForEach(forRolls, id: \.index) { roll in
			Button {
				action(roll.index)
			} label: {
				Text(roll.display ?? " ")
					.font(.caption)
					.minimumScaleFactor(0.2)
					.lineLimit(1)
					.frame(width: contentSize.width / 12)
					.padding(.horizontal, .smallSpacing)
					.padding(.vertical, .smallSpacing)
					.background(highlightRollIndex == roll.index ? Asset.Colors.Primary.default : Asset.Colors.Primary.light)
			}
			.id(RollID(frameIndex: frameIndex, rollIndex: roll.index))
			.contentShape(Rectangle())
			.buttonStyle(TappableElement())
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

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}

#if DEBUG
struct ScoreSheetViewPreviews: PreviewProvider {
	static var previews: some View {
		ScoreSheetView(store: .init(
			initialState: .init(
				steps: Game.FRAME_INDICES.map {
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
