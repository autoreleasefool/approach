import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class RightsTests: XCTestCase {
	func testAdjust_ByFramesWithRight_Adjusts() {
		let statistic = create(
			statistic: Statistics.Rights.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.rightThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertCounting(statistic, equals: 1)
	}

	func testAdjust_ByFramesWithoutRights_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.Rights.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin, .headPin, .leftThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.rightThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertCounting(statistic, equals: 0)
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.Rights.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.Rights.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}
}
