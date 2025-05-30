import AssetsLibrary
import Charts
import DateTimeLibrary
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI

public struct ShareableSeriesImage: View {
	@Environment(\.colorScheme) var colorScheme

	public let configuration: Configuration

	public init(configuration: Configuration) {
		self.configuration = configuration
	}

	var hasAnyPrimaryLabel: Bool {
		configuration.date != nil || configuration.bowlerName != nil || configuration.leagueName != nil
	}

	var hasAnySecondaryLabel: Bool {
		configuration.showDetails
	}

	public var body: some View {
		HStack(alignment: .top) {
			VStack(alignment: .leading, spacing: .smallSpacing) {
				if let date = configuration.date {
					ChartLabel(systemImage: "calendar", title: date.longFormat, style: .title)
				}

				if let bowlerName = configuration.bowlerName {
					ChartLabel(systemImage: "person", title: bowlerName, style: .plain)
				}

				if let leagueName = configuration.leagueName {
					ChartLabel(systemImage: "repeat", title: leagueName, style: .plain)
				}

				if hasAnyPrimaryLabel {
					tagline
						.padding(.top, .smallSpacing * -0.5)
						.padding(.leading, .smallSpacing)

					Spacer(minLength: 30)
						.fixedSize()
				}
			}

			Spacer()
		}
		.padding(.horizontal, .standardSpacing)
		.padding(.top, .standardSpacing)
		.padding(.bottom, .extraLargeSpacing)
		.frame(minHeight: 200, alignment: .top)
		.overlay(seriesDetails, alignment: .bottomLeading)
		.background(chart)
		.background(colorScheme == .dark ? .black : .white)
		.environment(\.sizeCategory, .extraLarge)
	}

	private var seriesDetails: some View {
		VStack(alignment: .leading, spacing: .smallSpacing) {
			if !hasAnyPrimaryLabel {
				tagline
			}

			HStack(spacing: .smallSpacing) {
				if configuration.showDetails {
					if let total = configuration.total {
						ChartLabel(
							systemImage: "checkmark.seal.fill",
							title: Strings.Sharing.Series.Details.totalLabel(total),
							style: .small
						)
					}

					ChartLabel(
						systemImage: "arrow.up",
						title: Strings.Sharing.Series.Details.highScoreLabel(configuration.scores.highestScore),
						style: .small
					)
					ChartLabel(
						systemImage: "arrow.down",
						title: Strings.Sharing.Series.Details.lowScoreLabel(configuration.scores.lowestScore),
						style: .small
					)
				}
			}
		}
		.padding(.standardSpacing)
	}

	private var tagline: some View {
		Text(Strings.Sharing.Common.Watermark.madeWithApproach)
			.font(.caption2)
			.monospaced()
	}

	private var chart: some View {
		Series.ScoreChart(
			id: configuration.id,
			scores: configuration.scores,
			style: .init(
				areaMarkForeground: .linearGradient(
					stops: [
						.init(color: Asset.Colors.Charts.Series.areaMark.swiftUIColor, location: 0.0),
						.init(color: Asset.Colors.Charts.Series.areaMark.swiftUIColor, location: 1.0),
					],
					startPoint: .leading,
					endPoint: .trailing
				),
				lineMarkForeground: .linearGradient(
					stops: [
						.init(color: Asset.Colors.Charts.Series.lineMark.swiftUIColor, location: 0.0),
						.init(color: Asset.Colors.Charts.Series.lineMark.swiftUIColor, location: 1.0),
					],
					startPoint: .leading,
					endPoint: .trailing
				),
				annotationForeground: Asset.Colors.Charts.Series.annotation.swiftUIColor,
				lineWidth: 4,
				annotateMaxScore: configuration.labelHighestScore,
				annotateMinScore: configuration.labelLowestScore,
				scoreDomain: configuration.scoreDomain
			)
		)
		.padding(.top, .extraLargeSpacing)
		.clipped()
	}
}

private struct ChartLabel: View {
	@Environment(\.colorScheme) private var colorScheme

	let systemImage: String
	let title: String
	let style: Style

	init(systemImage: String, title: String, style: Style) {
		self.systemImage = systemImage
		self.title = title
		self.style = style
	}

	var body: some View {
		HStack(spacing: style.spacing) {
			Image(systemName: systemImage)
				.resizable()
				.frame(width: style.iconSize, height: style.iconSize)

			Text(title)
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

extension ShareableSeriesImage {
	public struct Configuration: Equatable, Sendable {
		public let id: Series.ID
		public let date: Date?
		public let total: Int?
		public let showDetails: Bool
		public let scores: [Game.Score]
		public let bowlerName: String?
		public let leagueName: String?
		public let labelHighestScore: Bool
		public let labelLowestScore: Bool
		public let scoreDomain: ClosedRange<Int>
		public let displayScale: CGFloat
		public let colorScheme: ColorScheme

		public init(
			id: Series.ID,
			date: Date? = nil,
			total: Int? = nil,
			showDetails: Bool = true,
			scores: [Game.Score] = [],
			bowlerName: String? = nil,
			leagueName: String? = nil,
			labelHighestScore: Bool = false,
			labelLowestScore: Bool = false,
			scoreDomain: ClosedRange<Int> = 0...Game.MAXIMUM_SCORE,
			displayScale: CGFloat = .zero,
			colorScheme: ColorScheme
		) {
			self.id = id
			self.date = date
			self.total = total
			self.showDetails = showDetails
			self.scores = scores
			self.bowlerName = bowlerName
			self.leagueName = leagueName
			self.labelLowestScore = labelLowestScore
			self.labelHighestScore = labelHighestScore
			self.scoreDomain = scoreDomain
			self.displayScale = displayScale
			self.colorScheme = colorScheme
		}
	}
}

#Preview {
	ShareableSeriesImage(configuration: .init(
		id: UUID(),
//		date: Date(),
		total: 485,
		scores: [
			.init(index: 0, score: 225),
			.init(index: 1, score: 225),
			.init(index: 2, score: 100),
			.init(index: 3, score: 450),
		],
//		bowlerName: "Joseph",
//		leagueName: "Majors",
		labelHighestScore: true,
		labelLowestScore: true,
		scoreDomain: 0...450,
		colorScheme: .light
	))
}
