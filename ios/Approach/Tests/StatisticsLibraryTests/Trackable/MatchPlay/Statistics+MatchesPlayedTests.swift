import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class MatchesPlayedTests: XCTestCase {
	func testAdjustByGame() {
		let statistic = create(statistic: Statistics.MatchesPlayed.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 9)
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.MatchesPlayed.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}

	func testAdjustByFrame_DoesNothing() {
		let statistic = create(statistic: Statistics.MatchesPlayed.self, adjustedByFrames: Frame.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}
}
