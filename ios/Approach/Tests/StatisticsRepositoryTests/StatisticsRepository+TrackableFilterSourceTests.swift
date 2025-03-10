import DatabaseServiceInterface
import Dependencies
import FeatureFlagsLibrary
@testable import ModelsLibrary
import PreferenceServiceInterface
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import StringsLibrary
import TestDatabaseUtilitiesLibrary
import UserDefaultsPackageServiceInterface
import XCTest

final class TrackableFilterSourceTests: XCTestCase {
	@Dependency(StatisticsRepository.self) var statistics

	// MARK: Empty

	func testBowler_WithEmptyDatabase_ReturnsNoValues() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let db = try initializeApproachDatabase(withBowlers: .custom([bowler]))

		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .bowler(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "—", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
		])
	}

	func testLeague_WithEmptyDatabase_ReturnsNoValues() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let league = League.Database.mock(id: UUID(0), name: "Majors")
		let db = try initializeApproachDatabase(withLeagues: .custom([league]))

		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .league(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "—", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
		])
	}

	func testSeries_WithEmptyDatabase_ReturnsNoValues() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))
		let db = try initializeApproachDatabase(withSeries: .custom([series]))

		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .series(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "—", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
		])
	}

	func testGame_WithEmptyDatabase_ReturnsNoValues() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let game = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeApproachDatabase(withGames: .custom([game]))

		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .game(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
		])
	}

	// MARK: Populated, no filters

	func testBowler_NoFilters_ReturnsStatistics() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.userDefaults.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .bowler(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "197.2", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "269", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "183", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "626", valueDescription: nil, highlightAsNew: false),
		])
	}

	func testLeague_NoFilters_ReturnsStatistics() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.userDefaults.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .league(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "197.2", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "269", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "96", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
		])
	}

	func testSeries_NoFilters_ReturnsStatistics() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.userDefaults.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .series(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "197.2", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "269", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "3", valueDescription: nil, highlightAsNew: false),
		])
	}

	func testGame_NoFilters_ReturnsStatistics() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.userDefaults.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(source: .game(UUID(0))))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "1", valueDescription: nil, highlightAsNew: false),
		])
	}

	// MARK: Populated, all filters

	func testBowler_WithFilters_ReturnsStatistics() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.userDefaults.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
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

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "6", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
		])
	}

	func testLeague_WithFilters_ReturnsStatistics() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.userDefaults.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
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

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "6", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSeriesOf3, description: nil, value: "0", valueDescription: nil, highlightAsNew: false),
		])
	}

	func testSeries_WithFilters_ReturnsStatistics() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.userDefaults.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
				source: .series(UUID(0)),
				gameFilter: .init(
					lanes: .lanes([.init(id: UUID(12), label: "1", position: .noWall)]),
					opponent: .init(id: UUID(0), name: "Joseph")
				),
				frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))])
			))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.gameAverage, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.highSingle, description: nil, value: "192", valueDescription: nil, highlightAsNew: false),
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "1", valueDescription: nil, highlightAsNew: false),
		])
	}

	func testGame_WithFilters_ReturnsStatistics() async throws {
		try XCTSkipIf(true, "Not yet implemented")

		let db = try generatePopulatedDatabase()
		let statistics = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[FeatureFlagsService.self].isEnabled = { @Sendable _ in true }
			$0.preferences.bool = { @Sendable _ in true }
			$0.userDefaults.bool = { @Sendable _ in true }
			$0.uuid = .constant(UUID(0))
			$0[StatisticsRepository.self] = .liveValue
		} operation: {
			try await self.statistics.load(for: .init(
				source: .game(UUID(0)),
				frameFilter: .init(bowlingBallsUsed: [.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0)))])
			))
		}

		XCTAssertEqual(statistics.flatMap { $0.entries }, [
			.init(title: Strings.Statistics.Title.headPins, description: nil, value: "1", valueDescription: nil, highlightAsNew: false),
		])
	}
}
