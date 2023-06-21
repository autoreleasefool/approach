import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class HighSeriesOf3Tests: XCTestCase {
	func testAdjustBy_SeriesWith3Games_Adjusts() {
		let statistic = create(statistic: Statistics.HighSeriesOf3.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertHighestOf(statistic, equals: 456)
	}

	func testAdjustBy_SeriesNotWith3Games_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.HighSeriesOf3.self,
			adjustedBySeries: [
				Series.TrackableEntry(id: UUID(0), numberOfGames: 2, total: 123, date: Date()),
				Series.TrackableEntry(id: UUID(1), numberOfGames: 4, total: 456, date: Date()),
			]
		)

		AssertHighestOf(statistic, equals: 0)
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.HighSeriesOf3.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertHighestOf(statistic, equals: 0)
	}

	func testAdjustByFrame_DoesNothing() {
		let statistic = create(statistic: Statistics.HighSeriesOf3.self, adjustedByFrames: Frame.TrackableEntry.mocks)
		AssertHighestOf(statistic, equals: 0)
	}
}
