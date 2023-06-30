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
		let source: StatisticsWidget.Configuration.Source = .bowler(UUID(0))

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
		let source: StatisticsWidget.Configuration.Source = .league(UUID(0))

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
}
