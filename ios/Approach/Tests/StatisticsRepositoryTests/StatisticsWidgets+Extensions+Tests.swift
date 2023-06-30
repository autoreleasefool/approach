import Foundation
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
@testable import StatisticsWidgetsLibrary
import XCTest

final class StatisticsWidgetsExtensionsTests: XCTestCase {
	func testTimelineStartDate() {
		let formatter = ISO8601DateFormatter()

		var calendar = Calendar(identifier: .iso8601)
		calendar.timeZone = .gmt
		let date = formatter.date(from: "2023-06-30T17:26:17Z")!

		XCTAssertNil(StatisticsWidget.Configuration.Timeline.allTime.startDate(relativeTo: date, in: calendar))
		XCTAssertEqual(
			StatisticsWidget.Configuration.Timeline.past1Month.startDate(relativeTo: date, in: calendar),
			formatter.date(from: "2023-05-30T00:00:00Z")!
		)
		XCTAssertEqual(
			StatisticsWidget.Configuration.Timeline.past3Months.startDate(relativeTo: date, in: calendar),
			formatter.date(from: "2023-03-30T00:00:00Z")!
		)
		XCTAssertEqual(
			StatisticsWidget.Configuration.Timeline.past6Months.startDate(relativeTo: date, in: calendar),
			formatter.date(from: "2022-12-30T00:00:00Z")!
		)
		XCTAssertEqual(
			StatisticsWidget.Configuration.Timeline.pastYear.startDate(relativeTo: date, in: calendar),
			formatter.date(from: "2022-06-30T00:00:00Z")!
		)
	}
}
