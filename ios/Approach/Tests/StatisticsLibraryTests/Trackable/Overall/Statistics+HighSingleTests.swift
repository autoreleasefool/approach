import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class HighSingleTests: XCTestCase {
	func testAdjustByGame() {
		let statistic = create(statistic: Statistics.HighSingle.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertHighestOf(statistic, equals: 234)
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.HighSingle.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertHighestOf(statistic, equals: 0)
	}

	func testAdjustByFrame_DoesNothing() {
		let statistic = create(statistic: Statistics.HighSingle.self, adjustedByFrames: Frame.TrackableEntry.mocks)
		AssertHighestOf(statistic, equals: 0)
	}
}
