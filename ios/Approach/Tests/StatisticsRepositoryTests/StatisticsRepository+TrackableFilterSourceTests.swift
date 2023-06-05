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

	func testBowler_WithNoGames_ReturnsNoValues() async throws {
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
}
