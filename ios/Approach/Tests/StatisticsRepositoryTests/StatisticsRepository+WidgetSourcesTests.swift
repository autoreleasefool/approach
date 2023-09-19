import DatabaseServiceInterface
import Dependencies
@testable import ModelsLibrary
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
@testable import StatisticsWidgetsLibrary
import TestDatabaseUtilitiesLibrary
import XCTest

@MainActor
final class StatisticsRepositoryWidgetSourcesTests: XCTestCase {
	@Dependency(\.statistics) var statistics

	func testLoadsWidgetSources_ForBowler() async throws {
		let db = try generatePopulatedDatabase()
		let source: StatisticsWidget.Source = .bowler(UUID(0))

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadWidgetSources(source)
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: nil
		))
	}

	func testLoadsWidgetSources_ForLeague() async throws {
		let db = try generatePopulatedDatabase()
		let source: StatisticsWidget.Source = .league(UUID(0))

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadWidgetSources(source)
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: .init(id: UUID(0), name: "Majors, 2022-23")
		))
	}

	// MARK: Load Default Sources

	func testLoadsDefaultWidgetSources_WithOneBowler_ReturnsBowler() async throws {
		let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let db = try initializeDatabase(withBowlers: .custom([bowler1]))

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadDefaultWidgetSources()
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: nil
		))
	}

	func testLoadsDefaultWidgetSources_WithNoBowlers_ReturnsNil() async throws {
		let db = try initializeDatabase(withBowlers: .zero)

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadDefaultWidgetSources()
		}

		XCTAssertNil(sources)
	}

	func testLoadsDefaultWidgetSources_WithTwoBowlers_ReturnsNil() async throws {
		let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let bowler2 = Bowler.Database.mock(id: UUID(1), name: "Sarah")
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadDefaultWidgetSources()
		}

		XCTAssertNil(sources)
	}

	func testLoadsDefaultWidgetSources_WithOpponent_ReturnsBowler() async throws {
		let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let bowler2 = Bowler.Database.mock(id: UUID(1), name: "Sarah", kind: .opponent)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		let sources = try await withDependencies {
			$0.database.reader = { db }
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.loadDefaultWidgetSources()
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: nil
		))
	}
}
