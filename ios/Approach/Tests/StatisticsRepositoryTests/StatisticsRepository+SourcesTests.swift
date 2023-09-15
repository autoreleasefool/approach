import DatabaseServiceInterface
import Dependencies
@testable import ModelsLibrary
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import TestDatabaseUtilitiesLibrary
import XCTest

@MainActor
final class StatisticsRepositorySourcesTests: XCTestCase {
	@Dependency(\.statistics) var statistics

	func testLoadDefaultSources_WithNoBowlers_ReturnsNil() async throws {
		let db = try initializeDatabase(withBowlers: .zero)

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadDefaultSources()
		}

		XCTAssertNil(sources)
	}

	func testLoadDefaultSources_WithOneBowler_ReturnsSingleBowler() async throws {
		let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let db = try initializeDatabase(withBowlers: .custom([bowler1]))

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadDefaultSources()
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: nil,
			series: nil,
			game: nil
		))
	}

	func testLoadDefaultSources_WithTwoBowlers_ReturnsNil() async throws {
		let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let bowler2 = Bowler.Database.mock(id: UUID(1), name: "Sarah")
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadDefaultSources()
		}

		XCTAssertNil(sources)
	}

	func testLoadsSources_ForBowler() async throws {
		let db = try generatePopulatedDatabase()
		let source: TrackableFilter.Source = .bowler(UUID(0))

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadSources(source)
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: nil,
			series: nil,
			game: nil
		))
	}

	func testLoadsSources_ForLeague() async throws {
		let db = try generatePopulatedDatabase()
		let source: TrackableFilter.Source = .league(UUID(0))

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadSources(source)
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: .init(id: UUID(0), name: "Majors, 2022-23"),
			series: nil,
			game: nil
		))
	}

	func testLoadsSources_ForSeries() async throws {
		let db = try generatePopulatedDatabase()
		let source: TrackableFilter.Source = .series(UUID(0))

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadSources(source)
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: .init(id: UUID(0), name: "Majors, 2022-23"),
			series: .init(id: UUID(0), date: .init(timeIntervalSince1970: 1662512400)),
			game: nil
		))
	}

	func testLoadsSources_ForGame() async throws {
		let db = try generatePopulatedDatabase()
		let source: TrackableFilter.Source = .game(UUID(0))

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadSources(source)
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: .init(id: UUID(0), name: "Majors, 2022-23"),
			series: .init(id: UUID(0), date: .init(timeIntervalSince1970: 1662512400)),
			game: .init(id: UUID(0), index: 0, score: 192)
		))
	}
}
