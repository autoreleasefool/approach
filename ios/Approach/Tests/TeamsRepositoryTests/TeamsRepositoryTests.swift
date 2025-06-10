import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import Foundation
import GRDB
@testable import ModelsLibrary
import RecentlyUsedServiceInterface
@testable import TeamsRepository
@testable import TeamsRepositoryInterface
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary
import TestUtilitiesPackageLibrary

@Suite("TeamsRepository", .tags(.repository))
struct TeamsRepositoryTests {

	// MARK: List

	@Suite("list", .tags(.dependencies, .grdb))
	struct ListTests {
		@Dependency(TeamsRepository.self) var teams

		private func setUpDatabase() throws -> any DatabaseWriter {
			let team1 = Team.Database.mock(id: UUID(0), name: "Argos")
			let team2 = Team.Database.mock(id: UUID(1), name: "Condors")
			let team3 = Team.Database.mock(id: UUID(2), name: "Bruins")

			let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
			let bowler2 = Bowler.Database.mock(id: UUID(1), name: "Sarah")
			let bowler3 = Bowler.Database.mock(id: UUID(2), name: "Jordan")
			let bowler4 = Bowler.Database.mock(id: UUID(3), name: "Audriana")

			let teamBowler1 = TeamBowler.Database(teamId: UUID(0), bowlerId: UUID(0), position: 0)
			let teamBowler2 = TeamBowler.Database(teamId: UUID(0), bowlerId: UUID(1), position: 1)
			let teamBowler3 = TeamBowler.Database(teamId: UUID(1), bowlerId: UUID(0), position: 0)
			let teamBowler4 = TeamBowler.Database(teamId: UUID(1), bowlerId: UUID(2), position: 2)
			let teamBowler5 = TeamBowler.Database(teamId: UUID(1), bowlerId: UUID(3), position: 1)
			let teamBowler6 = TeamBowler.Database(teamId: UUID(2), bowlerId: UUID(2), position: 0)
			let teamBowler7 = TeamBowler.Database(teamId: UUID(2), bowlerId: UUID(3), position: 1)

			return try initializeApproachDatabase(withBowlers: .custom([bowler1, bowler2, bowler3, bowler4]), withTeams: .custom([team1, team2, team3]), withTeamBowlers: .custom([teamBowler1, teamBowler2, teamBowler3, teamBowler4, teamBowler5, teamBowler6, teamBowler7]))
		}

		@Test("Lists all teams", .tags(.unit))
		func listsAllTeams() async throws {
			let db = try setUpDatabase()

			// Fetching the teams
			let teams = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[TeamsRepository.self] = .liveValue
			} operation: {
				teams.list(ordered: .byName)
			}

			var iterator = teams.makeAsyncIterator()
			let fetched = try await iterator.next()

			#expect(fetched == [
				.init(id: UUID(0), name: "Argos", bowlers: [.init(name: "Joseph"), .init(name: "Sarah")]),
				.init(id: UUID(2), name: "Bruins", bowlers: [.init(name: "Jordan"), .init(name: "Audriana")]),
				.init(id: UUID(1), name: "Condors", bowlers: [.init(name: "Joseph"), .init(name: "Audriana"), .init(name: "Jordan")]),
			])
		}

		@Test("Sorts by name", .tags(.unit))
		func sortsByName() async throws {
			let db = try setUpDatabase()

			// Fetching the teams
			let teams = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[TeamsRepository.self] = .liveValue
			} operation: {
				teams.list(ordered: .byName)
			}

			var iterator = teams.makeAsyncIterator()
			let fetched = try await iterator.next()

			#expect(fetched?.map(\.name) == ["Argos", "Bruins", "Condors"])
		}

		@Test("Sorts by recently used", .tags(.unit))
		func sortsByRecentlyUsed() async throws {
			let db = try setUpDatabase()

			// Given an ordering of ids
			let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
			recentContinuation.yield([UUID(0), UUID(1)])

			// Fetching the teams
			let teams = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[RecentlyUsedService.self].observeRecentlyUsedIds = { @Sendable _ in recentStream }
				$0[TeamsRepository.self] = .liveValue
			} operation: {
				teams.list(ordered: .byRecentlyUsed)
			}

			var iterator = teams.makeAsyncIterator()
			let fetched = try await iterator.next()

			#expect(fetched?.map(\.name) == ["Argos", "Condors", "Bruins"])
		}

		@Test("Sorts bowlers by position", .tags(.unit))
		func sortsBowlersByPosition() async throws {
			let db = try setUpDatabase()

			// Fetching the teams
			let teams = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[TeamsRepository.self] = .liveValue
			} operation: {
				teams.list(ordered: .byName)
			}

			var iterator = teams.makeAsyncIterator()
			let fetched = try await iterator.next()

			#expect(fetched?.map(\.bowlers) == [
				[.init(name: "Joseph"), .init(name: "Sarah")],
				[.init(name: "Jordan"), .init(name: "Audriana")],
				[.init(name: "Joseph"), .init(name: "Audriana"), .init(name: "Jordan")],
			])
		}
	}
}
