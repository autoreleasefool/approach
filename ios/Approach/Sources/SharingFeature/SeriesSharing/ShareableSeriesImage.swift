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
					ChartLabel(symbol: .calendar, title: date.longFormat, style: .title)
				}

				if let bowlerName = configuration.bowlerName {
					ChartLabel(symbol: .person, title: bowlerName, style: .plain)
				}

				if let leagueName = configuration.leagueName {
					ChartLabel(symbol: .repeat, title: leagueName, style: .plain)
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
	}

	private var seriesDetails: some View {
		VStack(alignment: .leading, spacing: .smallSpacing) {
			if !hasAnyPrimaryLabel {
				tagline
			}

			HStack(spacing: .smallSpacing) {
				if configuration.showDetails {
					if let total = configuration.total {
						ChartLabel(symbol: .checkmarkSealFill, title: "\(total) TOTAL", style: .small)
					}

					ChartLabel(symbol: .arrowUp, title: "\(configuration.scores.highestScore) HIGH", style: .small)
					ChartLabel(symbol: .arrowDown, title: "\(configuration.scores.lowestScore) LOW", style: .small)
				}
			}
		}
		.padding(.standardSpacing)
	}

	private var tagline: some View {
		Text("\(Strings.Sharing.Watermark.madeWithApproach)\n\(Strings.Sharing.Watermark.tryApproach)")
			.font(.system(size: 8))
			.monospaced()
	}

	private var chart: some View {
		Series.ScoreChart(
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

	let symbol: SFSymbol
	let title: String
	let style: Style

	init(symbol: SFSymbol, title: String, style: Style) {
		self.symbol = symbol
		self.title = title
		self.style = style
	}

	var body: some View {
		HStack(spacing: style.spacing) {
			Image(systemSymbol: symbol)
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

		static var title: Self = .init(
			font: .title3.weight(.bold),
			iconSize: .smallIcon,
			spacing: .standardSpacing,
			padding: .smallSpacing
		)

		static var plain: Self = .init(
			font: .body,
			iconSize: .smallIcon,
			spacing: .standardSpacing,
			padding: .unitSpacing
		)

		static var small: Self = .init(
			font: .caption2,
			iconSize: 8,
			spacing: .smallSpacing,
			padding: .unitSpacing
		)
	}
}

extension ShareableSeriesImage {
	public struct Configuration: Equatable {
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
