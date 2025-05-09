import DatabaseServiceInterface
import Dependencies
import Foundation
import GRDB
@testable import ModelsLibrary
import PreferenceServiceInterface
@testable import StatisticsChartsLibrary
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary

@Suite("StatisticsRepository+LoadCharts", .tags(.repository, .grdb, .dependencies))
struct StatisticsRepositoryLoadChartsTests {
	@Dependency(StatisticsRepository.self) var statistics

	// MARK: - Empty

	@Test("Load charts for bowler with empty database returns no values", .tags(.unit))
	func loadCharts_forBowler_withEmptyDatabase_returnsNoValues() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let db = try initializeApproachDatabase(withBowlers: .custom([bowler]))
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

	@Test("Load charts for league with empty database returns no values", .tags(.unit))
	func loadCharts_forLeague_withEmptyDatabase_returnsNoValues() async throws {
		let league = League.Database.mock(id: UUID(0), name: "Majors")
		let db = try initializeApproachDatabase(withLeagues: .custom([league]))
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

	@Test("Load charts for series with empty database returns no values", .tags(.unit))
	func loadCharts_forSeries_withEmptyDatabase_returnsNoValues() async throws {
		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))
		let db = try initializeApproachDatabase(withSeries: .custom([series]))
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

	@Test("Load charts for game with empty database returns no values", .tags(.unit))
	func loadCharts_forGame_withEmptyDatabase_returnsNoValues() async throws {
		let game = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeApproachDatabase(withGames: .custom([game]))
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

	@Test(
		"Load charts for bowler with populated database returns values",
		.disabled(),
		.tags(.unit)
	)
	func loadCharts_forBowler_withPopulatedDatabase_returnsValues() async throws {
		let db = try generatePopulatedDatabase()
		let expectedResults: [(first: Entry?, last: Entry?)] = [
			(
				.averaging(.init(id: UUID(0), value: .init(197.25), xAxis: .date(Date(timeIntervalSince1970: 1_665_035_280)))),
				.averaging(.init(id: UUID(19), value: .init(197.25), xAxis: .date(Date(timeIntervalSince1970: 1_712_970_000))))
			),
			(
				.counting(.init(id: UUID(0), value: .init(269), xAxis: .date(Date(timeIntervalSince1970: 1_665_035_280), 2_522_880.0))),
				.counting(.init(id: UUID(19), value: .init(269), xAxis: .date(Date(timeIntervalSince1970: 1_712_970_000), 2_522_880.0)))
			),
			(
				.counting(.init(id: UUID(0), value: .init(15), xAxis: .date(Date(timeIntervalSince1970: 1_665_035_280), 2_522_880.0))),
				.counting(.init(id: UUID(19), value: .init(183), xAxis: .date(Date(timeIntervalSince1970: 1_712_970_000), 2_522_880.0)))
			),
			(
				.counting(.init(id: UUID(0), value: .init(0), xAxis: .date(Date(timeIntervalSince1970: 1_665_035_280), 2_522_880.0))),
				.counting(.init(id: UUID(19), value: .init(626), xAxis: .date(Date(timeIntervalSince1970: 1_712_970_000), 2_522_880.0)))
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
		sourceLocation: SourceLocation = #_sourceLocation
	) async throws {
		let entries: (first: Entry?, last: Entry?) = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0.preferences.bool = { @Sendable _ in true }
			$0.uuid = .incrementing
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			if let counting = statistic as? CountingStatistic.Type {
				let data = try await statistics.chart(statistic: counting, filter: filter)
				guard let first = data.countingEntries?.first,
							let last = data.countingEntries?.last
				else { return (nil, nil) }
				return (.counting(first), .counting(last))
			} else if let highest = statistic as? HighestOfStatistic.Type {
				let data = try await statistics.chart(statistic: highest, filter: filter)
				guard let first = data.countingEntries?.first,
							let last = data.countingEntries?.last
				else { return (nil, nil) }
				return (.counting(first), .counting(last))
			} else if let averaging = statistic as? AveragingStatistic.Type {
				let data = try await statistics.chart(statistic: averaging, filter: filter)
				guard let first = data.averagingEntries?.first,
							let last = data.averagingEntries?.last
				else { return (nil, nil) }
				return (.averaging(first), .averaging(last))
			} else {
				return (nil, nil)
			}
		}

		#expect(entries.first == expectedEntries.first, sourceLocation: sourceLocation)
		#expect(entries.last == expectedEntries.last, sourceLocation: sourceLocation)
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
