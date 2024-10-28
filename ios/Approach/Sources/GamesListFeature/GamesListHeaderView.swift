import AssetsLibrary
import Charts
import DateTimeLibrary
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

public struct GamesListHeaderView: View {
	@State private var contentSize: CGSize = .zero

	let id: Series.ID
	let scores: [Game.Score]
	let total: Int

	init(id: Series.ID, scores: [Game.Score]) {
		self.id = id
		self.scores = scores
		self.total = scores.reduce(0) { $0 + $1.score }
	}

	public var body: some View {
		Section {
			if scores.isEmpty {
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
					Series.ScoreChart(
						id: id,
						scores: scores,
						style: .gamesListHeader
					)
					.frame(width: contentSize.width, height: contentSize.height * 0.7)

					VStack {
						HStack(alignment: .firstTextBaseline) {
							VStack(alignment: .leading, spacing: 0) {
								Text(Strings.Game.List.Header.numberOfGames(scores.count))
									.font(.subheadline)
									.bold()

								if let scoreRange = scores.scoreRange {
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
	@State private var id = UUID()

	private static let scores: [Game.Score] = [
		.init(index: 0, score: 0),
		.init(index: 1, score: 200),
		.init(index: 2, score: 125),
		.init(index: 3, score: 300),
	]

	var body: some View {
		ZStack(alignment: .bottomLeading) {
			Series.ScoreChart(
				id: id,
				scores: MockGamesListHeaderView.scores,
				style: .gamesListHeaderMock
			)
			.frame(width: contentSize.width, height: contentSize.height * 0.7)

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

extension Series.ScoreChart.Style {
	static let gamesListHeader: Self = Self(
		areaMarkForeground: .linearGradient(
			stops: gradientStops(withColor: Asset.Colors.Charts.Game.areaMark.swiftUIColor),
			startPoint: .leading,
			endPoint: .trailing
		),
		lineMarkForeground: .linearGradient(
			stops: gradientStops(withColor: Asset.Colors.Charts.Game.lineMark.swiftUIColor),
			startPoint: .leading,
			endPoint: .trailing
		)
	)

	static let gamesListHeaderMock: Self = Self(
		areaMarkForeground: .linearGradient(
			stops: gradientStops(withColor: Asset.Colors.Charts.Mock.areaMark.swiftUIColor),
			startPoint: .leading,
			endPoint: .trailing
		),
		lineMarkForeground: .linearGradient(
			stops: gradientStops(withColor: Asset.Colors.Charts.Mock.lineMark.swiftUIColor),
			startPoint: .leading,
			endPoint: .trailing
		)
	)

	private static func gradientStops(withColor: Color) -> [Gradient.Stop] {
		[
			.init(color: Color.clear, location: 0),
			.init(color: withColor.opacity(0.4), location: 0.3),
			.init(color: withColor.opacity(0.5), location: 0.7),
			.init(color: Color.clear, location: 1),
		]
	}
}

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}

// MARK: - Preview

#if DEBUG
struct GamesListHeaderViewPreview: PreviewProvider {
	static var previews: some View {
		List {
			GamesListHeaderView(
				id: UUID(),
				scores: [
					.init(index: 0, score: 0),
					.init(index: 1, score: 0),
					.init(index: 2, score: 0),
				]
			)

			GamesListHeaderView(
				id: UUID(),
				scores: [
					.init(index: 0, score: 5),
					.init(index: 1, score: 445),
				]
			)

			GamesListHeaderView(
				id: UUID(),
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
				id: UUID(),
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
				id: UUID(),
				scores: [
					.init(index: 0, score: 233),
				]
			)

			GamesListHeaderView(
				id: UUID(),
				scores: []
			)

			GamesListHeaderView(
				id: UUID(),
				scores: [
					.init(index: 0, score: 445),
					.init(index: 1, score: 5),
				]
			)
		}
	}
}
#endif
