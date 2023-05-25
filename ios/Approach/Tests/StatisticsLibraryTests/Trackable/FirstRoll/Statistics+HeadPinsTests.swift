import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class HeadPinsTests: XCTestCase {
	func testAdjust_ByFramesWithHeadPin_Adjusts() {
		var statistic = Statistics.HeadPins()

		let frameList = [
			Frame.TrackableEntry(
				gameId: UUID(0),
				index: 0,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil)]
			),
			Frame.TrackableEntry(
				gameId: UUID(0),
				index: 1,
				rolls: [
					.init(index: 0, roll: .init(pinsDowned: [.leftThreePin], didFoul: false), bowlingBall: nil),
					.init(index: 1, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil),
				]
			),
		]

		for frame in frameList {
			statistic.adjust(byFrame: frame, configuration: .init(countHeadPin2AsHeadPin: false))
		}

		XCTAssertEqual(statistic.value, "1")
	}

	func testAdjust_ByFramesWithHeadPin2_WithHeadPin2Enabled_Adjusts() {
		var statistic = Statistics.HeadPins()

		let frameList = [
			Frame.TrackableEntry(
				gameId: UUID(0),
				index: 0,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil)]
			),
			Frame.TrackableEntry(
				gameId: UUID(0),
				index: 1,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin], didFoul: false), bowlingBall: nil)]
			),
			Frame.TrackableEntry(
				gameId: UUID(0),
				index: 2,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin], didFoul: false), bowlingBall: nil)]
			),
			Frame.TrackableEntry(
				gameId: UUID(0),
				index: 3,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .leftTwoPin], didFoul: false), bowlingBall: nil)]
			),
		]

		for frame in frameList {
			statistic.adjust(byFrame: frame, configuration: .init(countHeadPin2AsHeadPin: true))
		}

		XCTAssertEqual(statistic.value, "3")
	}

	func testAdjust_ByFramesWithHeadPin2_WithHeadPin2Disabled_DoesNotAdjust() {
		var statistic = Statistics.HeadPins()

		let frameList = [
			Frame.TrackableEntry(
				gameId: UUID(0),
				index: 0,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil)]
			),
			Frame.TrackableEntry(
				gameId: UUID(0),
				index: 1,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .leftTwoPin], didFoul: false), bowlingBall: nil)]
			),
			Frame.TrackableEntry(
				gameId: UUID(0),
				index: 2,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin], didFoul: false), bowlingBall: nil)]
			),
			Frame.TrackableEntry(
				gameId: UUID(0),
				index: 3,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin, .leftTwoPin], didFoul: false), bowlingBall: nil)]
			),
		]

		for frame in frameList {
			statistic.adjust(byFrame: frame, configuration: .init(countHeadPin2AsHeadPin: false))
		}

		XCTAssertEqual(statistic.value, "1")
	}
}
