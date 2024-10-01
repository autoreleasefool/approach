import AssetsLibrary
import Charts
import ModelsLibrary
import StringsLibrary
import SwiftUI

extension Series {
	public struct ScoreChart: View {
		public let id: Series.ID
		public let scores: [Game.Score]
		public let style: Style

		public init(
			id: Series.ID,
			scores: [Game.Score],
			style: Style
		) {
			self.id = id
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
						.lineStyle(StrokeStyle(lineWidth: style.lineWidth))
						.foregroundStyle(style.lineMarkForeground)
						.interpolationMethod(.catmullRom)

						if style.annotateMaxScore, let highestScoreAnnotation = scores.highestScoreAnnotation {
							ScoreAnnotation(
								score: highestScoreAnnotation.score,
								index: highestScoreAnnotation.index,
								position: annotationPosition(forIndex: highestScoreAnnotation.index, in: scores.indices),
								foreground: style.annotationForeground
							)
						}

						if style.annotateMinScore && (!style.annotateMaxScore || scores.lowestScore != scores.highestScore),
							 let lowestScoreAnnotation = scores.lowestScoreAnnotation {
							ScoreAnnotation(
								score: lowestScoreAnnotation.score,
								index: lowestScoreAnnotation.index,
								position: annotationPosition(forIndex: lowestScoreAnnotation.index, in: scores.indices),
								foreground: style.annotationForeground
							)
						}
					}
				}
				.id(id)
				.chartXAxis(.hidden)
				.chartYAxis(.hidden)
				.chartLegend(.hidden)
				.chartYScale(domain: scores.scoreDomain.coallescing(style.scoreDomain))
				.chartXScale(domain: 1...scores.count)
			} else {
				EmptyView()
			}
		}

		private func annotationPosition(forIndex index: Int, in range: Range<Int>) -> AnnotationPosition {
			if index == range.first {
				return .topTrailing
			} else if index == range.last {
				return .topLeading
			} else {
				return .top
			}
		}
	}
}

struct ScoreAnnotation: ChartContent {
	let score: Int
	let index: Int
	let position: AnnotationPosition
	let foreground: Color

	var body: some ChartContent {
		PointMark(
			x: .value(Strings.Series.List.Scores.Chart.xAxisLabel, index + 1),
			y: .value(Strings.Series.List.Scores.Chart.yAxisLabel, score)
		)
		.symbolSize(CGSize(width: 8, height: 8))
		.foregroundStyle(foreground)
		.annotation(position: position, spacing: 0) {
			Text("\(score)")
				.font(.caption.weight(.bold))
		}
	}
}

extension Series.ScoreChart {
	public struct Style: Sendable {
		public let areaMarkForeground: LinearGradient
		public let lineMarkForeground: LinearGradient
		public let annotationForeground: Color
		public let lineWidth: CGFloat
		public let annotateMaxScore: Bool
		public let annotateMinScore: Bool
		public let scoreDomain: ClosedRange<Int>?

		public init(
			areaMarkForeground: LinearGradient,
			lineMarkForeground: LinearGradient,
			annotationForeground: Color = .clear,
			lineWidth: CGFloat = 2,
			annotateMaxScore: Bool = false,
			annotateMinScore: Bool = false,
			scoreDomain: ClosedRange<Int>? = nil
		) {
			self.areaMarkForeground = areaMarkForeground
			self.lineMarkForeground = lineMarkForeground
			self.annotationForeground = annotationForeground
			self.lineWidth = lineWidth
			self.annotateMaxScore = annotateMaxScore
			self.annotateMinScore = annotateMinScore
			self.scoreDomain = scoreDomain
		}
	}
}

extension Array where Element == Game.Score {
	var lowestScoreAnnotation: (index: Int, score: Int)? {
		let lowestScore = self.lowestScore
		if let index = self.firstIndex(where: { $0.score == lowestScore }) {
			return (index, lowestScore)
		} else {
			return nil
		}
	}

	var highestScoreAnnotation: (index: Int, score: Int)? {
		let highestScore = self.highestScore
		if let index = self.firstIndex(where: { $0.score == highestScore }) {
			return (index, highestScore)
		} else {
			return nil
		}
	}
}

extension ClosedRange where Bound == Int {
	func coallescing(_ other: ClosedRange<Int>?) -> ClosedRange<Int> {
		guard let other else { return self }
		let coallescedLowerBound = other.lowerBound > lowerBound ? lowerBound : other.lowerBound
		let coallescedUpperBound = other.upperBound > upperBound ? other.upperBound : upperBound
		return (coallescedLowerBound)...(coallescedUpperBound)
	}
}
