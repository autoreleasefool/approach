import AssetsLibrary
import ModelsLibrary
import ScoringServiceInterface
import SwiftUI
import SwiftUIExtensionsLibrary

public struct ShareableScoreSheetView: View {
	let games: [SteppedGame]
	let style: Style

	@State private var contentSize: CGSize = .zero
	@State private var headerWidth: CGFloat = .zero

	public init(games: [SteppedGame], style: Style) {
		self.games = games
		self.style = style
	}

	public var body: some View {
		ScrollView(.horizontal, showsIndicators: false) {
			VStack(spacing: 0) {
				ForEach(games) { game in
					if game.id == games.first?.id {
						HStack(spacing: 0) {
							Spacer(minLength: headerWidth)
							ForEach(game.steps, id: \.index) { step in
								railView(forFrame: step)
									.background(style.rail)
									.roundCorners(
										topLeading: step.index == 0,
										topTrailing: Frame.isLast(step.index)
									)
							}
						}
					}

					gameView(forGame: game)
						.background(style.background)
						.roundCorners(
							topLeading: game.id == games.first?.id,
							topTrailing: game.id == games.first?.id,
							bottomLeading: game.id == games.last?.id
						)
						.borders(
							bottom: game.id != games.last?.id,
							color: Asset.Colors.ScoreSheet.Border.defaultBold,
							thickness: 2
						)

					if game.id == games.last?.id && games.count > 4 {
						HStack(spacing: 0) {
							Spacer(minLength: headerWidth)
							ForEach(game.steps, id: \.index) { step in
								railView(forFrame: step)
									.background(style.rail)
									.roundCorners(
										bottomLeading: step.index == 0,
										bottomTrailing: Frame.isLast(step.index)
									)
							}
						}
					}
				}
			}
		}
		.measure(key: ContentSizeKey.self, to: $contentSize)
	}

	private func gameView(forGame game: SteppedGame) -> some View {
		HStack(spacing: 0) {
			Text("Game \(game.index)")
				.foregroundColor(style.text)
				.padding()
				.matchWidth(byKey: GameHeaderWidthKey.self, to: $headerWidth)
				.frame(width: headerWidth > 0 ? headerWidth : nil)

			Grid(horizontalSpacing: 0, verticalSpacing: 0) {
				GridRow {
					ForEach(game.steps, id: \.index) { step in
						rollViews(
							forRolls: step.rolls,
							isFirstGame: game.id == games.first?.id,
							isFirstFrame: step.index == 0,
							isLastFrame: Frame.isLast(step.index)
						)
					}
				}

				GridRow {
					ForEach(game.steps, id: \.index) { step in
						frameView(forFrame: step)
							.gridCellColumns(Frame.NUMBER_OF_ROLLS)
					}
				}
			}
		}
	}

	private func rollViews(
		forRolls: [ScoreStep.RollStep],
		isFirstGame: Bool,
		isFirstFrame: Bool,
		isLastFrame: Bool
	) -> some View {
		ForEach(forRolls, id: \.index) { roll in
			Text(roll.display ?? " ")
				.font(.caption)
				.minimumScaleFactor(0.2)
				.lineLimit(1)
				.frame(width: contentSize.width / 12)
				.padding(.smallSpacing)
				.foregroundColor(style.text)
				.borders(
					trailing: !Frame.Roll.isLast(roll.index),
					bottom: true,
					leading: roll.index == 0 && !isFirstFrame,
					color: style.border
				)
		}
	}

	private func frameView(forFrame: ScoreStep) -> some View {
		Text(forFrame.display ?? " ")
			.frame(maxWidth: .infinity)
			.padding(.smallSpacing)
			.foregroundColor(style.text)
			.borders(leading: forFrame.index != 0, color: style.border)
	}

	private func railView(forFrame: ScoreStep) -> some View {
		Text(String(forFrame.index + 1))
			.font(.caption2)
			.frame(maxWidth: .infinity)
			.padding(.unitSpacing)
			.foregroundColor(style.label)
			.borders(leading: forFrame.index != 0, color: style.border)
	}
}

extension ShareableScoreSheetView {
	public struct Style {
		let text: ColorAsset
		let label: ColorAsset
		let background: ColorAsset
		let rail: ColorAsset
		let border: ColorAsset

		public static let `default` = Self(
			text: Asset.Colors.ScoreSheet.Text.default,
			label: Asset.Colors.ScoreSheet.Label.default,
			background: Asset.Colors.ScoreSheet.Background.default,
			rail: Asset.Colors.ScoreSheet.Rail.default,
			border: Asset.Colors.ScoreSheet.Border.default
		)

		public static let plain = Self(
			text: Asset.Colors.ScoreSheet.Text.plain,
			label: Asset.Colors.ScoreSheet.Label.plain,
			background: Asset.Colors.ScoreSheet.Background.plain,
			rail: Asset.Colors.ScoreSheet.Rail.plain,
			border: Asset.Colors.ScoreSheet.Border.plain
		)
	}
}

extension ShareableScoreSheetView {
	public struct SteppedGame: Identifiable {
		public let id: Game.ID
		public let index: Int
		public let steps: [ScoreStep]

		public init(id: Game.ID, index: Int, steps: [ScoreStep]) {
			self.id = id
			self.index = index
			self.steps = steps
		}
	}
}

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct GameHeaderWidthKey: PreferenceKey, MatchWidthPreferenceKey {}

#if DEBUG
struct ShareableScoreSheetViewPreviews: PreviewProvider {
	static var previews: some View {
		ShareableScoreSheetView(
			games: (0...3).map { index in
				.init(id: UUID(index), index: index, steps: Game.FRAME_INDICES.map {
					.init(
						index: $0,
						rolls: [
							.init(index: 0, display: "10", didFoul: true),
							.init(index: 1, display: "HP", didFoul: false),
							.init(index: 2, display: "3", didFoul: false),
						],
						score: 32
					)
				})
			},
			style: .default
		)
		.padding()
	}
}
#endif
