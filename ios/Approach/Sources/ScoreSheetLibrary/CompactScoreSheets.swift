import AssetsLibrary
import DateTimeLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

public struct CompactScoreSheets: View {
	public typealias Configuration = ScoreSheetConfiguration

	let games: [ScoredGame]
	let config: Configuration

	@State private var contentSize: CGSize = .zero
	@State private var headerWidth: CGFloat = .zero
	@State private var scoreWidth: CGFloat = .zero
	@State private var rollHeight: CGFloat = .zero

	public init(games: [ScoredGame], configuration: Configuration) {
		self.games = games
		self.config = configuration
	}

	public var body: some View {
		VStack(spacing: .standardSpacing) {
			ForEach(games) { game in
				scoreSheet(forGame: game)
			}
		}
	}

	private func scoreSheet(forGame game: ScoredGame) -> some View {
		Grid(horizontalSpacing: 0, verticalSpacing: 0) {
			GridRow {
				ForEach(game.frames.prefix(Game.NUMBER_OF_FRAMES / 2), id: \.index) { frame in
					railView(forFrameIndex: frame.index)
						.gridCellColumns(Frame.NUMBER_OF_ROLLS)
				}
			}

			GridRow {
				ForEach(game.frames.prefix(Game.NUMBER_OF_FRAMES / 2), id: \.index) { frame in
					rollViews(forFrame: frame)
				}
			}

			GridRow {
				ForEach(game.frames.prefix(Game.NUMBER_OF_FRAMES / 2), id: \.index) { frame in
					frameView(forFrame: frame)
						.gridCellColumns(Frame.NUMBER_OF_ROLLS)
				}
			}

			GridRow {
				ForEach(game.frames.suffix(Game.NUMBER_OF_FRAMES / 2), id: \.index) { frame in
					rollViews(forFrame: frame)
				}
			}

			GridRow {
				ForEach(game.frames.suffix(Game.NUMBER_OF_FRAMES / 2), id: \.index) { frame in
					frameView(forFrame: frame)
						.gridCellColumns(Frame.NUMBER_OF_ROLLS)
				}
			}

			GridRow {
				ForEach(game.frames.suffix(Game.NUMBER_OF_FRAMES / 2), id: \.index) { frame in
					railView(forFrameIndex: frame.index)
						.gridCellColumns(Frame.NUMBER_OF_ROLLS)
				}
			}
		}
	}

	private func frameView(forFrame frame: ScoredFrame) -> some View {
		Text(frame.displayValue ?? " ")
			.padding(.smallSpacing)
			.frame(maxWidth: .infinity)
			.foregroundColor(config.style.textOnBackground)
			.border(edges: [.leading], color: config.style.border)
			.border(
				edges: Frame.isLast(frame.index) ? [.trailing] : [],
				width: .standardBorder * 2,
				color: config.style.border
			)
			.background(config.style.background)
	}

	private func rollViews(forFrame frame: ScoredFrame) -> some View {
		ForEach(frame.rolls, id: \.index) { roll in
			Text(roll.displayValue ?? " ")
				.font(.caption)
				.minimumScaleFactor(0.2)
				.lineLimit(1)
				.padding(.smallSpacing)
				.foregroundColor(config.style.textOnBackground)
				.matchHeight(byKey: RollHeightKey.self, to: $rollHeight)
				.border(
					edges: [
						Frame.isLastRoll(roll.index) ? nil : .trailing,
						.bottom,
						roll.index == 0 ? .leading : nil,
					].compactMap { $0 },
					color: config.style.border
				)
				.background(config.style.background)
		}
	}

	private func railView(forFrameIndex index: Int) -> some View {
		Text(String(index + 1))
			.font(.caption2)
			.frame(maxWidth: .infinity)
			.padding(.unitSpacing)
			.foregroundColor(config.style.textOnRail)
			.background(config.style.railBackground)
	}
}

private struct RollHeightKey: PreferenceKey, MatchDimensionPreferenceKey {}

#if DEBUG
struct CompactScoreSheetsPreviews: PreviewProvider {
	static var previews: some View {
		CompactScoreSheets(
			games: (0...2).map { index in
				.init(
					id: UUID(uuidString: "00000000-0000-0000-0000-00000000000\(index)")!,
					index: index,
					frames: Game.FRAME_INDICES.map {
						.init(
							index: $0,
							rolls: [
								.init(index: 0, displayValue: "10", didFoul: true, isSecondary: false),
								.init(index: 1, displayValue: "HP", didFoul: false, isSecondary: false),
								.init(index: 2, displayValue: "3", didFoul: false, isSecondary: false),
							],
							score: 32
						)
					}
				)
			},
			configuration: .init()
		)
		.padding()
	}
}
#endif
