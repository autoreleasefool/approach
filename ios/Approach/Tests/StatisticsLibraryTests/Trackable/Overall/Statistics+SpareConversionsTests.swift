import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class SpareConversionsTests: XCTestCase {
	func testAdjust_ByFramesWithSpare_Adjusts() {
		let statistic = create(
			statistic: Statistics.SpareConversions.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .leftTwoPin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 2,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 3,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 4,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 5,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 6,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
					]
				),
			]
		)

		AssertPercentage(statistic, hasNumerator: 3, withDenominator: 4, formattedAs: "75%")
	}

	func testAdjust_ByFramesWithoutSpare_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.SpareConversions.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
						.init(index: 2, roll: .init(pinsDowned: [.headPin])),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
					]
				),
			]
		)

		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 1, formattedAs: "0%", overridingIsEmptyExpectation: true)
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.SpareConversions.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.SpareConversions.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}
}
