import DatabaseServiceInterface
import Dependencies
import GRDB
@testable import ModelsLibrary
import PreferenceServiceInterface
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import TestDatabaseUtilitiesLibrary
import XCTest

@MainActor
final class StatisticsRepositoryLoadChartsTests: XCTestCase {
	@Dependency(\.statistics) var statistics

	let graphableStatistics: [any GraphableStatistic.Type] = [
		Statistics.HighSingle.self,
		Statistics.HeadPins.self,
		Statistics.HighSeriesOf3.self,
	]

	// MARK: - Empty

	func testBowler_WithEmptyDatabase_ReturnsNoValues() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let db = try initializeDatabase(withBowlers: .custom([bowler]))
		let expectedResults: [(first: ChartEntry?, last: ChartEntry?)] = [
			(nil, nil),
			(nil, nil),
			(nil, nil),
		]

		for (graphable, expected) in zip(graphableStatistics, expectedResults) {
			try await assertChart(
				forStatistic: graphable,
				withFilter: .init(source: .bowler(UUID(0))),
				withDb: db,
				equals: expected
			)
		}
	}

	func testLeague_WithEmptyDatabase_ReturnsNoValues() async throws {
		let league = League.Database.mock(id: UUID(0), name: "Majors")
		let db = try initializeDatabase(withLeagues: .custom([league]))
		let expectedResults: [(first: ChartEntry?, last: ChartEntry?)] = [
			(nil, nil),
			(nil, nil),
			(nil, nil),
		]

		for (graphable, expected) in zip(graphableStatistics, expectedResults) {
			try await assertChart(
				forStatistic: graphable,
				withFilter: .init(source: .league(UUID(0))),
				withDb: db,
				equals: expected
			)
		}
	}

	func testSeries_WithEmptyDatabase_ReturnsNoValues() async throws {
		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))
		let db = try initializeDatabase(withSeries: .custom([series]))
		let expectedResults: [(first: ChartEntry?, last: ChartEntry?)] = [
			(nil, nil),
			(nil, nil),
			(nil, nil),
		]

		for (graphable, expected) in zip(graphableStatistics, expectedResults) {
			try await assertChart(
				forStatistic: graphable,
				withFilter: .init(source: .series(UUID(0))),
				withDb: db,
				equals: expected
			)
		}
	}

	func testGame_WithEmptyDatabase_ReturnsNoValues() async throws {
		let game = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeDatabase(withGames: .custom([game]))
		let expectedResults: [(first: ChartEntry?, last: ChartEntry?)] = [
			(nil, nil),
			(nil, nil),
			(nil, nil),
		]

		for (graphable, expected) in zip(graphableStatistics, expectedResults) {
			try await assertChart(
				forStatistic: graphable,
				withFilter: .init(source: .game(UUID(0))),
				withDb: db,
				equals: expected
			)
		}
	}

	// MARK: - Populated, no filters

	func testBowler_NoFilters_AllTime_ReturnsValues() async throws {
		let db = try generatePopulatedDatabase()
		let expectedResults: [(first: ChartEntry?, last: ChartEntry?)] = [
			(.init(id: UUID(0), value: .init(269), date: Date(timeIntervalSince1970: 1664530704)), .init(id: UUID(24), value: .init(269), date: Date(timeIntervalSince1970: 1712970000))),
			(.init(id: UUID(0), value: .init(12), date: Date(timeIntervalSince1970: 1664530704)), .init(id: UUID(24), value: .init(183), date: Date(timeIntervalSince1970: 1712970000))),
			(.init(id: UUID(0), value: .init(0), date: Date(timeIntervalSince1970: 1664530704)), .init(id: UUID(24), value: .init(626), date: Date(timeIntervalSince1970: 1712970000))),
		]

		for (graphable, expected) in zip(graphableStatistics, expectedResults) {
			try await assertChart(
				forStatistic: graphable,
				withFilter: .init(source: .bowler(UUID(0))),
				withDb: db,
				equals: expected
			)
		}
	}

	// MARK: - Assertion

	private func assertChart(
		forStatistic statistic: any GraphableStatistic.Type,
		withFilter filter: TrackableFilter,
		withDb db: any DatabaseWriter,
		equals expectedEntries: (first: ChartEntry?, last: ChartEntry?),
		file: StaticString = #file,
		line: UInt = #line
	) async throws {
		let entries = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .incrementing
			$0.statistics = .liveValue
		} operation: {
			try await self.statistics.chart(statistic: statistic, filter: filter)
		}

		XCTAssertEqual(entries.first, expectedEntries.first, file: file, line: line)
		XCTAssertEqual(entries.last, expectedEntries.last, file: file, line: line)
	}
}
