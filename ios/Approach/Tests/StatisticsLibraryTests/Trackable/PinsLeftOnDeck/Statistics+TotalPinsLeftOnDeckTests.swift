import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class TotalPinsLeftOnDeckTests: XCTestCase {
	func testAdjustByFrames() {
		let statistic = create(
			statistic: Statistics.TotalPinsLeftOnDeck.self,
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
						.init(index: 0, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					index: 9,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 9,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					index: 9,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					index: 9,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [])),
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin])),
					]
				),
			]
		)

		AssertCounting(statistic, equals: 43)
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.TotalPinsLeftOnDeck.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.TotalPinsLeftOnDeck.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}
}
