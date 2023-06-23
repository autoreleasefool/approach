import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class TwelvesTests: XCTestCase {
	func testAdjust_ByFramesWithTwelves_Adjusts() {
		let statistic = create(
			statistic: Statistics.Twelves.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin, .leftTwoPin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 2,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftThreePin, .leftTwoPin, .rightTwoPin])),
					]
				),
				Frame.TrackableEntry(
					index: 3,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftThreePin, .leftTwoPin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 4,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertCounting(statistic, equals: 4)
	}

	func testAdjust_ByFramesWithoutTwelves_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.Twelves.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin, .leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertCounting(statistic, equals: 0)
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.Twelves.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.Twelves.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}
}
