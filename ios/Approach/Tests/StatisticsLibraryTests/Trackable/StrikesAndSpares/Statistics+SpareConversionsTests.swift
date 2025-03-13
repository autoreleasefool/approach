import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("SpareConversions Statistic", .tags(.library, .statistics))
struct SpareConversionsTests {

	@Test("Adjust by frames with spare adjusts correctly")
	func adjust_byFramesWithSpare_adjustsCorrectly() {
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

		expectPercentage(statistic, hasNumerator: 3, withDenominator: 4, formattedAs: "75%")
	}

	@Test("Adjust by frames without spare does not adjust")
	func adjust_byFramesWithoutSpare_doesNotAdjust() {
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

		expectPercentage(statistic, hasNumerator: 0, withDenominator: 1, formattedAs: "0%", overridingIsEmptyExpectation: true)
	}

	@Test("Adjust by frames with spareable adjusts correctly")
	func adjust_inLastFrame_byFramesWithSpare_adjustsCorrectly() {
		let statistic = create(
			statistic: Statistics.SpareConversions.self,
			adjustedByFrames: [
				// Open attempt
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [])),
						.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin])),
					]
				),
				// Spared attempt, followed by strike
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
					]
				),
				// Spared attempt, followed by open
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [])),
					]
				),
				// Strike, followed by spared attempt
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin])),
					]
				),
				// Strike followed by open attempt
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [])),
					]
				),
				// Two strikes, followed by spareable shot
				Frame.TrackableEntry(
					index: Game.NUMBER_OF_FRAMES - 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.leftTwoPin])),
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

		expectPercentage(statistic, hasNumerator: 3, withDenominator: 5, formattedAs: "60%")
	}

	@Test("Adjust by series does nothing")
	func adjust_bySeries_doesNothing() {
		let statistic = create(statistic: Statistics.SpareConversions.self, adjustedBySeries: Series.TrackableEntry.mocks)
		expectPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	@Test("Adjust by game does nothing")
	func adjust_byGame_doesNothing() {
		let statistic = create(statistic: Statistics.SpareConversions.self, adjustedByGames: Game.TrackableEntry.mocks)
		expectPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}
}
