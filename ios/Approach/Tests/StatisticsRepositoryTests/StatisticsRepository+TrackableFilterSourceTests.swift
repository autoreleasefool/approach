import DatabaseServiceInterface
import Dependencies
import ModelsLibrary
import PreferenceServiceInterface
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import StringsLibrary
import TestDatabaseUtilitiesLibrary
import XCTest

@MainActor
final class TrackableFilterSourceTests: XCTestCase {
	@Dependency(\.statistics) var statistics

	// MARK: Empty

	func testBowler_WithEmptyDatabase_ReturnsNoValues() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let db = try initializeDatabase(withBowlers: .custom([bowler]))

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .bowler(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "0"),
			.init(title: Strings.Statistics.Title.headPins, value: "0"),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "0"),
		])
	}

	func testLeague_WithEmptyDatabase_ReturnsNoValues() async throws {
		let league = League.Database.mock(id: UUID(0), name: "Majors")
		let db = try initializeDatabase(withLeagues: .custom([league]))

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .league(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "0"),
			.init(title: Strings.Statistics.Title.headPins, value: "0"),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "0"),
		])
	}

	func testSeries_WithEmptyDatabase_ReturnsNoValues() async throws {
		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))
		let db = try initializeDatabase(withSeries: .custom([series]))

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .series(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "0"),
			.init(title: Strings.Statistics.Title.headPins, value: "0"),
		])
	}

	func testGame_WithEmptyDatabase_ReturnsNoValues() async throws {
		let game = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeDatabase(withGames: .custom([game]))

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .game(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.headPins, value: "0"),
		])
	}

	// MARK: Populated, no filters

	func testBowler_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .bowler(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "269"),
			.init(title: Strings.Statistics.Title.headPins, value: "183"),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "626"),
		])
	}

	func testLeague_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .league(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "269"),
			.init(title: Strings.Statistics.Title.headPins, value: "96"),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "0"),
		])
	}

	func testSeries_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .series(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "269"),
			.init(title: Strings.Statistics.Title.headPins, value: "3"),
		])
	}

	func testGame_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .game(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.headPins, value: "1"),
		])
	}

	// MARK: Populated, all filters

	func testBowler_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
				source: .bowler(UUID(0)),
				leagueFilter: .init(recurrence: .repeating),
				seriesFilter: .init(
					startDate: Date(timeIntervalSince1970: 1662512400),
					endDate: Date(timeIntervalSince1970: 1672189200), // 16 weeks
					alley: UUID(1)
				),
				gameFilter: .init(lanes: .lanes([UUID(12)]), opponent: UUID(0)),
				frameFilter: .init(bowlingBallsUsed: [UUID(1)])
			))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "192"),
			.init(title: Strings.Statistics.Title.headPins, value: "6"),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "0"),
		])
	}

	func testLeague_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
				source: .league(UUID(0)),
				seriesFilter: .init(
					startDate: Date(timeIntervalSince1970: 1662512400),
					endDate: Date(timeIntervalSince1970: 1672189200), // 16 weeks
					alley: UUID(1)
				),
				gameFilter: .init(lanes: .lanes([UUID(12)]), opponent: UUID(0)),
				frameFilter: .init(bowlingBallsUsed: [UUID(1)])
			))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "192"),
			.init(title: Strings.Statistics.Title.headPins, value: "6"),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "0"),
		])
	}

	func testSeries_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
				source: .series(UUID(0)),
				gameFilter: .init(lanes: .lanes([UUID(12)]), opponent: UUID(0)),
				frameFilter: .init(bowlingBallsUsed: [UUID(1)])
			))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "192"),
			.init(title: Strings.Statistics.Title.headPins, value: "1"),
		])
	}

	func testGame_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
				source: .game(UUID(0)),
				frameFilter: .init(bowlingBallsUsed: [UUID(1)])
			))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.headPins, value: "1"),
		])
	}
}
