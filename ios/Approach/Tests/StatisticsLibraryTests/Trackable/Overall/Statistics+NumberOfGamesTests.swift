import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class NumberOfGamesTests: XCTestCase {
	func testAdjustByGame() {
		let statistic = create(statistic: Statistics.NumberOfGames.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 18)
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.NumberOfGames.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}

	func testAdjustByFrame_DoesNothing() {
		let statistic = create(statistic: Statistics.NumberOfGames.self, adjustedByFrames: Frame.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}
}
