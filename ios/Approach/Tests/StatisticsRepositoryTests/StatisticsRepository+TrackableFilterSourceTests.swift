import DatabaseServiceInterface
import Dependencies
@testable import ModelsLibrary
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
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .bowler(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "0", isGraphable: true),
			.init(title: Strings.Statistics.Title.headPins, value: "0", isGraphable: true),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "0", isGraphable: true),
		])
	}

	func testLeague_WithEmptyDatabase_ReturnsNoValues() async throws {
		let league = League.Database.mock(id: UUID(0), name: "Majors")
		let db = try initializeDatabase(withLeagues: .custom([league]))

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .league(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "0", isGraphable: true),
			.init(title: Strings.Statistics.Title.headPins, value: "0", isGraphable: true),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "0", isGraphable: true),
		])
	}

	func testSeries_WithEmptyDatabase_ReturnsNoValues() async throws {
		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))
		let db = try initializeDatabase(withSeries: .custom([series]))

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .series(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "0", isGraphable: true),
			.init(title: Strings.Statistics.Title.headPins, value: "0", isGraphable: true),
		])
	}

	func testGame_WithEmptyDatabase_ReturnsNoValues() async throws {
		let game = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeDatabase(withGames: .custom([game]))

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .game(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.headPins, value: "0", isGraphable: true),
		])
	}

	// MARK: Populated, no filters

	func testBowler_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .bowler(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "269", isGraphable: true),
			.init(title: Strings.Statistics.Title.headPins, value: "183", isGraphable: true),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "626", isGraphable: true),
		])
	}

	func testLeague_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .league(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "269", isGraphable: true),
			.init(title: Strings.Statistics.Title.headPins, value: "96", isGraphable: true),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "0", isGraphable: true),
		])
	}

	func testSeries_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .series(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "269", isGraphable: true),
			.init(title: Strings.Statistics.Title.headPins, value: "3", isGraphable: true),
		])
	}

	func testGame_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .game(UUID(0))))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.headPins, value: "1", isGraphable: true),
		])
	}

	// MARK: Populated, all filters

	func testBowler_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
				source: .bowler(UUID(0)),
				leagueFilter: .init(recurrence: .repeating),
				seriesFilter: .init(
					startDate: Date(timeIntervalSince1970: 1662512400),
					endDate: Date(timeIntervalSince1970: 1672189200), // 16 weeks
					alley: .alley(.init(id: UUID(1), name: "Skyview"))
				),
				gameFilter: .init(
					lanes: .lanes([.init(id: UUID(12), label: "1", position: .noWall)]),
					opponent: .init(id: UUID(0), name: "Joseph")
				),
				frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue")])
			))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "192", isGraphable: true),
			.init(title: Strings.Statistics.Title.headPins, value: "6", isGraphable: true),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "0", isGraphable: true),
		])
	}

	func testLeague_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
				source: .league(UUID(0)),
				seriesFilter: .init(
					startDate: Date(timeIntervalSince1970: 1662512400),
					endDate: Date(timeIntervalSince1970: 1672189200), // 16 weeks
					alley: .alley(.init(id: UUID(1), name: "Skyview"))
				),
				gameFilter: .init(
					lanes: .lanes([.init(id: UUID(12), label: "1", position: .noWall)]),
					opponent: .init(id: UUID(0), name: "Joseph")
				),
				frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue")])
			))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "192", isGraphable: true),
			.init(title: Strings.Statistics.Title.headPins, value: "6", isGraphable: true),
			.init(title: Strings.Statistics.Title.highSeriesOf3, value: "0", isGraphable: true),
		])
	}

	func testSeries_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
				source: .series(UUID(0)),
				gameFilter: .init(
					lanes: .lanes([.init(id: UUID(12), label: "1", position: .noWall)]),
					opponent: .init(id: UUID(0), name: "Joseph")
				),
				frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue")])
			))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.highSingle, value: "192", isGraphable: true),
			.init(title: Strings.Statistics.Title.headPins, value: "1", isGraphable: true),
		])
	}

	func testGame_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
				source: .game(UUID(0)),
				frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue")])
			))
		}

		XCTAssertEqual(statistics.map(\.staticValue), [
			.init(title: Strings.Statistics.Title.headPins, value: "1", isGraphable: true),
		])
	}
}
