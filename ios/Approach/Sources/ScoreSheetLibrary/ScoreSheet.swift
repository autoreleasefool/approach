import AssetsLibrary
import ModelsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct ScoreSheet: View {
	@State private var contentSize: CGSize = .zero

	let game: ScoredGame
	@Binding var selection: Selection

	public init(game: ScoredGame, selection: Binding<Selection>) {
		self.game = game
		self._selection = selection
	}

	public struct Selection: Hashable {
		let frameIndex: Int
		let rollIndex: Int

		var isLast: Bool { Frame.isLast(frameIndex) }
	}

	public var body: some View {
		ScrollViewReader { proxy in
			ScrollView(.horizontal, showsIndicators: false) {
				Grid(horizontalSpacing: 0, verticalSpacing: 0) {
					GridRow {
						ForEach(game.frames, id: \.index) { frame in
							rollViews(forFrame: frame)
						}
					}

					GridRow {
						ForEach(game.frames, id: \.index) { frame in
							frameView(forFrame: frame)
								.gridCellColumns(Frame.NUMBER_OF_ROLLS)
						}
					}

					GridRow {
						ForEach(game.frames, id: \.index) { frame in
							railView(forFrame: frame)
								.gridCellColumns(Frame.NUMBER_OF_ROLLS)
						}
					}
				}
			}
			.cornerRadius(.standardRadius)
			.onChange(of: selection) { selection in
				withAnimation(.easeInOut(duration: 300)) {
					proxy.scrollTo(selection, anchor: selection.isLast ? .trailing : .leading)
				}
			}
			.onAppear {
				withAnimation(.easeInOut(duration: 300)) {
					proxy.scrollTo(selection, anchor: selection.isLast ? .trailing : .leading)
				}
			}
			.measure(key: ContentSizeKey.self, to: $contentSize)
		}
	}

	private func frameView(forFrame frame: ScoredFrame) -> some View {
		Button { selection = .init(frameIndex: frame.index, rollIndex: 0) } label: {
			Text(frame.displayValue ?? " ")
				.frame(maxWidth: .infinity)
				.padding(.smallSpacing)
				.foregroundColor(foreground(forFrameIndex: frame.index))
				.background(background(forFrameIndex: frame.index))
		}
		.buttonStyle(TappableElement())
		.borders(leading: frame.index != 0)
	}

	private func rollViews(forFrame frame: ScoredFrame) -> some View {
		ForEach(frame.rolls, id: \.index) { roll in
			Button { selection = .init(frameIndex: frame.index, rollIndex: roll.index) } label: {
				Text(roll.displayValue ?? " ")
					.font(.caption)
					.minimumScaleFactor(0.2)
					.lineLimit(1)
					.frame(width: contentSize.width / 12)
					.padding(.smallSpacing)
					.foregroundColor(foreground(forRollIndex: roll.index, inFrame: frame.index, didFoul: roll.didFoul))
					.background(background(forRollIndex: roll.index, inFrame: frame.index))
					.roundCorners(
						topLeading: frame.index == 0 && roll.index == 0,
						topTrailing: Frame.isLast(frame.index) && Frame.Roll.isLast(roll.index)
					)
			}
			.id(Selection(frameIndex: frame.index, rollIndex: roll.index))
			.buttonStyle(TappableElement())
			.borders(
				trailing: !Frame.Roll.isLast(roll.index),
				bottom: true,
				leading: frame.index != 0 && roll.index == 0
			)
		}
	}

	private func railView(forFrame frame: ScoredFrame) -> some View {
		Text(String(frame.index + 1))
			.font(.caption2)
			.frame(maxWidth: .infinity)
			.padding(.vertical, .unitSpacing)
			.foregroundColor(foreground(forRailInFrame: frame.index))
			.background(background(forRailInFrame: frame.index))
			.borders(leading: frame.index != 0)
			.roundCorners(
				bottomLeading: frame.index != 0,
				bottomTrailing: Frame.isLast(frame.index)
			)
	}

	// MARK: - Colors

	private func foreground(forFrameIndex: Int) -> ColorAsset {
		if selection.frameIndex == forFrameIndex {
			return Asset.Colors.ScoreSheet.Text.OnBackground.highlight
		} else {
			return Asset.Colors.ScoreSheet.Text.OnBackground.default
		}
	}

	private func background(forFrameIndex: Int) -> ColorAsset {
		if selection.frameIndex == forFrameIndex {
			return Asset.Colors.ScoreSheet.Background.highlight
		} else {
			return Asset.Colors.ScoreSheet.Background.default
		}
	}

	private func foreground(forRollIndex: Int, inFrame: Int, didFoul: Bool) -> ColorAsset {
		if selection.frameIndex == inFrame && selection.rollIndex == forRollIndex {
			if didFoul {
				return Asset.Colors.ScoreSheet.Text.OnBackground.highlightFoul
			} else {
				return Asset.Colors.ScoreSheet.Text.OnBackground.highlight
			}
		} else {
			if didFoul {
				return Asset.Colors.ScoreSheet.Text.OnBackground.foul
			} else {
				return Asset.Colors.ScoreSheet.Text.OnBackground.default
			}
		}
	}

	private func background(forRollIndex: Int, inFrame: Int) -> ColorAsset {
		if selection.frameIndex == inFrame && selection.rollIndex == forRollIndex {
			return Asset.Colors.ScoreSheet.Background.highlight
		} else {
			return Asset.Colors.ScoreSheet.Background.default
		}
	}

	private func foreground(forRailInFrame: Int) -> ColorAsset {
		if selection.frameIndex == forRailInFrame {
			return Asset.Colors.ScoreSheet.Text.OnRail.highlight
		} else {
			return Asset.Colors.ScoreSheet.Text.OnRail.default
		}
	}

	private func background(forRailInFrame: Int) -> ColorAsset {
		if selection.frameIndex == forRailInFrame {
			return Asset.Colors.ScoreSheet.Rail.highlight
		} else {
			return Asset.Colors.ScoreSheet.Rail.default
		}
	}
}

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}

#if DEBUG
struct ScoreSheetPreviews: PreviewProvider {
	static var previews: some View {
		VStack {
			Spacer()
			ScoreSheet(
				game: .init(
					id: UUID(uuidString: "00000000-0000-0000-0000-000000000000")!,
					index: 0,
					frames: Game.FRAME_INDICES.map {
						.init(
							index: $0,
							rolls: [
								.init(index: 0, displayValue: "HP", didFoul: true),
								.init(index: 0, displayValue: "10", didFoul: false),
								.init(index: 0, displayValue: "-", didFoul: false),
							],
							score: 255
						)
					}),
				selection: .constant(.init(frameIndex: 0, rollIndex: 0))
			)
			Spacer()
		}
		.background(.black)
	}
}
#endif
