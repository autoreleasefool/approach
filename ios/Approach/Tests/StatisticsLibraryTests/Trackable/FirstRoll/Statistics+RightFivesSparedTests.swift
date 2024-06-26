import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class RightFivesSparedTests: XCTestCase {
	func testAdjust_ByFramesWithRightFivesSpared_Adjusts() {
		let statistic = create(
			statistic: Statistics.RightFivesSpared.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 2,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftThreePin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					index: 3,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .rightThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					index: 4,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 1, withDenominator: 1, formattedAs: "100%")
	}

	func testAdjust_ByFramesWithoutRightFivesSpared_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.RightFivesSpared.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [])),
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
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 4,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 1, formattedAs: "0%", overridingIsEmptyExpectation: true)
	}

	func testAdjust_InLastFrame_ByFramesWithRightFivesSpared_Adjusts() {
			let statistic = create(
				statistic: Statistics.RightFivesSpared.self,
				adjustedByFrames: [
					// Open attempt
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .headPin])),
							.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin])),
						]
					),
					// Spared attempt, followed by strike
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin])),
							.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						]
					),
					// Spared attempt, followed by open
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin])),
							.init(index: 2, roll: .init(pinsDowned: [])),
						]
					),
					// Strike, followed by spared attempt
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [.rightThreePin, .rightTwoPin])),
							.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin])),
						]
					),
					// Strike followed by open attempt
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [.rightThreePin, .rightTwoPin])),
							.init(index: 2, roll: .init(pinsDowned: [])),
						]
					),
					// Two strikes, followed by spareable shot
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 2, roll: .init(pinsDowned: [.rightThreePin, .rightTwoPin])),
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

			AssertPercentage(statistic, hasNumerator: 3, withDenominator: 5, formattedAs: "60%")
		}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.RightFivesSpared.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.RightFivesSpared.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}
}
