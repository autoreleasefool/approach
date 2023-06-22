import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class AcesTests: XCTestCase {
	func testAdjust_ByFramesWithAces_Adjusts() {
		let statistic = create(
			statistic: Statistics.Aces.self,
			adjustedByFrames: [
				Frame.TrackableEntry(
					index: 0,
					rolls: [
						.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightThreePin, .leftThreePin]))
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
			withFrameConfiguration: .init(countHeadPin2AsHeadPin: false)
		)

		AssertCounting(statistic, equals: 1)
	}

	func testAdjust_ByFramesWithoutAces_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.Aces.self,
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
			withFrameConfiguration: .init(countHeadPin2AsHeadPin: false)
		)

		AssertCounting(statistic, equals: 0)
	}

	func testAdjustBySeries_DoesNothing() {
		let statistic = create(statistic: Statistics.Aces.self, adjustedBySeries: Series.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.Aces.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertCounting(statistic, equals: 0)
	}
}
