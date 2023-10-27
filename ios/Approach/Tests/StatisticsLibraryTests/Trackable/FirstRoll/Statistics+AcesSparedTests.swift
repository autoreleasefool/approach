import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class AcesSparedTests: XCTestCase {
	func testAdjust_ByFramesWithAcesSpared_Adjusts() {
		let statistic = create(
			statistic: Statistics.AcesSpared.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .rightTwoPin])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 1, withDenominator: 2, formattedAs: "50% (1)")
	}

	func testAdjust_ByFramesWithoutAcesSpared_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.AcesSpared.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin])),
					]
				),
				Frame.TrackableEntry(
					index: 2,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 1, formattedAs: "0%", overridingIsEmptyExpectation: true)
	}

	func testAdjust_InLastFrame_ByFramesWithAcesSpared_Adjusts() {
		let statistic = create(
			statistic: Statistics.AcesSpared.self,
			adjustedByFrames: [
				// Open attempt
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin])),
					]
				),
				// Spared attempt, followed by strike
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .rightTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
					]
				),
				// Spared attempt, followed by open
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .rightTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [])),
					]
				),
				// Strike, followed by spared attempt
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin])),
						.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .rightTwoPin])),
					]
				),
				// Strike followed by open attempt
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin])),
						.init(index: 2, roll: .init(pinsDowned: [])),
					]
				),
				// Two strikes, followed by spareable shot
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin])),
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
			]
		)

		AssertPercentage(statistic, hasNumerator: 3, withDenominator: 5, formattedAs: "60% (3)")
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.AcesSpared.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.AcesSpared.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}
}
