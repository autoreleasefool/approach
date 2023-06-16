import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class HighSeriesOf3Tests: XCTestCase {
	func testAdjustBy_SeriesWith3Games_Adjusts() {
		var statistic = Statistics.HighSeriesOf3()

		let seriesList = [
			Series.TrackableEntry(id: UUID(0), numberOfGames: 3, total: 123, date: Date()),
			Series.TrackableEntry(id: UUID(1), numberOfGames: 3, total: 456, date: Date()),
		]

		for series in seriesList {
			statistic.adjust(bySeries: series, configuration: .init())
		}

		XCTAssertEqual(statistic.value, "456")
	}

	func testAdjustBy_SeriesNotWith3Games_DoesNotAdjust() {
		var statistic = Statistics.HighSeriesOf3()

		let seriesList = [
			Series.TrackableEntry(id: UUID(0), numberOfGames: 2, total: 123, date: Date()),
			Series.TrackableEntry(id: UUID(1), numberOfGames: 4, total: 456, date: Date()),
		]

		for series in seriesList {
			statistic.adjust(bySeries: series, configuration: .init())
		}

		XCTAssertEqual(statistic.value, "0")
	}
}
