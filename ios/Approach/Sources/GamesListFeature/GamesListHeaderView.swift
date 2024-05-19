import AssetsLibrary
import Charts
import DateTimeLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

public struct GamesListHeaderView: View {
	@State private var contentSize: CGSize = .zero

	let scores: [Score]
	let total: Int

	init(scores: [Score]) {
		self.scores = scores
		self.total = scores.reduce(0) { $0 + $1.score }
	}

	public var body: some View {
		Section {
			if scores.count == 0 {
				Text(Strings.Game.List.Header.numberOfGames(scores.count))
					.font(.subheadline)
					.bold()
			} else if scores.count == 1 {
				Text(Strings.Game.List.Header.numberOfGames(scores.count))
					.font(.subheadline)
					.bold()
			} else if total == 0 {
				MockGamesListHeaderView()
					.listRowInsets(EdgeInsets())
					.alignmentGuide(.listRowSeparatorLeading) { d in
						d[.leading]
					}
			} else {
				ZStack(alignment: .bottomLeading) {
					GamesListHeaderChart(
						scores: scores,
						width: contentSize.width,
						height: contentSize.height * 0.7,
						isMocked: false
					)

					VStack {
						HStack(alignment: .firstTextBaseline) {
							VStack(alignment: .leading, spacing: 0) {
								Text(Strings.Game.List.Header.numberOfGames(scores.count))
									.font(.subheadline)
									.bold()

								if let scoreRange {
									Group {
										Text(Strings.Game.List.Header.highGame(scoreRange.highest))
											.padding(.top, .smallSpacing)
										Text(Strings.Game.List.Header.lowGame(scoreRange.lowest))
									}
									.italic()
								}
							}

							Spacer()

							if total > 0 {
								VStack(alignment: .trailing, spacing: .smallSpacing) {
									Text(Strings.Game.List.Header.seriesTotal)
										.font(.subheadline)
										.fontWeight(.thin)

									Text(String(total))
										.font(.title2)
										.fontWeight(.heavy)
										.italic()
								}
							}
						}

						Spacer()
					}
					.padding(.standardSpacing)
					.frame(height: scores.count > 1 ? 200 : nil)
					.measure(key: ContentSizeKey.self, to: $contentSize)
				}
				.listRowInsets(EdgeInsets())
				.alignmentGuide(.listRowSeparatorLeading) { d in
					d[.leading]
				}
			}
		}
	}
}

// MARK: Mock

private struct MockGamesListHeaderView: View {
	@State private var contentSize: CGSize = .zero

	private static let scores: [GamesListHeaderView.Score] = [
		.init(index: 0, score: 0),
		.init(index: 1, score: 200),
		.init(index: 2, score: 125),
		.init(index: 3, score: 300),
	]

	var body: some View {
		ZStack(alignment: .bottomLeading) {
			GamesListHeaderChart(
				scores: MockGamesListHeaderView.scores,
				width: contentSize.width,
				height: contentSize.height * 0.7,
				isMocked: true
			)

			VStack(alignment: .leading) {
				Text(Strings.Game.List.Header.seeYourScores)
					.font(.headline)

				Text(Strings.Game.List.Header.whenYouStartBowling)

				Spacer()
			}
			.padding()
			.frame(maxWidth: .infinity)
			.frame(height: 150)
			.measure(key: ContentSizeKey.self, to: $contentSize)
		}
	}
}

private struct GamesListHeaderChart: View {
	let scores: [GamesListHeaderView.Score]
	let width: CGFloat
	let height: CGFloat
	let isMocked: Bool

	var body: some View {
		Chart {
			ForEach(scores) { score in
				AreaMark(
					x: .value(Strings.Game.List.Header.Chart.xAxisLabel, score.index + 1),
					y: .value(Strings.Game.List.Header.Chart.yAxisLabel, score.score)
				)
				.foregroundStyle(
					.linearGradient(
						stops: isMocked ? Self.areaMarkMockedStops : Self.areaMarkStops,
						startPoint: .leading,
						endPoint: .trailing
					)
				)
				.interpolationMethod(.catmullRom)

				LineMark(
					x: .value(Strings.Game.List.Header.Chart.xAxisLabel, score.index + 1),
					y: .value(Strings.Game.List.Header.Chart.yAxisLabel, score.score)
				)
				.lineStyle(StrokeStyle(lineWidth: 2))
				.foregroundStyle(
					.linearGradient(
						stops: isMocked ? Self.linearMarkMockedStops : Self.linearMarkStops,
						startPoint: .leading,
						endPoint: .trailing
					)
				)
				.interpolationMethod(.catmullRom)
			}
		}
		.chartXAxis(.hidden)
		.chartYAxis(.hidden)
		.chartLegend(.hidden)
		.chartYScale(domain: 0...Game.MAXIMUM_SCORE)
		.chartXScale(domain: 1...scores.count)
		.frame(width: width, height: height)
	}

	private static let areaMarkStops: [Gradient.Stop] = [
		.init(color: Color.clear, location: 0),
		.init(color: Asset.Colors.Charts.Game.areaMark.swiftUIColor.opacity(0.4), location: 0.3),
		.init(color: Asset.Colors.Charts.Game.areaMark.swiftUIColor.opacity(0.5), location: 0.7),
		.init(color: Color.clear, location: 1),
	]

	private static let areaMarkMockedStops: [Gradient.Stop] = [
		.init(color: Color.clear, location: 0),
		.init(color: Asset.Colors.Charts.Mock.areaMark.swiftUIColor.opacity(0.4), location: 0.3),
		.init(color: Asset.Colors.Charts.Mock.areaMark.swiftUIColor.opacity(0.5), location: 0.7),
		.init(color: Color.clear, location: 1),
	]

	private static let linearMarkStops: [Gradient.Stop] = [
		.init(color: Color.clear, location: 0),
		.init(color: Asset.Colors.Charts.Game.lineMark.swiftUIColor.opacity(0.4), location: 0.3),
		.init(color: Asset.Colors.Charts.Game.lineMark.swiftUIColor.opacity(0.5), location: 0.7),
		.init(color: Color.clear, location: 1),
	]

	private static let linearMarkMockedStops: [Gradient.Stop] = [
		.init(color: Color.clear, location: 0),
		.init(color: Asset.Colors.Charts.Mock.lineMark.swiftUIColor.opacity(0.4), location: 0.3),
		.init(color: Asset.Colors.Charts.Mock.lineMark.swiftUIColor.opacity(0.5), location: 0.7),
		.init(color: Color.clear, location: 1),
	]
}

// MARK: Score

extension GamesListHeaderView {
	public struct Score: Identifiable, Equatable, Codable {
		public let index: Int
		public let score: Int

		public var id: Int { index }

		public init(index: Int, score: Int) {
			self.index = index
			self.score = score
		}
	}
}

// MARK: Score Range

extension GamesListHeaderView {
	var lowestScore: Int { scores.min { $0.score < $1.score }?.score ?? 0 }
	var highestScore: Int { scores.max { $0.score < $1.score }?.score ?? 0 }

	var scoreRange: (lowest: Int, highest: Int)? {
		let (lowest, highest) = (self.lowestScore, self.highestScore)
		if scores.count > 1 && lowest != highest {
			return (lowest, highest)
		} else {
			return nil
		}
	}
}

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}

// MARK: - Preview

#if DEBUG
struct GamesListHeaderViewPreview: PreviewProvider {
	static var previews: some View {
		List {
			GamesListHeaderView(
				scores: [
					.init(index: 0, score: 0),
					.init(index: 1, score: 0),
					.init(index: 2, score: 0),
				]
			)

			GamesListHeaderView(
				scores: [
					.init(index: 0, score: 5),
					.init(index: 1, score: 445),
				]
			)

			GamesListHeaderView(
				scores: [
					.init(index: 0, score: 233),
					.init(index: 1, score: 198),
					.init(index: 2, score: 204),
					.init(index: 3, score: 238),
					.init(index: 4, score: 221),
					.init(index: 5, score: 253),
					.init(index: 6, score: 304),
					.init(index: 7, score: 208),
					.init(index: 8, score: 210),
					.init(index: 9, score: 193),
					.init(index: 10, score: 357),
					.init(index: 11, score: 368),
				]
			)

			GamesListHeaderView(
				scores: [
					.init(index: 0, score: 233),
					.init(index: 1, score: 198),
					.init(index: 2, score: 204),
					.init(index: 3, score: 238),
					.init(index: 4, score: 221),
					.init(index: 5, score: 253),
					.init(index: 6, score: 304),
					.init(index: 7, score: 208),
					.init(index: 8, score: 210),
					.init(index: 9, score: 193),
					.init(index: 10, score: 357),
					.init(index: 11, score: 368),
				]
			)

			GamesListHeaderView(
				scores: [
					.init(index: 0, score: 233),
				]
			)

			GamesListHeaderView(
				scores: []
			)

			GamesListHeaderView(
				scores: [
					.init(index: 0, score: 445),
					.init(index: 1, score: 5),
				]
			)
		}
	}
}
#endif
