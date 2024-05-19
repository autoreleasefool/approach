import AssetsLibrary
import Charts
import DateTimeLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

public struct SeriesListItem: View {
	@State private var contentSize: CGSize = .zero

	let series: Series.List

	public var body: some View {
		ZStack(alignment: .bottomLeading) {
			if series.scores.count > 1 {
				Chart {
					ForEach(series.scores) { score in
						AreaMark(
							x: .value(Strings.Series.List.Scores.Chart.xAxisLabel, score.index + 1),
							y: .value(Strings.Series.List.Scores.Chart.yAxisLabel, score.score)
						)
						.foregroundStyle(
							.linearGradient(
								stops: [
									.init(color: Asset.Colors.Charts.Series.areaMark.swiftUIColor, location: 0.3),
									.init(color: Color.clear, location: 0.95),
								],
								startPoint: .leading,
								endPoint: .trailing
							)
						)
						.interpolationMethod(.catmullRom)

						LineMark(
							x: .value(Strings.Series.List.Scores.Chart.xAxisLabel, score.index + 1),
							y: .value(Strings.Series.List.Scores.Chart.yAxisLabel, score.score)
						)
						.lineStyle(StrokeStyle(lineWidth: 2))
						.foregroundStyle(
							.linearGradient(
								stops: [
									.init(color: Asset.Colors.Charts.Series.lineMark.swiftUIColor, location: 0.3),
									.init(color: Color.clear, location: 0.95),
								],
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
				.chartYScale(domain: series.scoreDomain)
				.chartXScale(domain: 1...series.scores.count)
				.frame(
					width: contentSize.width * 0.9,
					height: contentSize.height / 2
				)
			}

			VStack(spacing: .standardSpacing) {
				HStack {
					Image(systemSymbol: .calendar)
						.resizable()
						.frame(width: .smallIcon, height: .smallIcon)
						.padding(.trailing, .standardSpacing)

					VStack(alignment: .leading, spacing: 0) {
						Text(series.primaryDate.longFormat)
							.font(.subheadline)

						if let bowledOnDate = series.bowledOnDate {
							Text(Strings.Series.List.PreBowl.preBowledOn(bowledOnDate.longFormat))
								.font(.caption)
						}
					}

					Spacer()

					Image(systemSymbol: .chevronForward)
						.scaledToFit()
						.frame(width: .tinyIcon, height: .tinyIcon)
				}
				.contentShape(Rectangle())

				HStack(alignment: .lastTextBaseline) {
					VStack(alignment: .leading, spacing: 0) {
						Text(Strings.Series.List.numberOfGames(series.scores.count))
							.font(.caption)
							.padding(.horizontal, .unitSpacing)
							.bold()

						if let range = series.scoreRange {
							Text(Strings.Series.List.Scores.range(range.lowest, range.highest))
								.font(.caption)
								.italic()
								.padding(.horizontal, .unitSpacing)
								.background(
									Material.thinMaterial,
									in: RoundedRectangle(cornerRadius: .standardRadius)
								)
								.padding(.top, .unitSpacing)
						}
					}

					Spacer()

					if series.total > 0 {
						VStack(alignment: .trailing, spacing: 0) {
							Text(String(series.total))
								.font(.title2)
								.fontWeight(.heavy)
								.italic()

							Text(Strings.Series.List.Scores.total)
								.font(.caption2)
								.italic()
						}
					}
				}
			}
			.padding(.smallSpacing)
			.measure(key: ContentSizeKey.self, to: $contentSize)
		}
		.contentShape(Rectangle())
	}
}

extension Series.List {
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

	var scoreDomain: ClosedRange<Int> {
		if let scoreRange {
			return max(scoreRange.lowest - 10, 0)...min(scoreRange.highest + 10, Game.MAXIMUM_SCORE)
		} else {
			return 0...Game.MAXIMUM_SCORE
		}
	}
}

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}

#if DEBUG
struct SeriesListItemPreview: PreviewProvider {
	static var preBowls: [Series.List] = [
		.init(
			id: UUID(1),
			date: Date(),
			appliedDate: Date(),
			scores: [
				.init(index: 0, score: 5),
				.init(index: 1, score: 445),
			],
			total: 450,
			preBowl: .regular
		),
	]

	static var regular: [Series.List] = [
		.init(
			id: UUID(0),
			date: Date(),
			appliedDate: nil,
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
			],
			total: 1651,
			preBowl: .regular
		),
		.init(
			id: UUID(1),
			date: Date(),
			appliedDate: nil,
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
			],
			total: 1651,
			preBowl: .regular
		),
		.init(
			id: UUID(1),
			date: Date(),
			appliedDate: nil,
			scores: [
				.init(index: 0, score: 233),
			],
			total: 233,
			preBowl: .regular
		),
		.init(
			id: UUID(1),
			date: Date(),
			appliedDate: nil,
			scores: [
				.init(index: 0, score: 5),
				.init(index: 1, score: 445),
			],
			total: 450,
			preBowl: .regular
		),
	]

	static var previews: some View {
		List {
			Section {
				ForEach(preBowls) {
					SeriesListItem(series: $0)
						.listRowInsets(EdgeInsets())
						.alignmentGuide(.listRowSeparatorLeading) { d in
								d[.leading]
						}
				}
			}

			Section {
				ForEach(regular) {
					SeriesListItem(series: $0)
						.listRowInsets(EdgeInsets())
						.alignmentGuide(.listRowSeparatorLeading) { d in
								d[.leading]
						}
				}
			}
		}
	}
}
#endif
