import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("Strikes Statistic", .tags(.library, .statistics))
struct StrikesTests {

	@Test("Adjust by frames with strike adjusts correctly", .tags(.unit))
	func adjust_byFramesWithStrike_adjustsCorreclty() {
		let statistic = create(
			statistic: Statistics.Strikes.self,
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
					index: 2,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [])),
						.init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightTwoPin, .rightThreePin])),
					]
				),
			]
		)

		expectPercentage(statistic, hasNumerator: 1, withDenominator: 4, formattedAs: "25%")
	}

	@Test("Adjust by frames without strike does not adjust", .tags(.unit))
	func adjust_byFramesWithoutStrike_doesNotAdjust() {
		let statistic = create(
			statistic: Statistics.Strikes.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin, .headPin])),
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

		expectPercentage(statistic, hasNumerator: 0, withDenominator: 2, formattedAs: "0%")
	}

	@Test("Adjust by series does nothing", .tags(.unit))
	func adjust_bySeries_doesNothing() {
		let statistic = create(statistic: Statistics.Strikes.self, adjustedBySeries: Series.TrackableEntry.mocks)
		expectPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	@Test("Adjust by game does nothing", .tags(.unit))
	func adjust_byGame_doesNothing() {
		let statistic = create(statistic: Statistics.Strikes.self, adjustedByGames: Game.TrackableEntry.mocks)
		expectPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}
}
