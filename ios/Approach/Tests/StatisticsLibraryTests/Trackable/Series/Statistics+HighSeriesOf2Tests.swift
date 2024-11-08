import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class HighSeriesOf2Tests: XCTestCase {
	func testAdjustBy_SeriesWith2Games_Adjusts() {
		let statistic = create(
			statistic: Statistics.HighSeriesOf2.self,
			adjustedBySeries: [
				Series.TrackableEntry(id: UUID(0), numberOfGames: 2, total: 222, date: Date()),
				Series.TrackableEntry(id: UUID(1), numberOfGames: 2, total: 456, date: Date()),
			]
		)
		AssertHighestOf(statistic, equals: 456)
	}

	func testAdjustBy_SeriesNotWith2Games_DoesNotAdjust() {
		let statistic = create(
			statistic: Statistics.HighSeriesOf2.self,
			adjustedBySeries: [
				Series.TrackableEntry(id: UUID(1), numberOfGames: 3, total: 333, date: Date()),
				Series.TrackableEntry(id: UUID(2), numberOfGames: 4, total: 444, date: Date()),
				Series.TrackableEntry(id: UUID(3), numberOfGames: 5, total: 555, date: Date()),
				Series.TrackableEntry(id: UUID(4), numberOfGames: 6, total: 666, date: Date()),
				Series.TrackableEntry(id: UUID(5), numberOfGames: 7, total: 777, date: Date()),
				Series.TrackableEntry(id: UUID(6), numberOfGames: 8, total: 888, date: Date()),
				Series.TrackableEntry(id: UUID(7), numberOfGames: 9, total: 999, date: Date()),
				Series.TrackableEntry(id: UUID(8), numberOfGames: 10, total: 1_010, date: Date()),
				Series.TrackableEntry(id: UUID(9), numberOfGames: 11, total: 1_111, date: Date()),
				Series.TrackableEntry(id: UUID(10), numberOfGames: 12, total: 1_212, date: Date()),
				Series.TrackableEntry(id: UUID(11), numberOfGames: 13, total: 1_313, date: Date()),
				Series.TrackableEntry(id: UUID(12), numberOfGames: 14, total: 1_414, date: Date()),
				Series.TrackableEntry(id: UUID(13), numberOfGames: 15, total: 1_515, date: Date()),
				Series.TrackableEntry(id: UUID(14), numberOfGames: 16, total: 1_616, date: Date()),
				Series.TrackableEntry(id: UUID(15), numberOfGames: 17, total: 1_717, date: Date()),
				Series.TrackableEntry(id: UUID(16), numberOfGames: 18, total: 1_818, date: Date()),
				Series.TrackableEntry(id: UUID(17), numberOfGames: 19, total: 1_919, date: Date()),
				Series.TrackableEntry(id: UUID(18), numberOfGames: 20, total: 2_020, date: Date()),
				Series.TrackableEntry(id: UUID(19), numberOfGames: 21, total: 2_121, date: Date()),
			]
		)

		AssertHighestOf(statistic, equals: 0)
	}

	func testAdjustByGame_DoesNothing() {
		let statistic = create(statistic: Statistics.HighSeriesOf2.self, adjustedByGames: Game.TrackableEntry.mocks)
		AssertHighestOf(statistic, equals: 0)
	}

	func testAdjustByFrame_DoesNothing() {
		let statistic = create(statistic: Statistics.HighSeriesOf2.self, adjustedByFrames: Frame.TrackableEntry.mocks)
		AssertHighestOf(statistic, equals: 0)
	}
}
