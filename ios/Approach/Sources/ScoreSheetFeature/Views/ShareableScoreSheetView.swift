import AssetsLibrary
import DateTimeLibrary
import ModelsLibrary
import ScoringServiceInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

public struct ShareableScoreSheetView: View {
	let games: [SteppedGame]
	let config: ShareableScoreSheetConfiguration

	@State private var contentSize: CGSize = .zero
	@State private var headerWidth: CGFloat = .zero
	@State private var scoreWidth: CGFloat = .zero

	public init(games: [SteppedGame], configuration: ShareableScoreSheetConfiguration) {
		self.games = games
		self.config = configuration
	}

	public var body: some View {
		ScrollView(.horizontal, showsIndicators: false) {
			HStack(alignment: .bottom, spacing: 0) {
				if config.hasLabels(onSide: .left) {
					labels
						.padding(.trailing, .smallSpacing)
				}

				VStack(alignment: .leading, spacing: 0) {
					if config.hasLabels(onSide: .top) {
						labels
							.padding(.bottom, .smallSpacing)
					}

					ForEach(games) { game in
						if game.id == games.first?.id && config.showFrameLabels {
							if config.showFrameDetails {
								HStack(spacing: 0) {
									Spacer(minLength: headerWidth)

									ForEach(game.steps, id: \.index) { step in
										railView(label: String(step.index + 1))
											.borders(leading: step.index != 0, color: config.style.border)
											.background(config.style.railBackground)
											.roundCorners(topLeading: step.index == 0)
											.borders(trailing: Frame.isLast(step.index), color: config.style.border, thickness: 2)
									}

									railView(label: Strings.Sharing.ScoreSheet.score)
										.italic()
										.background(config.style.railBackground)
										.roundCorners(topTrailing: true)
										.frame(width: scoreWidth > 0 ? scoreWidth : nil)
								}
							} else {
								HStack(spacing: 0) {
									Spacer()
										.frame(minWidth: headerWidth, maxWidth: headerWidth)

									railView(label: Strings.Sharing.ScoreSheet.score)
										.background(config.style.railBackground)
										.roundCorners(topLeading: true, topTrailing: true)
										.frame(width: scoreWidth > 0 ? scoreWidth : nil)

									Spacer()
								}
							}
						}

						gameView(forGame: game)
							.background(config.style.background)
							.roundCorners(
								topLeading: game.id == games.first?.id,
								topTrailing: game.id == games.first?.id && !config.showFrameLabels,
								bottomLeading: game.id == games.last?.id,
								bottomTrailing: game.id == games.last?.id
							)
							.borders(
								bottom: game.id != games.last?.id,
								color: config.style.strongBorder,
								thickness: 2
							)
					}

					if config.hasLabels(onSide: .bottom) {
						labels
							.padding(.top, .smallSpacing)
					}
				}

				if config.hasLabels(onSide: .right) {
					labels
						.padding(.leading, .smallSpacing)
				}
			}
		}
		.measure(key: ContentSizeKey.self, to: $contentSize)
	}

	@ViewBuilder private var labels: some View {
		if config.showFrameDetails {
			switch config.labelPosition {
			case .top, .bottom: horizontalLabels
			case .left, .right: verticalLabels
			}
		} else {
			verticalLabels
		}
	}

	private var verticalLabels: some View {
		VStack(alignment: config.labelPosition == .left ? .trailing : .leading, spacing: .standardSpacing) {
			headerLabels
			detailsLabels
		}
	}

	private var horizontalLabels: some View {
		HStack(alignment: config.labelPosition == .top ? .bottom : .top, spacing: .standardSpacing) {
			headerLabels
			detailsLabels
		}
	}

	private var headerLabels: some View {
		VStack(alignment: config.labelPosition == .left ? .trailing : .leading, spacing: 0) {
			if let titleLabel = config.titleLabel {
				Text(titleLabel.header)
					.foregroundColor(.black)
					.font(.title2)
					.bold()

				if let subHeader = titleLabel.subHeader {
					Text(subHeader)
						.foregroundColor(.black)
						.italic()
				}
			}
		}
	}

	private var detailsLabels: some View {
		VStack(alignment: config.labelPosition == .left ? .trailing : .leading, spacing: 0) {
			if let seriesDate = config.seriesDate {
				HStack {
					if config.labelPosition != .left {
						Image(systemSymbol: .calendar)
					}

					Text(seriesDate.longFormat)

					if config.labelPosition == .left {
						Image(systemSymbol: .calendar)
					}
				}
				.foregroundColor(.black)
				.font(.caption)
				.padding(.bottom, .unitSpacing)
			}

			if let alleyName = config.alleyName {
				HStack {
					if config.labelPosition != .left {
						Image(systemSymbol: .buildingColumns)
					}

					Text(alleyName)

					if config.labelPosition == .left {
						Image(systemSymbol: .buildingColumns)
					}
				}
				.foregroundColor(.black)
				.font(.caption)
			}
		}
	}

	private func gameView(forGame game: SteppedGame) -> some View {
		HStack(spacing: 0) {
			Text(Strings.Game.titleWithOrdinal(game.index + 1))
				.foregroundColor(config.style.textOnBackground)
				.padding()
				.matchWidth(byKey: GameHeaderWidthKey.self, to: $headerWidth)
				.frame(width: headerWidth > 0 ? headerWidth : nil)
				.borders(trailing: !config.showFrameDetails, color: config.style.border, thickness: 2)

			if config.showFrameDetails {
				Grid(horizontalSpacing: 0, verticalSpacing: 0) {
					GridRow {
						ForEach(game.steps, id: \.index) { step in
							rollViews(
								forRolls: step.rolls,
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

			Text(String(game.score))
				.foregroundColor(config.style.textOnBackground)
				.bold()
				.padding()
				.matchWidth(byKey: ScoreWidthKey.self, to: $scoreWidth)
				.frame(width: scoreWidth > 0 ? scoreWidth : nil)
		}
	}

	private func rollViews(
		forRolls: [ScoreStep.RollStep],
		isLastFrame: Bool
	) -> some View {
		ForEach(forRolls, id: \.index) { roll in
			Text(roll.display ?? " ")
				.font(.caption)
				.minimumScaleFactor(0.2)
				.lineLimit(1)
				.frame(width: contentSize.width / 12)
				.padding(.smallSpacing)
				.foregroundColor(config.style.textOnBackground)
				.borders(
					trailing: !Frame.Roll.isLast(roll.index),
					bottom: true,
					leading: roll.index == 0,
					color: config.style.border
				)
				.borders(trailing: isLastFrame, color: config.style.border, thickness: 2)
		}
	}

	private func frameView(forFrame: ScoreStep) -> some View {
		Text(forFrame.display ?? " ")
			.frame(maxWidth: .infinity)
			.padding(.smallSpacing)
			.foregroundColor(config.style.textOnBackground)
			.borders(leading: true, color: config.style.border)
			.borders(trailing: Frame.isLast(forFrame.index), color: config.style.border, thickness: 2)
	}

	private func railView(label: String) -> some View {
		Text(label)
			.font(.caption2)
			.frame(maxWidth: .infinity)
			.padding(.unitSpacing)
			.foregroundColor(config.style.textOnRail)
	}
}

extension ShareableScoreSheetView {
	public struct SteppedGame: Identifiable, Equatable {
		public let id: Game.ID
		public let index: Int
		public let score: Int
		public let steps: [ScoreStep]

		public init(id: Game.ID, index: Int, score: Int, steps: [ScoreStep]) {
			self.id = id
			self.index = index
			self.score = score
			self.steps = steps
		}
	}
}
 
private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct GameHeaderWidthKey: PreferenceKey, MatchWidthPreferenceKey {}
private struct ScoreWidthKey: PreferenceKey, MatchWidthPreferenceKey {}

#if DEBUG
struct ShareableScoreSheetViewPreviews: PreviewProvider {
	static var previews: some View {
		ShareableScoreSheetView(
			games: (0...3).map { index in
				.init(
					id: UUID(index),
					index: index,
					score: 32,
					steps: Game.FRAME_INDICES.map {
						.init(
							index: $0,
							rolls: [
								.init(index: 0, display: "10", didFoul: true),
								.init(index: 1, display: "HP", didFoul: false),
								.init(index: 2, display: "3", didFoul: false),
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
