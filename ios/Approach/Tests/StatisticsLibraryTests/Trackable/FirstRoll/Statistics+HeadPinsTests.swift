import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class HeadPinsTests: XCTestCase {
	func testAdjust_ByFramesWithHeadPin_Adjusts() {
		var statistic = Statistics.HeadPins()

		let frameList = [
			Frame.TrackableEntry(
				index: 0,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin]))]
			),
			Frame.TrackableEntry(
				index: 1,
				rolls: [
					.init(index: 0, roll: .init(pinsDowned: [.leftThreePin])),
					.init(index: 1, roll: .init(pinsDowned: [.headPin])),
				]
			),
		]

		for frame in frameList {
			statistic.adjust(byFrame: frame, configuration: .init(countHeadPin2AsHeadPin: false))
		}

		AssertCounting(statistic, equals: 1)
	}

	func testAdjust_ByFramesWithHeadPin2_WithHeadPin2Enabled_Adjusts() {
		var statistic = Statistics.HeadPins()

		let frameList = [
			Frame.TrackableEntry(
				index: 0,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin]))]
			),
			Frame.TrackableEntry(
				index: 1,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin]))]
			),
			Frame.TrackableEntry(
				index: 2,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin]))]
			),
			Frame.TrackableEntry(
				index: 3,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .leftTwoPin]))]
			),
		]

		for frame in frameList {
			statistic.adjust(byFrame: frame, configuration: .init(countHeadPin2AsHeadPin: true))
		}

		AssertCounting(statistic, equals: 3)
	}

	func testAdjust_ByFramesWithHeadPin2_WithHeadPin2Disabled_DoesNotAdjust() {
		var statistic = Statistics.HeadPins()

		let frameList = [
			Frame.TrackableEntry(
				index: 0,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin]))]
			),
			Frame.TrackableEntry(
				index: 1,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin]))]
			),
			Frame.TrackableEntry(
				index: 2,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin]))]
			),
			Frame.TrackableEntry(
				index: 3,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .leftTwoPin]))]
			),
		]

		for frame in frameList {
			statistic.adjust(byFrame: frame, configuration: .init(countHeadPin2AsHeadPin: false))
		}

		AssertCounting(statistic, equals: 1)
	}
}
