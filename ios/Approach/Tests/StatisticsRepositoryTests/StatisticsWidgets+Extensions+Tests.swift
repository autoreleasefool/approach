import Foundation
import ModelsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
@testable import StatisticsWidgetsLibrary
import Testing

@Suite("StatisticsWidgetsExtensions")
struct StatisticsWidgetsExtensionsTests {

	@Suite("Timeline")
	struct TimelineTests {

		@Test("startDate is correct")
		func startDateIsCorrect() throws{
			let formatter = ISO8601DateFormatter()

			var calendar = Calendar(identifier: .iso8601)
			calendar.timeZone = .gmt
			let date = formatter.date(from: "2023-06-30T17:26:17Z")!

			let allTimeDate = StatisticsWidget.Timeline.allTime.startDate(relativeTo: date, in: calendar)
			#expect(allTimeDate == nil)

			let past1MonthsExpected = StatisticsWidget.Timeline.past1Month
				.startDate(relativeTo: date, in: calendar)
			let past1MonthsDate = #require(formatter.date(from: "2023-05-30T00:00:00Z"))
			#expect(past1MonthsExpected == past1MonthsDate)

			let past3MonthsExpected = StatisticsWidget.Timeline.past3Months
				.startDate(relativeTo: date, in: calendar)
			let past3MonthsDate = #require(formatter.date(from: "2023-03-30T00:00:00Z"))
			#expect(past3MonthsExpected == past3MonthsDate)

			let past6MonthsExpected = StatisticsWidget.Timeline.past6Months
				.startDate(relativeTo: date, in: calendar)
			let past6MonthsDate = #require(formatter.date(from: "2022-12-30T00:00:00Z"))
			#expect(past6MonthsExpected == past6MonthsDate)

			let pastYearExpected = StatisticsWidget.Timeline.pastYear
				.startDate(relativeTo: date, in: calendar)
			let pastYearDate = #require(formatter.date(from: "2022-06-30T00:00:00Z"))
			#expect(pastYearExpected == pastYearDate)
		}
	}
}
