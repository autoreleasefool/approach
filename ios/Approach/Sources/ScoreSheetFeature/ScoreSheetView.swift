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
			let rollIndex: Int
			let frameIndex: Int
			if currentFrameIndex == 0 {
				frameIndex = 0
				rollIndex = 0
			} else if Frame.isLast(currentFrameIndex) {
				frameIndex = currentFrameIndex
				rollIndex = 2
			} else {
				frameIndex = currentFrameIndex - 1
				rollIndex = 1
			}
			return .init(
				frameIndex: frameIndex,
				rollIndex: rollIndex
			)
		}

		init(state: ScoreSheet.State) {
			self.steps = state.steps
			self.currentFrameIndex = state.currentFrameIndex
			self.currentRollIndex = state.currentRollIndex
		}
	}

	struct FrameID: Hashable {
		let index: Int
	}

	struct RollID: Hashable {
		let frameIndex: Int
		let rollIndex: Int

		var isLast: Bool { Frame.isLast(frameIndex) }
	}

	public init(store: StoreOf<ScoreSheet>) {
		self.store = store
	}

	public var body: some View {
		ScrollViewReader { proxy in
			WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
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
								railLabel(step, highlighted: viewStore.currentFrameIndex == step.index)
							}
						}
					}
				}
				.cornerRadius(.standardRadius)
				.scrollIndicators(.hidden)
				.onChange(of: viewStore.rollId) { rollId in
					withAnimation(.easeInOut(duration: 300)) {
						if rollId.isLast {
							proxy.scrollTo(rollId, anchor: .trailing)
						} else {
							proxy.scrollTo(rollId, anchor: .leading)
						}
					}
				}
				.onAppear {
					withAnimation(.easeInOut(duration: 300)) {
						let rollId = viewStore.rollId
						if rollId.isLast {
							proxy.scrollTo(rollId, anchor: .trailing)
						} else {
							proxy.scrollTo(rollId, anchor: .leading)
						}
					}
				}
			})
			.measure(key: ContentSizeKey.self, to: $contentSize)
		}
	}

	private func stepView(_ step: ScoreStep, highlighted: Bool, action: @escaping () -> Void) -> some View {
		Button(action: action) {
			Text(step.display ?? " ")
				.frame(maxWidth: .infinity)
				.padding(.smallSpacing)
				.foregroundColor(
					highlighted
					? Asset.Colors.ScoreSheet.Text.highlight
					: Asset.Colors.ScoreSheet.Text.default
				)
				.background(
					highlighted
					? Asset.Colors.ScoreSheet.Background.highlight
					: Asset.Colors.ScoreSheet.Background.default
				)
		}
		.contentShape(Rectangle())
		.buttonStyle(TappableElement())
		.borders(leading: step.index != 0)
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
				.padding(.smallSpacing)
				.foregroundColor(
					highlightRollIndex == roll.index
					? roll.didFoul ? Asset.Colors.ScoreSheet.Text.highlightFoul : Asset.Colors.ScoreSheet.Text.highlight
					: roll.didFoul ? Asset.Colors.ScoreSheet.Text.foul : Asset.Colors.ScoreSheet.Text.default
				)
				.background(
					highlightRollIndex == roll.index
					? Asset.Colors.ScoreSheet.Background.highlight
					: Asset.Colors.ScoreSheet.Background.default
				)
				.roundCorners(
					topLeading: frameIndex == 0 && roll.index == 0,
					topTrailing: Frame.isLast(frameIndex) && Frame.Roll.isLast(roll.index)
				)
			}
			.id(RollID(frameIndex: frameIndex, rollIndex: roll.index))
			.contentShape(Rectangle())
			.buttonStyle(TappableElement())
			.borders(trailing: !Frame.Roll.isLast(roll.index), bottom: true, leading: frameIndex != 0 && roll.index == 0)
		}
	}

	private func railLabel(_ step: ScoreStep, highlighted: Bool) -> some View {
		Text(String(step.index + 1))
			.font(.caption2)
			.foregroundColor(
				highlighted
				? Asset.Colors.ScoreSheet.Label.highlight
				: Asset.Colors.ScoreSheet.Label.default
			)
			.frame(maxWidth: .infinity)
			.foregroundColor(.white)
			.padding(.vertical, .unitSpacing)
			.gridCellColumns(Frame.NUMBER_OF_ROLLS)
			.background(
				highlighted
				? Asset.Colors.ScoreSheet.Rail.highlight
				: Asset.Colors.ScoreSheet.Rail.default
			)
			.borders(leading: step.index != 0)
			.roundCorners(
				bottomLeading: step.index == 0,
				bottomTrailing: Frame.isLast(step.index)
			)
	}
}

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}

#if DEBUG
struct ScoreSheetViewPreviews: PreviewProvider {
	static var previews: some View {
		VStack {
			Spacer()
			ScoreSheetView(store: .init(
				initialState: .init(
					steps: Game.FRAME_INDICES.map {
						.init(
							index: $0,
							rolls: [
								.init(index: 0, display: "1", didFoul: true),
								.init(index: 1, display: "2", didFoul: false),
								.init(index: 2, display: "3", didFoul: false),
							],
							score: 32
						)
					},
					currentFrameIndex: 0,
					currentRollIndex: 0
				),
				reducer: ScoreSheet.init
			))
			Spacer()
		}
		.background(.black)
	}
}
#endif
