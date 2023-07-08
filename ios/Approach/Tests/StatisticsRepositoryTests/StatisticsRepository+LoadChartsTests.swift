import DatabaseServiceInterface
import Dependencies
import GRDB
@testable import ModelsLibrary
import PreferenceServiceInterface
@testable import StatisticsChartsLibrary
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import TestDatabaseUtilitiesLibrary
import XCTest

@MainActor
final class StatisticsRepositoryLoadChartsTests: XCTestCase {
	@Dependency(\.statistics) var statistics

	// MARK: - Empty

	func testBowler_WithEmptyDatabase_ReturnsNoValues() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let db = try initializeDatabase(withBowlers: .custom([bowler]))
		let expectedResults: [((first: Entry?, last: Entry?))] = [
			(nil, nil),
			(nil, nil),
			(nil, nil),
			(nil, nil),
		]

		for (statistic, expected) in zip(Statistics.allCases, expectedResults) {
			try await assertChart(
				forStatistic: statistic,
				withFilter: .init(source: .bowler(UUID(0))),
				withDb: db,
				equals: expected
			)
		}
	}

	func testLeague_WithEmptyDatabase_ReturnsNoValues() async throws {
		let league = League.Database.mock(id: UUID(0), name: "Majors")
		let db = try initializeDatabase(withLeagues: .custom([league]))
		let expectedResults: [((first: Entry?, last: Entry?))] = [
			(nil, nil),
			(nil, nil),
			(nil, nil),
			(nil, nil),
		]

		for (statistic, expected) in zip(Statistics.allCases, expectedResults) {
			try await assertChart(
				forStatistic: statistic,
				withFilter: .init(source: .league(UUID(0))),
				withDb: db,
				equals: expected
			)
		}
	}

	func testSeries_WithEmptyDatabase_ReturnsNoValues() async throws {
		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))
		let db = try initializeDatabase(withSeries: .custom([series]))
		let expectedResults: [((first: Entry?, last: Entry?))] = [
			(nil, nil),
			(nil, nil),
			(nil, nil),
			(nil, nil),
		]

		for (statistic, expected) in zip(Statistics.allCases, expectedResults) {
			try await assertChart(
				forStatistic: statistic,
				withFilter: .init(source: .series(UUID(0))),
				withDb: db,
				equals: expected
			)
		}
	}

	func testGame_WithEmptyDatabase_ReturnsNoValues() async throws {
		let game = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeDatabase(withGames: .custom([game]))
		let expectedResults: [((first: Entry?, last: Entry?))] = [
			(nil, nil),
			(nil, nil),
			(nil, nil),
			(nil, nil),
		]

		for (statistic, expected) in zip(Statistics.allCases, expectedResults) {
			try await assertChart(
				forStatistic: statistic,
				withFilter: .init(source: .game(UUID(0))),
				withDb: db,
				equals: expected
			)
		}
	}

	// MARK: - Populated, no filters

	func testBowler_NoFilters_AllTime_ReturnsValues() async throws {
		let db = try generatePopulatedDatabase()
		let expectedResults: [(first: Entry?, last: Entry?)] = [
			(
				.averaging(.init(id: UUID(0), value: .init(197.25), date: Date(timeIntervalSince1970: 1665035280))),
				.averaging(.init(id: UUID(19), value: .init(197.25), date: Date(timeIntervalSince1970: 1712970000)))
			),
			(
				.counting(.init(id: UUID(0), value: .init(269), date: Date(timeIntervalSince1970: 1665035280), timeRange: 2522880.0)),
				.counting(.init(id: UUID(19), value: .init(269), date: Date(timeIntervalSince1970: 1712970000), timeRange: 2522880.0))
			),
			(
				.counting(.init(id: UUID(0), value: .init(15), date: Date(timeIntervalSince1970: 1665035280), timeRange: 2522880.0)),
				.counting(.init(id: UUID(19), value: .init(183), date: Date(timeIntervalSince1970: 1712970000), timeRange: 2522880.0))
			),
			(
				.counting(.init(id: UUID(0), value: .init(0), date: Date(timeIntervalSince1970: 1665035280), timeRange: 2522880.0)),
				.counting(.init(id: UUID(19), value: .init(626), date: Date(timeIntervalSince1970: 1712970000), timeRange: 2522880.0))
			),
		]

		for (statistic, expected) in zip(Statistics.allCases, expectedResults) {
			try await assertChart(
				forStatistic: statistic,
				withFilter: .init(source: .bowler(UUID(0))),
				withDb: db,
				equals: expected
			)
		}
	}

	// MARK: - Assertion

	enum Entry: Equatable {
		case averaging(AveragingChart.Data.Entry?)
		case counting(CountingChart.Data.Entry?)
	}

	private func assertChart(
		forStatistic statistic: Statistic.Type,
		withFilter filter: TrackableFilter,
		withDb db: any DatabaseWriter,
		equals expectedEntries: (first: Entry?, last: Entry?),
		file: StaticString = #file,
		line: UInt = #line
	) async throws {
		let entries: (first: Entry?, last: Entry?) = try await withDependencies {
			$0.database.reader = { db }
			$0.preferences.getBool = { _ in true }
			$0.uuid = .incrementing
			$0.statistics = .liveValue
		} operation: {
			if let counting = statistic as? CountingStatistic.Type {
				let data = try await self.statistics.chart(statistic: counting, filter: filter)
				guard let first = data.countingEntries?.first,
							let last = data.countingEntries?.last
				else { return (nil, nil) }
				return (.counting(first), .counting(last))
			} else if let highest = statistic as? HighestOfStatistic.Type {
				let data = try await self.statistics.chart(statistic: highest, filter: filter)
				guard let first = data.countingEntries?.first,
							let last = data.countingEntries?.last
				else { return (nil, nil) }
				return (.counting(first), .counting(last))
			} else if let averaging = statistic as? AveragingStatistic.Type {
				let data = try await self.statistics.chart(statistic: averaging, filter: filter)
				guard let first = data.averagingEntries?.first,
							let last = data.averagingEntries?.last
				else { return (nil, nil) }
				return (.averaging(first), .averaging(last))
			} else {
				return (nil, nil)
			}
		}

		XCTAssertEqual(entries.first, expectedEntries.first, "First \(statistic)", file: file, line: line)
		XCTAssertEqual(entries.last, expectedEntries.last, "Last \(statistic)", file: file, line: line)
	}
}

extension Statistics.ChartContent {
	var countingEntries: [CountingChart.Data.Entry]? {
		switch self {
		case .counting(let data):
			return data.entries
		case .averaging, .percentage, .chartUnavailable, .dataMissing:
			return nil
		}
	}

	var averagingEntries: [AveragingChart.Data.Entry]? {
		switch self {
		case .averaging(let data):
			return data.entries
		case .counting, .percentage, .chartUnavailable, .dataMissing:
			return nil
		}
	}

	var percentageEntries: [PercentageChart.Data.Entry]? {
		switch self {
		case .percentage(let data):
			return data.entries
		case .averaging, .counting, .chartUnavailable, .dataMissing:
			return nil
		}
	}
}
