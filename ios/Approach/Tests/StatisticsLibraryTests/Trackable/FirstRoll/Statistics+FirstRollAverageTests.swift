import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class FirstRollAverageTests: XCTestCase {
	func testAdjustByFrames() {
		let statistic = create(
			statistic: Statistics.FirstRollAverage.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					gameId: UUID(0),
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .rightTwoPin])),
						.init(index: 0, roll: .init(pinsDowned: [.leftThreePin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					gameId: UUID(0),
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin, .headPin, .leftThreePin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin, .headPin, .leftThreePin, .leftTwoPin])),
					]
				),
				Frame.TrackableEntry(
					gameId: UUID(0),
					index: 2,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin, .headPin, .leftThreePin, .leftTwoPin])),
					]
				),
				Frame.TrackableEntry(
					gameId: UUID(0),
					index: 9,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin, .headPin, .leftThreePin, .leftTwoPin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin, .headPin, .leftThreePin, .leftTwoPin])),
						.init(index: 2, roll: .init(pinsDowned: [.rightTwoPin])),
					]
				),
			]
		)

		AssertAveraging(statistic, hasTotal: 51, withDivisor: 6, formattedAs: "8.5")
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.FirstRollAverage.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertAveraging(statistic, hasTotal: 0, withDivisor: 0, formattedAs: "—")
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.FirstRollAverage.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertAveraging(statistic, hasTotal: 0, withDivisor: 0, formattedAs: "—")
	}
}
