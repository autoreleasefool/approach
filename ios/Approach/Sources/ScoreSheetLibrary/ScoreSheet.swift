import AssetsLibrary
import ModelsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

public struct ScoreSheet: View {
	let game: ScoredGame
	let configuration: Configuration
	let contentSize: CGSize
	@Binding var selection: Selection

	public init(
		game: ScoredGame,
		configuration: Configuration,
		contentSize: CGSize,
		selection: Binding<Selection>
	) {
		self.game = game
		self.configuration = configuration
		self.contentSize = contentSize
		self._selection = selection
	}

	public struct Selection: Hashable, Sendable {
		public var frameIndex: Int
		public var rollIndex: Int

		public init(frameIndex: Int, rollIndex: Int) {
			self.frameIndex = frameIndex
			self.rollIndex = rollIndex
		}

		var isLast: Bool { Frame.isLast(frameIndex) }
		var frameId: FrameID { .init(frameIndex: frameIndex) }

		public static let unselected: Self = .init(frameIndex: -1, rollIndex: -1)
	}

	struct FrameID: Hashable {
		let frameIndex: Int
	}

	public var body: some View {
		HStack(alignment: .center, spacing: 0) {
			Grid(horizontalSpacing: 0, verticalSpacing: 0) {
				if configuration.railOnTop {
					GridRow {
						ForEach(game.frames, id: \.index) { frame in
							RailView(frame: frame, highlight: selection.frameIndex == frame.index)
								.gridCellColumns(Frame.NUMBER_OF_ROLLS)
						}
					}
				}

				GridRow {
					ForEach(game.frames, id: \.index) { frame in
						rollViews(forFrame: frame)
					}
				}

				GridRow {
					ForEach(game.frames, id: \.index) { frame in
						FrameView(frame: frame, highlight: selection.frameIndex == frame.index) {
							selection = .init(frameIndex: frame.index, rollIndex: 0)
						}
						.gridCellColumns(Frame.NUMBER_OF_ROLLS)
					}
				}

				if !configuration.railOnTop {
					GridRow {
						ForEach(game.frames, id: \.index) { frame in
							RailView(frame: frame, highlight: selection.frameIndex == frame.index)
								.gridCellColumns(Frame.NUMBER_OF_ROLLS)
						}
					}
				}
			}
			.frame(maxHeight: .infinity)

			FinalScoreView(score: game.score ?? 0)
		}
		.fixedSize(horizontal: false, vertical: true)
	}

	private func rollViews(forFrame frame: ScoredFrame) -> some View {
		ForEach(frame.rolls, id: \.index) { roll in
			RollView(
				frame: frame,
				roll: roll,
				highlight: selection.frameIndex == frame.index && selection.rollIndex == roll.index,
				width: contentSize.width / 10
			) {
				selection = .init(frameIndex: frame.index, rollIndex: roll.index)
			}
		}
	}
}

public struct FrameView: View {
	@Environment(\.scoreSheetConfiguration) var configuration: ScoreSheet.Configuration

	let frame: ScoredFrame
	let highlight: Bool
	let onTap: (() -> Void)?

	public init(frame: ScoredFrame, highlight: Bool = false, onTap: (() -> Void)? = nil) {
		self.frame = frame
		self.highlight = highlight
		self.onTap = onTap
	}

	public var body: some View {
		Button { onTap?() } label: {
			Text(frame.displayValue ?? " ")
				.frame(maxWidth: .infinity)
				.padding(.smallSpacing)
				.foregroundStyle(configuration.foreground(highlight: highlight))
				.background(configuration.background(highlight: highlight))
				.roundCorners(
					topLeading: configuration.shouldRound(.frame, inPosition: .topLeading, frameIndex: frame.index),
					bottomLeading: configuration.shouldRound(.frame, inPosition: .bottomLeading, frameIndex: frame.index)
				)
		}
		.disabled(onTap == nil)
		.id(ScoreSheet.FrameID(frameIndex: frame.index))
		.buttonStyle(TappableElement())
		.border(edges: frame.index == 0 ? [] : [.leading], color: configuration.border)
	}
}

public struct RollView: View {
	@Environment(\.scoreSheetConfiguration) var configuration: ScoreSheet.Configuration

	let frame: ScoredFrame
	let roll: ScoredRoll
	let highlight: Bool
	let width: CGFloat?
	let onTap: (() -> Void)?

	public init(
		frame: ScoredFrame,
		roll: ScoredRoll,
		highlight: Bool = false,
		width: CGFloat? = nil,
		onTap: (() -> Void)? = nil
	) {
		self.frame = frame
		self.roll = roll
		self.highlight = highlight
		self.width = width
		self.onTap = onTap
	}

	public var body: some View {
		Button { onTap?() } label: {
			Text(roll.displayValue ?? " ")
				.multilineTextAlignment(.center)
				.font(.caption)
				.minimumScaleFactor(0.2)
				.lineLimit(1)
				.frame(width: width)
				.padding(.vertical, .unitSpacing)
				.foregroundStyle(
					configuration.foreground(
						highlight: highlight,
						didFoul: roll.didFoul,
						isSecondary: roll.isSecondary
					)
				)
				.background(configuration.background(highlight: highlight))
				.roundCorners(
					topLeading: configuration.shouldRound(
						.roll,
						inPosition: .topLeading,
						frameIndex: frame.index,
						rollIndex: roll.index
					),
					bottomLeading: configuration.shouldRound(
						.roll,
						inPosition: .bottomLeading,
						frameIndex: frame.index,
						rollIndex: roll.index
					)
				)
		}
		.disabled(onTap == nil)
		.id(ScoreSheet.Selection(frameIndex: frame.index, rollIndex: roll.index))
		.buttonStyle(TappableElement())
		.border(
			edges: [
				.bottom,
				Frame.isLastRoll(roll.index) ? nil : .trailing,
				frame.index != 0 && roll.index == 0 ? .leading : nil,
			].compactMap { $0 },
			color: configuration.border
		)
	}
}

public struct RailView: View {
	@Environment(\.scoreSheetConfiguration) var configuration: ScoreSheet.Configuration

	let frame: ScoredFrame
	let highlight: Bool

	public init(frame: ScoredFrame, highlight: Bool = false) {
		self.frame = frame
		self.highlight = highlight
	}

	public var body: some View {
		Text(String(frame.index + 1))
			.font(.caption2)
			.frame(maxWidth: .infinity)
			.padding(.vertical, .unitSpacing)
			.foregroundStyle(configuration.railForeground(highlight: highlight))
			.background(configuration.railBackground(highlight: highlight))
			.border(edges: frame.index == 0 ? [] : [.leading], color: configuration.border)
			.roundCorners(
				topLeading: configuration.shouldRound(.rail, inPosition: .topLeading, frameIndex: frame.index),
				bottomLeading: configuration.shouldRound(.rail, inPosition: .bottomLeading, frameIndex: frame.index)
			)
	}
}

public struct FinalScoreView: View {
	@Environment(\.scoreSheetConfiguration) var configuration: ScoreSheet.Configuration

	let score: Int
	let width: CGFloat?

	public init(score: Int, width: CGFloat? = nil) {
		self.score = score
		self.width = width
	}

	public var body: some View {
		Text("\(score)")
			.font(.title2)
			.bold()
			.padding(.vertical, .smallSpacing)
			.padding(.horizontal, .largeSpacing)
			.frame(width: width)
			.frame(maxHeight: .infinity)
			.foregroundStyle(configuration.foreground)
			.background(configuration.background)
			.roundCorners(
				topTrailing: configuration.shouldRound(.score, inPosition: .topTrailing),
				bottomTrailing: configuration.shouldRound(.score, inPosition: .bottomTrailing)
			)
			.border(edges: [.leading], color: configuration.border)
	}
}

#Preview(traits: .sizeThatFitsLayout) {
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
				}
			),
			configuration: .plain,
			contentSize: CGSize(width: 400, height: 200),
			selection: .constant(.init(frameIndex: 0, rollIndex: 0))
		)
		Spacer()
	}
}
