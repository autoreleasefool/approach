import AssetsLibrary
import DateTimeLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ScoreSheetLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

public struct ShareableGamesImage: View {
	@Environment(\.colorScheme) var colorScheme

	public let configuration: Configuration
	@State private var gridItemHeight: CGFloat = .zero

	public init(configuration: Configuration) {
		self.configuration = configuration
	}

	public var body: some View {
		let baseScoreSheetConfiguration = configuration.style.scoreSheetConfiguration(
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
					.foregroundColor(baseScoreSheetConfiguration.foreground)
			}

			if let subtitles = configuration.subtitles {
				HStack {
					ForEach(subtitles) { subtitle in
						ScoreSheetLabel(item: subtitle, style: .plain)
							.foregroundColor(baseScoreSheetConfiguration.foreground)
					}
				}
				.padding(.horizontal)
				.padding(.vertical)
			}

			Grid(alignment: .topTrailing, horizontalSpacing: 0, verticalSpacing: 0) {
				ForEach(configuration.scores) { game in
					let scoreSheetConfiguration = configuration.style.scoreSheetConfiguration(
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
					}

					GridRow {
						Rectangle()
							.fill(scoreSheetConfiguration.border.swiftUIColor)
							.frame(height: 3)
							.gridCellColumns(2)
					}
				}
			}

			tagline
		}
		.background(baseScoreSheetConfiguration.background)
	}

	private var tagline: some View {
		Text(Strings.Sharing.Common.Watermark.madeWithApproach)
			.font(.system(size: 8))
			.monospaced()
			.padding(.smallSpacing)
			.frame(maxWidth: .infinity, alignment: .trailing)
	}
}

extension ShareableGamesImage {
	public struct Configuration: Equatable, Sendable {
		public let scores: [ScoredGame]
		public let style: Style
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
			style: Style,
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
					bowlerName.transform { ScoreSheetLabel.Item(symbol: .person, title: $0) },
					leagueName.transform { ScoreSheetLabel.Item(symbol: .repeat, title: $0) },
				].compactMap { $0 }
			} else if leagueName != nil {
				[
					bowlerName.transform { ScoreSheetLabel.Item(symbol: .person, title: $0) },
				].compactMap { $0 }
			} else {
				[]
			}

			if isShowingSeriesDetails {
				if let total {
					subtitles.append(ScoreSheetLabel.Item(
						symbol: .checkmarkSealFill,
						title: Strings.Sharing.Game.Details.totalLabel(total)
					))
				}

				subtitles.append(ScoreSheetLabel.Item(
					symbol: .arrowUp,
					title: Strings.Sharing.Game.Details.highScoreLabel(scores.highestScore)
				))

				subtitles.append(ScoreSheetLabel.Item(
					symbol: .arrowDown,
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

private struct ScoreSheetLabel: View {
	@Environment(\.colorScheme) private var colorScheme

	struct Item: Identifiable, Equatable {
		let symbol: SFSymbol
		let title: String

		var id: String { title }
	}

	let item: Item
	let style: Style

	init(item: Item, style: Style) {
		self.item = item
		self.style = style
	}

	var body: some View {
		HStack(spacing: style.spacing) {
			Image(systemSymbol: item.symbol)
				.resizable()
				.scaledToFit()
				.frame(width: style.iconSize, height: style.iconSize)

			Text(item.title)
				.font(style.font)
		}
		.padding(.horizontal, .smallSpacing)
		.padding(.vertical, style.padding)
		.background(
			colorScheme == .dark ? Color.gray.opacity(0.8) : Color.black.opacity(0.2),
			in: RoundedRectangle(cornerRadius: .standardRadius)
		)
	}

	struct Style {
		let font: Font
		let iconSize: CGFloat
		let spacing: CGFloat
		let padding: CGFloat

		static let title: Self = .init(
			font: .title3.weight(.bold),
			iconSize: .smallIcon,
			spacing: .standardSpacing,
			padding: .smallSpacing
		)

		static let plain: Self = .init(
			font: .body,
			iconSize: .smallIcon,
			spacing: .standardSpacing,
			padding: .unitSpacing
		)

		static let small: Self = .init(
			font: .caption2,
			iconSize: 8,
			spacing: .smallSpacing,
			padding: .unitSpacing
		)
	}
}

extension ShareableGamesImage.Configuration {
	public enum Style: CaseIterable, Identifiable, Sendable {
		case plain
		case grayscale

		public var id: Self { self }

		func scoreSheetConfiguration(
			isFirstGame: Bool,
			isLastGame: Bool,
			allowLeadingRounding: Bool,
			allowTrailingRounding: Bool
		) -> ScoreSheet.Configuration {
			switch self {
			case .grayscale:
				.shareableGrayscale(
					isFirstGame: isFirstGame,
					isLastGame: isLastGame,
					allowLeadingRounding: allowLeadingRounding,
					allowTrailingRounding: allowTrailingRounding
				)
			case .plain:
				.shareablePlain(
					isFirstGame: isFirstGame,
					isLastGame: isLastGame,
					allowLeadingRounding: allowLeadingRounding,
					allowTrailingRounding: allowTrailingRounding
				)
			}
		}
	}
}

extension Optional {
	func transform<T>(_ transform: (Wrapped) -> T) -> T? {
		if let self {
			transform(self)
		} else {
			nil
		}
	}
}

extension ScoreSheet.Configuration {
	static func shareablePlain(
		isFirstGame: Bool,
		isLastGame: Bool,
		allowLeadingRounding: Bool,
		allowTrailingRounding: Bool
	) -> ScoreSheet.Configuration {
		ScoreSheet.Configuration(
			foreground: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.default,
			foregroundHighlight: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.highlight,
			foregroundSecondary: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.secondary,
			foregroundFoul: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.foul,
			foregroundFoulHighlight: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.highlightFoul,
			background: Asset.Colors.ScoreSheet.Plain.Background.default,
			backgroundHighlight: Asset.Colors.ScoreSheet.Plain.Background.highlight,
			railForeground: Asset.Colors.ScoreSheet.Plain.Text.OnRail.default,
			railForegroundHighlight: Asset.Colors.ScoreSheet.Plain.Text.OnRail.highlight,
			railBackground: Asset.Colors.ScoreSheet.Plain.Rail.default,
			railBackgroundHighlight: Asset.Colors.ScoreSheet.Plain.Rail.highlight,
			border: Asset.Colors.ScoreSheet.Plain.Border.default,
			allowLeadingRounding: allowLeadingRounding,
			allowTopRounding: isFirstGame,
			allowTrailingRounding: allowTrailingRounding,
			allowBottomRounding: isLastGame,
			railOnTop: true
		)
	}

	static func shareableGrayscale(
		isFirstGame: Bool,
		isLastGame: Bool,
		allowLeadingRounding: Bool,
		allowTrailingRounding: Bool
	) -> ScoreSheet.Configuration {
		ScoreSheet.Configuration(
			foreground: Asset.Colors.ScoreSheet.Grayscale.Text.OnBackground.default,
			foregroundHighlight: Asset.Colors.ScoreSheet.Grayscale.Text.OnBackground.highlight,
			foregroundSecondary: Asset.Colors.ScoreSheet.Grayscale.Text.OnBackground.secondary,
			foregroundFoul: Asset.Colors.ScoreSheet.Grayscale.Text.OnBackground.foul,
			foregroundFoulHighlight: Asset.Colors.ScoreSheet.Grayscale.Text.OnBackground.highlightFoul,
			background: Asset.Colors.ScoreSheet.Grayscale.Background.default,
			backgroundHighlight: Asset.Colors.ScoreSheet.Grayscale.Background.highlight,
			railForeground: Asset.Colors.ScoreSheet.Grayscale.Text.OnRail.default,
			railForegroundHighlight: Asset.Colors.ScoreSheet.Grayscale.Text.OnRail.highlight,
			railBackground: Asset.Colors.ScoreSheet.Grayscale.Rail.default,
			railBackgroundHighlight: Asset.Colors.ScoreSheet.Grayscale.Rail.highlight,
			border: Asset.Colors.ScoreSheet.Grayscale.Border.default,
			allowLeadingRounding: allowLeadingRounding,
			allowTopRounding: isFirstGame,
			allowTrailingRounding: allowTrailingRounding,
			allowBottomRounding: isLastGame,
			railOnTop: true
		)
	}
}

private struct GridItemHeightKey: PreferenceKey, MatchDimensionPreferenceKey {}

#Preview(traits: .sizeThatFitsLayout) {
	ShareableGamesImage(configuration: ShareableGamesImage.Configuration(
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
