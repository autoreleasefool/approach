import AssetsLibrary
import ModelsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
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
		public var frameIndex: Int
		public var rollIndex: Int

		public init(frameIndex: Int, rollIndex: Int) {
			self.frameIndex = frameIndex
			self.rollIndex = rollIndex
		}

		var isLast: Bool { Frame.isLast(frameIndex) }
		var frameId: FrameID { .init(frameIndex: frameIndex) }

		public static let none: Self = .init(frameIndex: -1, rollIndex: -1)
	}

	struct FrameID: Hashable {
		let frameIndex: Int
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
			.onChange(of: selection) {
				withAnimation(.easeInOut(duration: 300)) {
					proxy.scrollTo(selection.frameId, anchor: selection.isLast ? .trailing : .leading)
				}
			}
			.onAppear {
				withAnimation(.easeInOut(duration: 300)) {
					proxy.scrollTo(selection.frameId, anchor: selection.isLast ? .trailing : .leading)
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
		.id(FrameID(frameIndex: frame.index))
		.buttonStyle(TappableElement())
		.border(edges: frame.index == 0 ? [] : [.leading])
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
					.foregroundColor(
						foreground(
							forRollIndex: roll.index,
							inFrame: frame.index,
							didFoul: roll.didFoul,
							isSecondary: roll.isSecondary
						)
					)
					.background(background(forRollIndex: roll.index, inFrame: frame.index))
					.roundCorners(
						topLeading: frame.index == 0 && roll.index == 0,
						topTrailing: Frame.isLast(frame.index) && Frame.isLastRoll(roll.index)
					)
			}
			.id(Selection(frameIndex: frame.index, rollIndex: roll.index))
			.buttonStyle(TappableElement())
			.border(
				edges: [
					.bottom,
					Frame.isLastRoll(roll.index) ? nil : .trailing,
					frame.index != 0 && roll.index == 0 ? .leading : nil,
				].compactMap { $0 }
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
			.border(edges: frame.index == 0 ? [] : [.leading])
			.roundCorners(
				bottomLeading: frame.index == 0,
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

	private func foreground(
		forRollIndex: Int,
		inFrame: Int,
		didFoul: Bool,
		isSecondary: Bool
	) -> ColorAsset {
		if isSecondary {
			return Asset.Colors.ScoreSheet.Text.OnBackground.secondary
		}

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
								.init(index: 0, displayValue: "HP", didFoul: true, isSecondary: false),
								.init(index: 1, displayValue: "10", didFoul: false, isSecondary: false),
								.init(index: 2, displayValue: "-", didFoul: false, isSecondary: false),
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
