import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class FoulsTests: XCTestCase {
	func testAdjust_ByFramesWithFouls_Adjusts() {
		let statistic = create(
			statistic: Statistics.Fouls.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [], didFoul: true)),
					]
				),
				Frame.TrackableEntry(
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin], didFoul: true)),
						.init(index: 1, roll: .init(pinsDowned: [.headPin], didFoul: true)),
					]
				),
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 3, withDenominator: 3, formattedAs: "100%")
	}

	func testAdjust_ByFramesWithoutFouls_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.Fouls.self,
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
			],
			withFrameConfiguration: .default
		)

		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 4, formattedAs: "0%")
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.Fouls.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.Fouls.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertPercentage(statistic, hasNumerator: 0, withDenominator: 0, formattedAs: "0%")
	}
}
