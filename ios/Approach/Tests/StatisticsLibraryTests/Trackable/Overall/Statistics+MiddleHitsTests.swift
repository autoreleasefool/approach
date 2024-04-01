import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class MiddleHitsTests: XCTestCase {
	func testAdjust_ByFramesWithMiddleHit_Adjusts() {
		let statistic = create(
			statistic: Statistics.MiddleHits.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin]))]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin])),
					]
				),
			]
		)

		AssertPercentage(statistic, hasNumerator: 1, withDenominator: 2, formattedAs: "50%")
	}

	func testAdjust_ByFramesWithoutMiddleHit_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.MiddleHits.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin]))]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
					]
				),
			]
		)

		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 2, formattedAs: "0%")
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.MiddleHits.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.MiddleHits.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}
}
