import AssetsLibrary
import DateTimeLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ScoreSheetLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

public struct HorizontalShareableGamesImage: View {
	@Environment(\.colorScheme) var colorScheme

	public let configuration: Configuration
	@State private var gridItemHeight: CGFloat = .zero

	public init(configuration: Configuration) {
		self.configuration = configuration
	}

	public var body: some View {
		let baseScoreSheetConfiguration = configuration.style.horizontalScoreSheetConfiguration(
			isFirstGame: false,
			isLastGame: false,
			allowLeadingRounding: false,
			allowTrailingRounding: true
		)

		VStack(alignment: .leading, spacing: 0) {
			if let title = configuration.title {
				Text(title)
					.font(.title)
					.bold()
					.padding(.horizontal)
					.padding(.top)
					.padding(.bottom, configuration.subtitles == nil ? .standardSpacing : 0)
					.foregroundStyle(baseScoreSheetConfiguration.foreground)
			}

			if let subtitles = configuration.subtitles {
				HStack {
					ForEach(subtitles) { subtitle in
						ScoreSheetLabel(item: subtitle, style: .plain)
							.foregroundStyle(baseScoreSheetConfiguration.foreground)
					}
				}
				.padding()
			}

			Grid(alignment: .topTrailing, horizontalSpacing: 0, verticalSpacing: 0) {
				ForEach(configuration.scores) { game in
					let scoreSheetConfiguration = configuration.style.horizontalScoreSheetConfiguration(
						isFirstGame: game.id == configuration.scores.first?.id,
						isLastGame: game.id == configuration.scores.last?.id,
						allowLeadingRounding: !configuration.isShowingGameTitles,
						allowTrailingRounding: true
					)

					if configuration.hasLeadingText && game == configuration.scores.first {
						GridRow {
							Rectangle()
								.fill(scoreSheetConfiguration.border.swiftUIColor)
								.frame(height: 3)
								.gridCellColumns(2)
						}
					}

					GridRow {
						Text(Strings.Game.titleWithOrdinal(game.index + 1))
							.font(.headline)
							.padding(.horizontal)
							.foregroundStyle(scoreSheetConfiguration.foreground)
							.matchHeight(byKey: GridItemHeightKey.self, to: $gridItemHeight)

						ScoreSheet(
							game: game,
							configuration: scoreSheetConfiguration,
							contentSize: CGSize(width: 360, height: 100),
							selection: .constant(.unselected)
						)
						.matchHeight(byKey: GridItemHeightKey.self, to: $gridItemHeight)
						.border(
							edges: [.leading],
							width: 3,
							color: scoreSheetConfiguration.border
						)
						.environment(\.scoreSheetConfiguration, scoreSheetConfiguration)
					}

					GridRow {
						Rectangle()
							.fill(scoreSheetConfiguration.border.swiftUIColor)
							.frame(height: 3)
							.gridCellColumns(2)
					}
				}
			}

			tagline(baseScoreSheetConfiguration)
		}
		.background(baseScoreSheetConfiguration.background)
		.environment(\.sizeCategory, .extraLarge)
	}

	private func tagline(_ configuration: ScoreSheet.Configuration) -> some View {
		Text(Strings.Sharing.Common.Watermark.madeWithApproach)
			.font(.caption)
			.monospaced()
			.padding(.smallSpacing)
			.foregroundStyle(configuration.foreground)
			.frame(maxWidth: .infinity, alignment: .trailing)
	}
}

extension HorizontalShareableGamesImage {
	public struct Configuration: Equatable, Sendable {
		public let scores: [ScoredGame]
		public let style: ShareableGamesImage.Style
		public let bowlerName: String?
		public let leagueName: String?
		public let date: Date?
		public let total: Int?
		public let isShowingSeriesDetails: Bool
		public let isShowingGameTitles: Bool
		public let displayScale: CGFloat
		public let colorScheme: ColorScheme
		fileprivate let title: String?
		fileprivate let subtitles: [ScoreSheetLabel.Item]?

		public init(
			scores: [ScoredGame],
			isShowingGameTitles: Bool,
			isShowingSeriesDetails: Bool,
			bowlerName: String?,
			leagueName: String?,
			date: Date?,
			total: Int?,
			style: ShareableGamesImage.Style,
			displayScale: CGFloat,
			colorScheme: ColorScheme
		) {
			self.scores = scores
			self.style = style
			self.isShowingSeriesDetails = isShowingSeriesDetails
			self.bowlerName = bowlerName
			self.leagueName = leagueName
			self.date = date
			self.total = total
			self.isShowingGameTitles = isShowingGameTitles
			self.displayScale = displayScale
			self.colorScheme = colorScheme

			self.title = if let date {
				date.longFormat
			} else if let leagueName {
				leagueName
			} else if let bowlerName {
				bowlerName
			} else {
				nil
			}

			var subtitles: [ScoreSheetLabel.Item] = if date != nil {
				[
					bowlerName.flatMap { ScoreSheetLabel.Item(systemImage: "person", title: $0) },
					leagueName.flatMap { ScoreSheetLabel.Item(systemImage: "repeat", title: $0) },
				].compactMap { $0 }
			} else if leagueName != nil {
				[
					bowlerName.flatMap { ScoreSheetLabel.Item(systemImage: "person", title: $0) },
				].compactMap { $0 }
			} else {
				[]
			}

			if isShowingSeriesDetails {
				if let total {
					subtitles.append(ScoreSheetLabel.Item(
						systemImage: "checkmark.seal.fill",
						title: Strings.Sharing.Game.Details.totalLabel(total)
					))
				}

				subtitles.append(ScoreSheetLabel.Item(
					systemImage: "arrow.up",
					title: Strings.Sharing.Game.Details.highScoreLabel(scores.highestScore)
				))

				subtitles.append(ScoreSheetLabel.Item(
					systemImage: "arrow.down",
					title: Strings.Sharing.Game.Details.lowScoreLabel(scores.lowestScore)
				))
			}

			self.subtitles = subtitles.isEmpty ? nil : subtitles
		}

		var hasLeadingText: Bool {
			title != nil
		}
	}
}

extension ShareableGamesImage.Style {
	func horizontalScoreSheetConfiguration(
		isFirstGame: Bool,
		isLastGame: Bool,
		allowLeadingRounding: Bool,
		allowTrailingRounding: Bool
	) -> ScoreSheet.Configuration {
		switch self {
		case .grayscale:
			.shareableGrayscale(
				allowTopRounding: isFirstGame,
				allowBottomRounding: isLastGame,
				allowLeadingRounding: allowLeadingRounding,
				allowTrailingRounding: allowTrailingRounding
			)
		case .plain:
			.shareablePlain(
				allowTopRounding: isFirstGame,
				allowBottomRounding: isLastGame,
				allowLeadingRounding: allowLeadingRounding,
				allowTrailingRounding: allowTrailingRounding
			)
		}
	}
}

private struct GridItemHeightKey: PreferenceKey, MatchDimensionPreferenceKey {}

#Preview(traits: .sizeThatFitsLayout) {
	HorizontalShareableGamesImage(configuration: HorizontalShareableGamesImage.Configuration(
		scores: (0...4).map {
			ScoredGame(
				id: UUID(uuidString: "00000000-0000-0000-0000-00000000000\($0)")!,
				index: $0,
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
			)
		},
		isShowingGameTitles: true,
		isShowingSeriesDetails: true,
		bowlerName: "Joseph",
		leagueName: "Majors, 2023-24",
		date: Date(),
		total: 1_410,
		style: .grayscale,
		displayScale: .zero,
		colorScheme: .light
	))
}
