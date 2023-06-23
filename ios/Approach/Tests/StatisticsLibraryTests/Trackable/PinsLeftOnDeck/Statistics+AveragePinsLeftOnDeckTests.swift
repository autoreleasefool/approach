import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class AveragePinsLeftOnDeckTests: XCTestCase {
	func testAdjustByFrames() {
		let statistic = create(
			statistic: Statistics.AveragePinsLeftOnDeck.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					gameId: UUID(0),
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin])),
						.init(index: 1, roll: .init(pinsDowned: [.rightTwoPin, .rightThreePin])),
						.init(index: 2, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					gameId: UUID(0),
					index: 1,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin])),
						.init(index: 0, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					gameId: UUID(0),
					index: 9,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					gameId: UUID(1),
					index: 9,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
					]
				),
				Frame.TrackableEntry(
					gameId: UUID(2),
					index: 9,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [])),
					]
				),
				Frame.TrackableEntry(
					gameId: UUID(3),
					index: 9,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [])),
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin, .leftThreePin, .rightTwoPin, .rightThreePin])),
						.init(index: 0, roll: .init(pinsDowned: [.leftTwoPin])),
					]
				),
			]
		)

		AssertAveraging(statistic, hasTotal: 48, withDivisor: 4, formattedAs: "12")
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.AveragePinsLeftOnDeck.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertAveraging(statistic, hasTotal: 0, withDivisor: 0, formattedAs: "—")
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.AveragePinsLeftOnDeck.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertAveraging(statistic, hasTotal: 0, withDivisor: 0, formattedAs: "—")
	}
}
