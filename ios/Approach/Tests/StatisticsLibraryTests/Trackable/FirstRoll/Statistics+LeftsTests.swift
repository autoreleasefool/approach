import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class LeftsTests: XCTestCase {
	func testAdjust_ByFramesWithLeft_Adjusts() {
		let statistic = create(
			statistic: Statistics.Lefts.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin, .headPin, .leftThreePin]))
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin])),
					]
				),
			],
			withFrameConfiguration: .init(countHeadPin2AsHeadPin: false)
		)

		AssertCounting(statistic, equals: 1)
	}

	func testAdjust_ByFramesWithoutLefts_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.Lefts.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .rightThreePin, .headPin, .leftThreePin]))
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin])),
					]
				),
			],
			withFrameConfiguration: .init(countHeadPin2AsHeadPin: false)
		)

		AssertCounting(statistic, equals: 0)
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.Lefts.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.Lefts.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}
}
