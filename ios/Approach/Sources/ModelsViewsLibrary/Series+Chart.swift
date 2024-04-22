import AssetsLibrary
import Charts
import ModelsLibrary
import StringsLibrary
import SwiftUI

extension Series {
	public struct ScoreChart: View {
		public let scores: [Game.Score]
		public let style: Style

		public init(
			scores: [Game.Score],
			style: Style
		) {
			self.scores = scores
			self.style = style
		}

		public var body: some View {
			if scores.count > 1 {
				Chart {
					ForEach(scores) { score in
						AreaMark(
							x: .value(Strings.Series.List.Scores.Chart.xAxisLabel, score.index + 1),
							y: .value(Strings.Series.List.Scores.Chart.yAxisLabel, score.score)
						)
						.foregroundStyle(style.areaMarkForeground)
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
				.chartYScale(domain: scores.scoreDomain)
				.chartXScale(domain: 1...scores.count)
			} else {
				EmptyView()
			}
		}
	}
}

extension Series.ScoreChart {
	public struct Style {
		public var areaMarkForeground: LinearGradient
		public var lineMarkForeground: LinearGradient

		public init(areaMarkForeground: LinearGradient, lineMarkForeground: LinearGradient) {
			self.areaMarkForeground = areaMarkForeground
			self.lineMarkForeground = lineMarkForeground
		}
	}
}
