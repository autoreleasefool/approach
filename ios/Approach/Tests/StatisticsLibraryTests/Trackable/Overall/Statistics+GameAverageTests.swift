import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class GameAverageTests: XCTestCase {
	func testAdjustByGame() {
		let statistic = create(statistic: Statistics.GameAverage.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertAveraging(statistic, hasTotal: 3213, withDivisor: 18, formattedAs: "178.5")
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.GameAverage.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertAveraging(statistic, hasTotal: 0, withDivisor: 0, formattedAs: "—")
	}

	func testAdjustByFrame_DoesNothing() {
		let statistic = create(statistic: Statistics.GameAverage.self, adjustedByFrames: Frame.TrackableEntry.mocks)
		AssertAveraging(statistic, hasTotal: 0, withDivisor: 0, formattedAs: "—")
	}

	func testDoesNotAdjustByZeroGames() {
		let statistic = create(
			statistic: Statistics.GameAverage.self,
			adjustedByGames: [
				Game.TrackableEntry(seriesId: UUID(0), id: UUID(0), score: 100, date: Date(timeIntervalSince1970: 123)),
				Game.TrackableEntry(seriesId: UUID(0), id: UUID(1), score: 120, date: Date(timeIntervalSince1970: 123)),
				Game.TrackableEntry(seriesId: UUID(0), id: UUID(2), score: 0, date: Date(timeIntervalSince1970: 123)),
			]
		)

		AssertAveraging(statistic, hasTotal: 220, withDivisor: 2, formattedAs: "110")
	}
}
