import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class RightOfMiddleHitsTests: XCTestCase {
	func testAdjust_ByFramesWithRightOfMiddleHit_Adjusts() {
		let statistic = create(
			statistic: Statistics.RightOfMiddleHits.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [.init(index: 0, roll: .init(pinsDowned: [.rightTwoPin]))]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin])),
					]
				),
			]
		)

		AssertPercentage(statistic, hasNumerator: 1, withDenominator: 2, formattedAs: "50% (1)")
	}

	func testAdjust_ByFramesWithoutRightOfMiddleHit_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.RightOfMiddleHits.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin]))]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
					]
				),
			]
		)

		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 2, formattedAs: "0%")
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.RightOfMiddleHits.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.RightOfMiddleHits.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}
}
