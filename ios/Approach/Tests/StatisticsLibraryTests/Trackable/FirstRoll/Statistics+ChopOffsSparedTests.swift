import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class ChopOffsSparedTests: XCTestCase {
	func testAdjust_ByFramesWithChopOffsSpared_Adjusts() {
		let statistic = create(
			statistic: Statistics.ChopOffsSpared.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					index: 2,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftThreePin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 3,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftThreePin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					index: 4,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .rightThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 2, withDenominator: 4, formattedAs: "50%")
	}

	func testAdjust_ByFramesWithoutChopOffsSpared_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.ChopOffsSpared.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 2,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightThreePin, .leftTwoPin])),
					]
				),
				Frame.TrackableEntry(
					index: 3,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					index: 4,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftThreePin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 2, formattedAs: "0%")
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.ChopOffsSpared.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.ChopOffsSpared.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}
}
