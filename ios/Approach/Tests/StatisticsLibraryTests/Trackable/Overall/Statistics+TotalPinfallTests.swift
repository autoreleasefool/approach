import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class TotalPinfallTests: XCTestCase {
	func testAdjustByGame() {
		let statistic = create(statistic: Statistics.TotalPinfall.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 3213)
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.TotalPinfall.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}

	func testAdjustByFrame_DoesNothing() {
		let statistic = create(statistic: Statistics.TotalPinfall.self, adjustedByFrames: Frame.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}
}
