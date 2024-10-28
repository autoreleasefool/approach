import DatabaseServiceInterface
import Dependencies
@testable import ModelsLibrary
import PreferenceServiceInterface
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import TestDatabaseUtilitiesLibrary
import XCTest

final class StatisticsRepositorySourcesTests: XCTestCase {
	@Dependency(StatisticsRepository.self) var statistics

	// MARK: Load Default Sources

	func testLoadDefaultSources_WithNoBowlers_ReturnsNil() async throws {
		let db = try initializeApproachDatabase(withBowlers: .zero)

		let sources = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsRepository.self] = .liveValue
			$0.preferences.string = { @Sendable _ in nil }
		} operation: {
			try await self.statistics.loadDefaultSources()
		}

		XCTAssertNil(sources)
	}

	func testLoadDefaultSources_WithOneBowler_ReturnsSingleBowler() async throws {
		let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let db = try initializeApproachDatabase(withBowlers: .custom([bowler1]))

		let sources = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsRepository.self] = .liveValue
			$0.preferences.string = { @Sendable _ in nil }
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
		let db = try initializeApproachDatabase(withBowlers: .custom([bowler1, bowler2]))

		let sources = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsRepository.self] = .liveValue
			$0.preferences.string = { @Sendable _ in nil }
		} operation: {
			try await self.statistics.loadDefaultSources()
		}

		XCTAssertNil(sources)
	}

	func testLoadDefaultSources_WithOpponent_ReturnsBowler() async throws {
		let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let bowler2 = Bowler.Database.mock(id: UUID(1), name: "Sarah", kind: .opponent)
		let db = try initializeApproachDatabase(withBowlers: .custom([bowler1, bowler2]))

		let sources = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsRepository.self] = .liveValue
			$0.preferences.string = { @Sendable _ in nil }
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

	// MARK: Load Sources

	func testLoadsSources_ForBowler() async throws {
		let db = try generatePopulatedDatabase()
		let source: TrackableFilter.Source = .bowler(UUID(0))

		let sources = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsRepository.self] = .liveValue
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
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsRepository.self] = .liveValue
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
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.loadSources(source)
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: .init(id: UUID(0), name: "Majors, 2022-23"),
			series: .init(id: UUID(0), date: .init(timeIntervalSince1970: 1_662_512_400)),
			game: nil
		))
	}

	func testLoadsSources_ForGame() async throws {
		let db = try generatePopulatedDatabase()
		let source: TrackableFilter.Source = .game(UUID(0))

		let sources = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.loadSources(source)
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: .init(id: UUID(0), name: "Majors, 2022-23"),
			series: .init(id: UUID(0), date: .init(timeIntervalSince1970: 1_662_512_400)),
			game: .init(id: UUID(0), index: 0, score: 192)
		))
	}
}
