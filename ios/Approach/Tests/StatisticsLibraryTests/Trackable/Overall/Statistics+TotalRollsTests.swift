import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class TotalRollsTests: XCTestCase {
	func testAdjustByFrames() {
		let statistic = create(statistic: Statistics.TotalRolls.self, adjustedByFrames: Frame.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 18)
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.TotalRolls.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.TotalRolls.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}
}
