import DatabaseServiceInterface
import Dependencies
import FeatureFlagsLibrary
import Foundation
@testable import ModelsLibrary
import PreferenceServiceInterface
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import StringsLibrary
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary
import UserDefaultsPackageServiceInterface

@Suite("TrackableFilterSource", .tags(.repository))
struct TrackableFilterSourceTests {

	@Suite("Bowler", .tags(.grdb, .dependencies))
	struct BowlerTests {
		@Dependency(StatisticsRepository.self) var statistics

		@Test(
			"With empty database returns no values",
			.tags(.unit),
			.disabled()
		)
		func withEmptyDatabase_returnsNoValues() async throws {
			let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")
			let db = try initializeApproachDatabase(withBowlers: .custom([bowler]))

			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(source: .bowler(UUID(0))))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "—", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			])
		}

		@Test(
			"With no filters, returns expected statistics",
			.tags(.unit),
			.disabled()
		)
		func withNoFilters_returnsExpectedStatistics() async throws {
			let db = try generatePopulatedDatabase()
			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.userDefaults.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(source: .bowler(UUID(0))))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "197.2", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "269", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "183", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "626", valueDescription: nil, highlightAsNew: false),
			])
		}

		@Test(
			"With filters, returns expected statistics",
			.tags(.unit),
			.disabled()
		)
		func withFilters_returnsExpectedStatistics() async throws {
			let db = try generatePopulatedDatabase()
			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.userDefaults.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(
					source: .bowler(UUID(0)),
					leagueFilter: .init(recurrence: .repeating),
					seriesFilter: .init(
						startDate: Date(timeIntervalSince1970: 1_662_512_400),
						endDate: Date(timeIntervalSince1970: 1_672_189_200), // 16 weeks
						alley: .alley(.init(id: UUID(1), name: "Skyview"))
					),
					gameFilter: .init(
						lanes: .lanes([.init(id: UUID(12), label: "1", position: .noWall)]),
						opponent: .init(id: UUID(0), name: "Joseph")
					),
					frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))])
				))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "6", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			])
		}
	}

	@Suite("League", .tags(.grdb, .dependencies))
	struct LeagueTests {
		@Dependency(StatisticsRepository.self) var statistics

		@Test(
			"With empty database returns no values",
			.tags(.unit),
			.disabled()
		)
		func withEmptyDatabase_returnsNoValues() async throws {
			let league = League.Database.mock(id: UUID(0), name: "Majors")
			let db = try initializeApproachDatabase(withLeagues: .custom([league]))

			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(source: .league(UUID(0))))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "—", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			])
		}

		@Test(
			"With no filters, returns expected statistics",
			.tags(.unit),
			.disabled()
		)
		func withNoFilters_returnsExpectedStatistics() async throws {
			let db = try generatePopulatedDatabase()
			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.userDefaults.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(source: .league(UUID(0))))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "197.2", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "269", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "96", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			])
		}

		@Test("With filters, returns expected statistics", .tags(.unit), .disabled())
		func withFilters_returnsExpectedStatistics() async throws {
			let db = try generatePopulatedDatabase()
			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.userDefaults.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(
					source: .league(UUID(0)),
					seriesFilter: .init(
						startDate: Date(timeIntervalSince1970: 1_662_512_400),
						endDate: Date(timeIntervalSince1970: 1_672_189_200), // 16 weeks
						alley: .alley(.init(id: UUID(1), name: "Skyview"))
					),
					gameFilter: .init(
						lanes: .lanes([.init(id: UUID(12), label: "1", position: .noWall)]),
						opponent: .init(id: UUID(0), name: "Joseph")
					),
					frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))])
				))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "6", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			])
		}
	}

	@Suite("Series", .tags(.grdb, .dependencies))
	struct SeriesTests {
		@Dependency(StatisticsRepository.self) var statistics

		@Test("With empty database returns no values", .tags(.unit), .disabled())
		func withEmptyDatabase_returnsNoValues() async throws {
			let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))
			let db = try initializeApproachDatabase(withSeries: .custom([series]))

			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(source: .series(UUID(0))))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "—", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			])
		}

		@Test(
			"With no filters, returns expected statistics",
			.tags(.unit),
			.disabled()
		)
		func withNoFilters_returnsExpectedStatistics() async throws {
			let db = try generatePopulatedDatabase()
			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.userDefaults.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(source: .series(UUID(0))))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "197.2", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "269", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "3", valueDescription: nil, highlightAsNew: false),
			])
		}

		@Test("With filters, returns expected statistics", .tags(.unit), .disabled())
		func withFilters_returnsExpectedStatistics() async throws {
			let db = try generatePopulatedDatabase()
			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.userDefaults.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(
					source: .series(UUID(0)),
					gameFilter: .init(
						lanes: .lanes([.init(id: UUID(12), label: "1", position: .noWall)]),
						opponent: .init(id: UUID(0), name: "Joseph")
					),
					frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))])
				))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "1", valueDescription: nil, highlightAsNew: false),
			])
		}
	}

	@Suite("Games", .tags(.grdb, .dependencies))
	struct GamesTests {
		@Dependency(StatisticsRepository.self) var statistics

		@Test("With empty database returns no values", .tags(.unit), .disabled())
		func withEmptyDatabase_returnsNoValues() async throws {
			let game = Game.Database.mock(id: UUID(0), index: 0)
			let db = try initializeApproachDatabase(withGames: .custom([game]))

			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(source: .game(UUID(0))))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			])
		}

		@Test(
			"With no filters, returns expected statistics",
			.tags(.unit),
			.disabled()
		)
		func withNoFilters_returnsExpectedStatistics() async throws {
			let db = try generatePopulatedDatabase()
			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.userDefaults.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(source: .game(UUID(0))))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "1", valueDescription: nil, highlightAsNew: false),
			])
		}

		@Test("With filters, returns expected statistics", .tags(.unit), .disabled())
		func withFilters_returnsExpectedStatistics() async throws {
			let db = try generatePopulatedDatabase()
			let loadedStatistics = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
				$0.preferences.bool = { @Sendable _ in true }
				$0.userDefaults.bool = { @Sendable _ in true }
				$0.uuid = .constant(UUID(0))
				$0[StatisticsRepository.self] = .liveValue
			} operation: {
				try await statistics.load(for: .init(
					source: .game(UUID(0)),
					frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))])
				))
			}

			#expect(loadedStatistics.flatMap { $0.entries } == [
				.init(title: Strings.Statistics.Title.headPins, description: nil, value: "1", valueDescription: nil, highlightAsNew: false),
			])
		}
	}
}
