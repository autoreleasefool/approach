import ModelsLibrary
import ScoreSheetLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

public struct VerticalShareableGameImage: View {
	@Environment(\.colorScheme) var colorScheme

	public let configuration: Configuration
	@State private var contentSize: CGSize = .zero
	@State private var frameRowSize: CGSize = .zero
	@State private var lastFrameSize: CGSize = .zero
	@State private var lastRailSize: CGSize = .zero
	@State private var lastRollSize: CGSize = .zero

	public init(configuration: Configuration) {
		self.configuration = configuration
	}

	public var body: some View {
		let availableWidth = contentSize.width
		let scoreSheetConfiguration = configuration.style.verticalScoreSheetConfiguration()

		VStack(alignment: .leading, spacing: 0) {
			if let title = configuration.title {
				Text(title)
					.font(.title)
					.bold()
					.padding(.top)
					.padding(.bottom, configuration.subtitles == nil ? .standardSpacing : 0)
					.foregroundColor(configuration.labelForeground)
			}

			if let subtitles = configuration.subtitles {
				HStack {
					ForEach(subtitles) { subtitle in
						ScoreSheetLabel(item: subtitle, style: .plain)
							.foregroundColor(configuration.labelForeground)
					}
				}
				.padding(.vertical, .standardSpacing)
			}

			Grid(alignment: .topTrailing, horizontalSpacing: 0, verticalSpacing: 0) {
				rowViews(
					forFrames: Array(configuration.score.frames[0...2]),
					rollWidth: availableWidth / 3
				)

				Divider()
					.frame(height: 3)
					.background(scoreSheetConfiguration.border)

				rowViews(
					forFrames: Array(configuration.score.frames[3...5]),
					rollWidth: availableWidth / 3
				)

				Divider()
					.frame(height: 3)
					.background(scoreSheetConfiguration.border)

				rowViews(
					forFrames: Array(configuration.score.frames[6...8]),
					rollWidth: availableWidth / 3
				)

				Divider()
					.frame(height: 3)
					.background(scoreSheetConfiguration.border)
					.measure(key: FrameRowSizeKey.self, to: $frameRowSize)

				GridRow {
					RailView(frame: configuration.score.frames[9])
						.gridCellColumns(Frame.NUMBER_OF_ROLLS)
						.measure(key: LastRailSizeKey.self, to: $lastRailSize)
				}

				GridRow {
					rollViews(forFrame: configuration.score.frames[9], width: availableWidth / 3)
						.measure(key: LastRollSizeKey.self, to: $lastRollSize)
				}

				GridRow {
					FrameView(frame: configuration.score.frames[9])
						.gridCellColumns(Frame.NUMBER_OF_ROLLS)
						.measure(key: LastFrameSizeKey.self, to: $lastFrameSize)
				}
			}
			.background(scoreSheetConfiguration.background)
			.roundCorners(topLeading: true, topTrailing: true, bottomLeading: true, bottomTrailing: true)
			.fixedSize(horizontal: true, vertical: false)
			.overlay(alignment: .bottomTrailing) {
				FinalScoreView(score: configuration.score.score ?? 0, width: frameRowSize.width - lastFrameSize.width)
					.frame(
						height: lastFrameSize.height + lastRailSize.height + lastRollSize.height,
						alignment: .bottomTrailing
					)
					.roundCorners(bottomTrailing: true)
			}

			tagline
		}
		.measure(key: ContentSizeKey.self, to: $contentSize)
		.padding(.standardSpacing)
		.background(configuration.imageBackground)
		.environment(\.scoreSheetConfiguration, scoreSheetConfiguration)
		.environment(\.sizeCategory, .accessibilityLarge)
	}

	private func rollViews(forFrame frame: ScoredFrame, width: CGFloat) -> some View {
		ForEach(frame.rolls, id: \.index) { roll in
			RollView(frame: frame, roll: roll, width: width / 3)
		}
	}

	@ViewBuilder
	private func rowViews(
		forFrames frames: [ScoredFrame],
		rollWidth: CGFloat
	) -> some View {
		GridRow {
			ForEach(frames) { frame in
				RailView(frame: frame)
					.gridCellColumns(Frame.NUMBER_OF_ROLLS)
			}
		}

		GridRow {
			ForEach(frames) { frame in
				rollViews(forFrame: frame, width: rollWidth)
			}
		}

		GridRow {
			ForEach(frames) { frame in
				FrameView(frame: frame)
					.gridCellColumns(Frame.NUMBER_OF_ROLLS)
			}
		}
	}

	private var tagline: some View {
		Text(Strings.Sharing.Common.Watermark.madeWithApproach)
			.font(.caption)
			.monospaced()
			.foregroundColor(configuration.labelForeground)
			.padding(.smallSpacing)
			.frame(maxWidth: .infinity, alignment: .trailing)
	}
}

// MARK: Sizing

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct LastFrameSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct LastRailSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct LastRollSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct FrameRowSizeKey: PreferenceKey, CGSizePreferenceKey {}

// MARK: Configuration

extension VerticalShareableGameImage {
	public struct Configuration: Equatable, Sendable {
		public let score: ScoredGame
		public let style: ShareableGamesImage.Style
		public let bowlerName: String?
		public let leagueName: String?
		public let date: Date?
		public let displayScale: CGFloat
		public let colorScheme: ColorScheme
		fileprivate let title: String?
		fileprivate let subtitles: [ScoreSheetLabel.Item]?

		fileprivate var imageBackground: Color {
			colorScheme == .dark ? .gray : .white
		}

		fileprivate var labelForeground: Color {
			colorScheme == .dark ? .white : .black
		}

		public init(
			score: ScoredGame,
			style: ShareableGamesImage.Style,
			bowlerName: String?,
			leagueName: String?,
			date: Date?,
			displayScale: CGFloat,
			colorScheme: ColorScheme
		) {
			self.score = score
			self.style = style
			self.bowlerName = bowlerName
			self.leagueName = leagueName
			self.date = date
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

			let subtitles: [ScoreSheetLabel.Item] = if date != nil {
				[
					bowlerName.flatMap { ScoreSheetLabel.Item(symbol: .person, title: $0) },
					leagueName.flatMap { ScoreSheetLabel.Item(symbol: .repeat, title: $0) },
				].compactMap { $0 }
			} else if leagueName != nil {
				[
					bowlerName.flatMap { ScoreSheetLabel.Item(symbol: .person, title: $0) }
				].compactMap { $0 }
			} else {
				[]
			}

			self.subtitles = subtitles.isEmpty ? nil : subtitles
		}
	}
}

// MARK: Style

extension ShareableGamesImage.Style {
	func verticalScoreSheetConfiguration() -> ScoreSheet.Configuration {
		switch self {
		case .plain:
			.shareablePlain(
				allowTopRounding: false,
				allowBottomRounding: false,
				allowLeadingRounding: false,
				allowTrailingRounding: false
			)
		case .grayscale:
			.shareableGrayscale(
				allowTopRounding: false,
				allowBottomRounding: false,
				allowLeadingRounding: false,
				allowTrailingRounding: false
			)
		}
	}
}

// MARK: - Extensions

extension ScoredFrame: Identifiable {
	public var id: Int { index }
}

// MARK: - Preview

#Preview {
	VerticalShareableGameImage(configuration: VerticalShareableGameImage.Configuration(
		score: ScoredGame(
			id: UUID(uuidString: "00000000-0000-0000-0000-000000000000")!,
			index: 0,
			frames: Game.FRAME_INDICES.map {
				.init(
					index: $0,
					rolls: [
						.init(index: 0, displayValue: "HP", didFoul: true, isSecondary: false),
						.init(index: 1, displayValue: "10", didFoul: false, isSecondary: false),
						.init(index: 2, displayValue: "—", didFoul: false, isSecondary: false),
					],
					score: 255
				)
			}
		),
		style: .plain,
		bowlerName: "Joseph",
		leagueName: "Majors, 2024",
		date: Date(),
		displayScale: 1.0,
		colorScheme: .dark,
	))
	.frame(minWidth: 900)
}
