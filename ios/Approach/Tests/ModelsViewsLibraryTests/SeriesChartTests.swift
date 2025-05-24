import AssetsLibrary
@testable import ModelsLibrary
@testable import ModelsViewsLibrary
import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary

@Suite("SeriesChart", .tags(.library), .snapshots(record: .failed))
struct SeriesChartTests {

	@Test(
		"Series Chart snapshots",
		.tags(.snapshot),
		arguments: [
			[],
			[120, 240],
			[240, 120],
			[300, 450, 450, 300, 450],
			[400, 0, 200, 80, 380, 120, 180, 200, 0, 90]
		]
	)
	@MainActor
	func snapshotSeriesChart(scores: [Int]) {
		let gameScores = scores.enumerated().map {
			Game.Score(index: $0.offset, score: $0.element)
		}

		let view = Series.ScoreChart(
			id: Series.ID(),
			scores: gameScores,
			style: .init(
				areaMarkForeground: Self.areaMarkForeground,
				lineMarkForeground: Self.lineMarkForeground
			)
		)
			.frame(width: 300, height: 400)

		assertSnapshot(
			of: view,
			as: .image,
			named: scores.map(String.init).joined(separator: ",")
		)
	}

	@Test("Series Chart with annotations", .tags(.snapshot))
	@MainActor
	func snapshotWithAnnotations() {
		let view = Series.ScoreChart(
			id: Series.ID(),
			scores: [
				Game.Score(index: 0, score: 190),
				Game.Score(index: 1, score: 220),
				Game.Score(index: 2, score: 230),
				Game.Score(index: 3, score: 180),
			],
			style: .init(
				areaMarkForeground: Self.areaMarkForeground,
				lineMarkForeground: Self.lineMarkForeground,
				annotationForeground: Self.annotationForeground,
				annotateMaxScore: true,
				annotateMinScore: true
			)
		)
			.frame(width: 300, height: 400)

		assertSnapshot(of: view, as: .image)
	}

	@Test("Series Chart with line width", .tags(.snapshot))
	@MainActor
	func snapshotWithLineWidth() {
		let view = Series.ScoreChart(
			id: Series.ID(),
			scores: [
				Game.Score(index: 0, score: 190),
				Game.Score(index: 1, score: 220),
				Game.Score(index: 2, score: 230),
				Game.Score(index: 3, score: 180),
			],
			style: .init(
				areaMarkForeground: Self.areaMarkForeground,
				lineMarkForeground: Self.lineMarkForeground,
				lineWidth: 8
			)
		)
			.frame(width: 300, height: 400)

		assertSnapshot(of: view, as: .image)
	}

	@Test("Series Chart with score domain", .tags(.snapshot))
	@MainActor
	func snapshotWithScoreDomain() {
		let view = Series.ScoreChart(
			id: Series.ID(),
			scores: [
				Game.Score(index: 0, score: 190),
				Game.Score(index: 1, score: 220),
				Game.Score(index: 2, score: 230),
				Game.Score(index: 3, score: 180),
			],
			style: .init(
				areaMarkForeground: Self.areaMarkForeground,
				lineMarkForeground: Self.lineMarkForeground,
				scoreDomain: 100...300
			)
		)
			.frame(width: 300, height: 400)

		assertSnapshot(of: view, as: .image)
	}
}

// MARK: Series.ScoreChart.Style

extension SeriesChartTests {
	static var areaMarkForeground: LinearGradient {
		.linearGradient(
			stops: [
				.init(color: Asset.Colors.Charts.Series.areaMark.swiftUIColor, location: 0.3),
				.init(color: Color.clear, location: 0.95),
			],
			startPoint: .leading,
			endPoint: .trailing
		)
	}

	static var lineMarkForeground: LinearGradient {
		.linearGradient(
			stops: [
				.init(color: Asset.Colors.Charts.Series.lineMark.swiftUIColor, location: 0.3),
				.init(color: Color.clear, location: 0.95),
			],
			startPoint: .leading,
			endPoint: .trailing
		)
	}

	static var annotationForeground: Color {
		Asset.Colors.Charts.Series.annotation.swiftUIColor
	}
}
