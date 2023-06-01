import DatabaseServiceInterface
import Dependencies
import ModelsLibrary
import PreferenceServiceInterface
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import TestDatabaseUtilitiesLibrary
import XCTest

@MainActor
final class StatisticsForBowlerTests: XCTestCase {
	@Dependency(\.statistics) var statistics

	func testHasNoGames_ReturnsNoValues() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let db = try initializeDatabase(withBowlers: .custom([bowler]))

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = {
				XCTAssertEqual($0, PreferenceKey.statisticsCountH2AsH.rawValue)
				return true
			}
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(forBowler: UUID(0))
		}

		XCTAssertEqual(statistics.map(\.value), [
			Statistics.HighSingle(highSingle: 0).value,
			Statistics.HeadPins(headPins: 0).value,
			Statistics.HighSeriesOf3(highSeries: 0).value,
		])
	}

	func testHasGames_ReturnsStatistics() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let league = League.Database.mock(id: UUID(0), name: "Majors")
		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_789))
		let game = Game.Database.mock(id: UUID(0), index: 0, score: 37)
		let frames = [
			Frame.Database.mock(index: 0, roll0: "011111"),
			Frame.Database.mock(index: 1, roll0: "000100", roll1: "011000", roll2: "000000"),
			Frame.Database.mock(index: 2, roll0: "000101", roll1: "000000", roll2: "000000"),
		]

		let db = try initializeDatabase(
			withBowlers: .custom([bowler]),
			withGear: .zero,
			withLeagues: .custom([league]),
			withSeries: .custom([series]),
			withGames: .custom([game]),
			withFrames: .custom(frames)
		)

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = {
				XCTAssertEqual($0, PreferenceKey.statisticsCountH2AsH.rawValue)
				return true
			}
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(forBowler: UUID(0))
		}

		XCTAssertEqual(statistics.map(\.value), [
			Statistics.HighSingle(highSingle: 37).value,
			Statistics.HeadPins(headPins: 2).value,
			Statistics.HighSeriesOf3(highSeries: 0).value,
		])
	}
}
