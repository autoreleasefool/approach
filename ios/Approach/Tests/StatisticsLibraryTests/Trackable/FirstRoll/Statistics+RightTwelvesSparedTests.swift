import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class RightTwelvesSparedTests: XCTestCase {
	func testAdjust_ByFramesWithRightTwelvesSpared_Adjusts() {
		let statistic = create(
			statistic: Statistics.RightTwelvesSpared.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
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
						.init(index: 1, roll: .init(pinsDowned: [])),
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
				Frame.TrackableEntry(
					index: 5,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 1, withDenominator: 3, formattedAs: "33.3% (1)")
	}

	func testAdjust_ByFramesWithoutRightTwelvesSpared_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.RightTwelvesSpared.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
						.init(index: 2, roll: .init(pinsDowned: [.leftThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 2,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftThreePin, .leftTwoPin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					index: 3,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftThreePin, .leftTwoPin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
						.init(index: 2, roll: .init(pinsDowned: [.rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 4,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin])),
					]
				),
				Frame.TrackableEntry(
					index: 5,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .rightTwoPin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 3, formattedAs: "0%", overridingIsEmptyExpectation: true)
	}

	func testAdjust_InLastFrame_ByFramesWithRightTwelvesSpared_Adjusts() {
			let statistic = create(
				statistic: Statistics.RightTwelvesSpared.self,
				adjustedByFrames: [
					// Open attempt
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [])),
							.init(index: 2, roll: .init(pinsDowned: [.leftThreePin])),
						]
					),
					// Spared attempt, followed by strike
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [.leftThreePin])),
							.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						]
					),
					// Spared attempt, followed by open
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [.leftThreePin])),
							.init(index: 2, roll: .init(pinsDowned: [])),
						]
					),
					// Strike, followed by spared attempt
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 2, roll: .init(pinsDowned: [.leftThreePin])),
						]
					),
					// Strike followed by open attempt
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 2, roll: .init(pinsDowned: [])),
						]
					),
					// Two strikes, followed by spareable shot
					Frame.TrackableEntry(
						index: Game.NUMBER_OF_FRAMES - 1,
						rolls: [
							.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
							.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .headPin, .rightThreePin, .rightTwoPin])),
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
		let statistic = create(statistic: Statistics.RightTwelvesSpared.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.RightTwelvesSpared.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}
}
