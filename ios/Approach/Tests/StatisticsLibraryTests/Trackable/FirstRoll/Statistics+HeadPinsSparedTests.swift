import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class HeadPinsSparedTests: XCTestCase {
	func testAdjust_ByFramesWithHeadPinSpared_Adjusts() {
		let statistic = create(
			statistic: Statistics.HeadPinsSpared.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 1, withDenominator: 2, formattedAs: "50%")
	}

	func testAdjust_ByFramesWithHeadPin2Spared_WithHeadPin2Enabled_Adjusts() {
		let statistic = create(
			statistic: Statistics.HeadPinsSpared.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 2,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .leftTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 3,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .leftTwoPin])),
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin])),
					]
				),
			],
			withFrameConfiguration: .init(countHeadPin2AsHeadPin: true, countSplitWithBonusAsSplit: false)
		)

		AssertPercentage(statistic, hasNumerator: 2, withDenominator: 2, formattedAs: "100%")
	}

	func testAdjust_ByFramesWithHeadPin2Spared_WithHeadPin2Disabled_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.HeadPinsSpared.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 2,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .leftTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 3,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .leftTwoPin])),
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	func testAdjust_InLastFrame_ByFramesWithHeadPinsSpared_Adjusts() {
		let statistic = create(
			statistic: Statistics.HeadPinsSpared.self,
			adjustedByFrames: [
				// Open attempt
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .leftTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
					]
				),
				// Spared attempt, followed by strike
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
					]
				),
				// Spared attempt, followed by open
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 2, roll: .init(pinsDowned: [])),
					]
				),
				// Strike, followed by spared attempt
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin, .leftTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.leftThreePin, .rightTwoPin, .rightThreePin])),
					]
				),
				// Strike followed by open attempt
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin, .rightTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [])),
					]
				),
				// Two strikes, followed by spareable shot
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.headPin])),
					]
				),
				// Three strikes
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
					]
				),
			],
			withFrameConfiguration: .init(countHeadPin2AsHeadPin: true, countSplitWithBonusAsSplit: false)
		)

		AssertPercentage(statistic, hasNumerator: 3, withDenominator: 5, formattedAs: "60%")
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.HeadPinsSpared.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.HeadPinsSpared.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}
}
