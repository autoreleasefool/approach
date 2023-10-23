import DatabaseServiceInterface
import Dependencies
import FeatureFlagsServiceInterface
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
			$0.featureFlags.isEnabled = { _ in true }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .bowler(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "—", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "0", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", highlightAsNew: false),
		])
	}

	func testLeague_WithEmptyDatabase_ReturnsNoValues() async throws {
		let league = League.Database.mock(id: UUID(0), name: "Majors")
		let db = try initializeDatabase(withLeagues: .custom([league]))

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.featureFlags.isEnabled = { _ in true }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .league(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "—", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "0", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", highlightAsNew: false),
		])
	}

	func testSeries_WithEmptyDatabase_ReturnsNoValues() async throws {
		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))
		let db = try initializeDatabase(withSeries: .custom([series]))

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.featureFlags.isEnabled = { _ in true }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .series(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "—", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "0", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", highlightAsNew: false),
		])
	}

	func testGame_WithEmptyDatabase_ReturnsNoValues() async throws {
		let game = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeDatabase(withGames: .custom([game]))

		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.featureFlags.isEnabled = { _ in true }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .game(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", highlightAsNew: false),
		])
	}

	// MARK: Populated, no filters

	func testBowler_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.featureFlags.isEnabled = { _ in true }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .bowler(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "197.2", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "269", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "183", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "626", highlightAsNew: false),
		])
	}

	func testLeague_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.featureFlags.isEnabled = { _ in true }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .league(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "197.2", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "269", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "96", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", highlightAsNew: false),
		])
	}

	func testSeries_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.featureFlags.isEnabled = { _ in true }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .series(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "197.2", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "269", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "3", highlightAsNew: false),
		])
	}

	func testGame_NoFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.featureFlags.isEnabled = { _ in true }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .game(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "1", highlightAsNew: false),
		])
	}

	// MARK: Populated, all filters

	func testBowler_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.featureFlags.isEnabled = { _ in true }
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
				frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))])
			))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "192", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "192", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "6", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", highlightAsNew: false),
		])
	}

	func testLeague_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.featureFlags.isEnabled = { _ in true }
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
				frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))])
			))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "192", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "192", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "6", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", highlightAsNew: false),
		])
	}

	func testSeries_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.featureFlags.isEnabled = { _ in true }
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
				frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))])
			))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "192", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "192", highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "1", highlightAsNew: false),
		])
	}

	func testGame_WithFilters_ReturnsStatistics() async throws {
		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0.database.reader = { db }
			$0.featureFlags.isEnabled = { _ in true }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .constant(UUID(0))
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
				source: .game(UUID(0)),
				frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))])
			))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "1", highlightAsNew: false),
		])
	}
}
