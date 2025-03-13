import DatabaseServiceInterface
import Dependencies
import Foundation
@testable import ModelsLibrary
import PreferenceServiceInterface
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary

@Suite("StatisticsRepository+Sources", .tags(.repository))
struct StatisticsRepositorySourcesTests {

	// MARK: Load Default Sources

	@Suite("loadDefaultSources", .tags(.grdb, .dependencies))
	struct LoadDefaultSourcesTests {
		@Dependency(StatisticsRepository.self) var statistics

		@Test("Returns nil with no bowlers", .tags(.unit))
		func returnsNil_withNoBowlers() async throws {
			let db = try initializeApproachDatabase(withBowlers: .zero)

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
				$0.preferences.string = { @Sendable _ in nil }
			} operation: {
				try await statistics.loadDefaultSources()
			}

			#expect(sources == nil)
		}

		@Test("Returns single bowler with one bowler", .tags(.unit))
		func returnsSingleBowler_withOneBowler() async throws {
			let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
			let db = try initializeApproachDatabase(withBowlers: .custom([bowler1]))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
				$0.preferences.string = { @Sendable _ in nil }
			} operation: {
				try await statistics.loadDefaultSources()
			}

			#expect(sources == .init(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: nil,
				series: nil,
				game: nil
			))
		}

		@Test("Returns nil with two bowlers", .tags(.unit))
		func returnsNil_withTwoBowlers() async throws {
			let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
			let bowler2 = Bowler.Database.mock(id: UUID(1), name: "Sarah")
			let db = try initializeApproachDatabase(withBowlers: .custom([bowler1, bowler2]))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
				$0.preferences.string = { @Sendable _ in nil }
			} operation: {
				try await statistics.loadDefaultSources()
			}

			#expect(sources == nil)
		}

		@Test("Returns bowler when one is an opponent", .tags(.unit))
		func returnsBowler_whenOneIsAnOpponent() async throws {
			let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
			let bowler2 = Bowler.Database.mock(id: UUID(1), name: "Sarah", kind: .opponent)
			let db = try initializeApproachDatabase(withBowlers: .custom([bowler1, bowler2]))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
				$0.preferences.string = { @Sendable _ in nil }
			} operation: {
				try await statistics.loadDefaultSources()
			}

			#expect(sources == .init(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: nil,
				series: nil,
				game: nil
			))
		}
	}

	// MARK: Load Sources

	@Suite("loadSources", .tags(.grdb, .dependencies))
	struct LoadSourcesTests {
		@Dependency(StatisticsRepository.self) var statistics

		@Test("Loads sources for bowler", .tags(.unit))
		func loadSources_forBowler() async throws {
			let db = try generatePopulatedDatabase()
			let source: TrackableFilter.Source = .bowler(UUID(0))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.loadSources(source)
			}

			#expect(sources == .init(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: nil,
				series: nil,
				game: nil
			))
		}

		@Test("Loads sources for league", .tags(.unit))
		func loadsSources_forLeague() async throws {
			let db = try generatePopulatedDatabase()
			let source: TrackableFilter.Source = .league(UUID(0))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.loadSources(source)
			}

			#expect(sources == .init(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: .init(id: UUID(0), name: "Majors, 2022-23"),
				series: nil,
				game: nil
			))
		}

		@Test("Loads sources for series", .tags(.unit))
		func loadsSources_forSeries() async throws {
			let db = try generatePopulatedDatabase()
			let source: TrackableFilter.Source = .series(UUID(0))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.loadSources(source)
			}

			#expect(sources == .init(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: .init(id: UUID(0), name: "Majors, 2022-23"),
				series: .init(id: UUID(0), date: .init(timeIntervalSince1970: 1_662_512_400)),
				game: nil
			))
		}

		@Test("Loads sources for game", .tags(.unit))
		func loadsSources_forGame() async throws {
			let db = try generatePopulatedDatabase()
			let source: TrackableFilter.Source = .game(UUID(0))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.loadSources(source)
			}

			#expect(sources == .init(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: .init(id: UUID(0), name: "Majors, 2022-23"),
				series: .init(id: UUID(0), date: .init(timeIntervalSince1970: 1_662_512_400)),
				game: .init(id: UUID(0), index: 0, score: 192)
			))
		}
	}
}
