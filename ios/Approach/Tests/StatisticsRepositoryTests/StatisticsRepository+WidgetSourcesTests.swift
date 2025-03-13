import DatabaseServiceInterface
import Dependencies
import Foundation
@testable import ModelsLibrary
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
@testable import StatisticsWidgetsLibrary
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary

@Suite("StatisticsRepository+WidgetSources", .tags(.repository))
struct StatisticsRepositoryWidgetSourcesTests {

	@Suite("loadWidgetSources", .tags(.dependencies, .grdb))
	struct LoadWidgetSourcesTests {
		@Dependency(StatisticsRepository.self) var statistics

		@Test("loadsWidgetSources for Bowler", .tags(.unit))
		func loadsForBowler() async throws {
			let db = try generatePopulatedDatabase()
			let source: StatisticsWidget.Source = .bowler(UUID(0))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.loadWidgetSources(source)
			}

			#expect(sources == .init(bowler: .init(id: UUID(0), name: "Joseph"), league: nil))
		}

		@Test("loadsWidgetSources for League", .tags(.unit))
		func loadsForLeague() async throws {
			let db = try generatePopulatedDatabase()
			let source: StatisticsWidget.Source = .league(UUID(0))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.loadWidgetSources(source)
			}

			#expect(sources == .init(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: .init(id: UUID(0), name: "Majors, 2022-23")
			))
		}
	}

	// MARK: Load Default Sources

	@Suite("loadDefaultWidgets", .tags(.dependencies, .grdb))
	struct LoadDefaultWidgetsTests {
		@Dependency(StatisticsRepository.self) var statistics

		@Test("With one bowler, returns one bowler", .tags(.unit))
		func withOneBowler_returnsOneBowler() async throws {
			let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
			let db = try initializeApproachDatabase(withBowlers: .custom([bowler1]))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.loadDefaultWidgetSources()
			}

			let expectedSources = StatisticsWidget.Sources(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: nil
			)

			#expect(sources == expectedSources)
		}

		@Test("With no bowlers, returns nil", .tags(.unit))
		func withNoBowlers_returnsNil() async throws {
			let db = try initializeApproachDatabase(withBowlers: .zero)

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.loadDefaultWidgetSources()
			}

			#expect(sources == nil)
		}

		@Test("With two bowlers, returns nil", .tags(.unit))
		func withTwoBowlers_returnsNil() async throws {
			let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
			let bowler2 = Bowler.Database.mock(id: UUID(1), name: "Sarah")
			let db = try initializeApproachDatabase(withBowlers: .custom([bowler1, bowler2]))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.loadDefaultWidgetSources()
			}

			#expect(sources == nil)
		}

		@Test("With opponent, returns bowler", .tags(.unit))
		func withOpponent_returnsOneBowler() async throws {
			let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
			let bowler2 = Bowler.Database.mock(id: UUID(1), name: "Sarah", kind: .opponent)
			let db = try initializeApproachDatabase(withBowlers: .custom([bowler1, bowler2]))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.loadDefaultWidgetSources()
			}

			let expectedSources = StatisticsWidget.Sources(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: nil
			)

			#expect(sources == expectedSources)
		}
	}
}
