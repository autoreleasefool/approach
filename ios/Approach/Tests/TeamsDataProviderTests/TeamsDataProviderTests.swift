import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
@testable import TeamsDataProvider
@testable import TeamsDataProviderInterface
import XCTest

final class TeamsDataProviderTests: XCTestCase {
	func testFetchTeams_ByRecentlyUsed_SortsByNameWhenNoRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let team1: Team = .mock(id: id0, name: "first")
		let team2: Team = .mock(id: id1, name: "second")
		let team3: Team = .mock(id: id2, name: "third")

		try await DependencyValues.withValues {
			$0.persistenceService.fetchTeams = { request in
				XCTAssertEqual(request.ordering, .byRecentlyUsed)
				return [team1, team2, team3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .teams)
				return []
			}
		} operation: {
			let dataProvider: TeamsDataProvider = .liveValue

			let result = try await dataProvider.fetchTeams(.init(filter: nil, ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [team1, team2, team3])
		}
	}

	func testFetchTeams_ByRecentlyUsed_SortsByRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let date = Date(timeIntervalSince1970: 1672519204)

		let team1: Team = .mock(id: id0, name: "first")
		let team2: Team = .mock(id: id1, name: "second")
		let team3: Team = .mock(id: id2, name: "third")

		try await DependencyValues.withValues {
			$0.persistenceService.fetchTeams = { request in
				XCTAssertEqual(request.ordering, .byRecentlyUsed)
				return [team1, team2, team3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .teams)
				return [.init(id: id2, lastUsedAt: date), .init(id: id1, lastUsedAt: date)]
			}
		} operation: {
			let dataProvider: TeamsDataProvider = .liveValue

			let result = try await dataProvider.fetchTeams(.init(filter: nil, ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [team3, team2, team1])
		}
	}

	func testFetchTeams_ByName_SortsByName() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let date = Date(timeIntervalSince1970: 1672519204)

		let team1: Team = .mock(id: id0, name: "first")
		let team2: Team = .mock(id: id1, name: "second")
		let team3: Team = .mock(id: id2, name: "third")

		try await DependencyValues.withValues {
			$0.persistenceService.fetchTeams = { request in
				XCTAssertEqual(request.ordering, .byName)
				return [team1, team2, team3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .teams)
				return [.init(id: id2, lastUsedAt: date)]
			}
		} operation: {
			let dataProvider: TeamsDataProvider = .liveValue

			let result = try await dataProvider.fetchTeams(.init(filter: nil, ordering: .byName))

			XCTAssertEqual(result, [team1, team2, team3])
		}
	}

	func testObserveTeams_ByRecentlyUsed_SortsByNameWhenNoRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let team1: Team = .mock(id: id0, name: "first")
		let team2: Team = .mock(id: id1, name: "second")
		let team3: Team = .mock(id: id2, name: "third")

		let (teams, teamsContinuation) = AsyncThrowingStream<[Team], Error>.streamWithContinuation()
		let (recents, recentsContinuation) = AsyncStream<[RecentlyUsedService.Entry]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.observeTeams = { request in
				XCTAssertEqual(request.ordering, .byRecentlyUsed)
				return teams
			}
			$0.recentlyUsedService.observeRecentlyUsed = { category in
				XCTAssertEqual(category, .teams)
				return recents
			}
		} operation: {
			let dataProvider: TeamsDataProvider = .liveValue

			teamsContinuation.yield([team1, team2, team3])
			recentsContinuation.yield([])

			var observations = dataProvider.observeTeams(.init(filter: nil, ordering: .byRecentlyUsed)).makeAsyncIterator()
			let result = try await observations.next()

			XCTAssertEqual(result, [team1, team2, team3])
		}
	}

	func testObserveTeams_ByRecentlyUsed_SortsByRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let date = Date(timeIntervalSince1970: 1672519204)

		let team1: Team = .mock(id: id0, name: "first")
		let team2: Team = .mock(id: id1, name: "second")
		let team3: Team = .mock(id: id2, name: "third")

		let (teams, teamsContinuation) = AsyncThrowingStream<[Team], Error>.streamWithContinuation()
		let (recents, recentsContinuation) = AsyncStream<[RecentlyUsedService.Entry]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.observeTeams = { request in
				XCTAssertEqual(request.ordering, .byRecentlyUsed)
				return teams
			}
			$0.recentlyUsedService.observeRecentlyUsed = { category in
				XCTAssertEqual(category, .teams)
				return recents
			}
		} operation: {
			let dataProvider: TeamsDataProvider = .liveValue

			teamsContinuation.yield([team1, team2, team3])
			recentsContinuation.yield([.init(id: id2, lastUsedAt: date), .init(id: id1, lastUsedAt: date)])

			var observations = dataProvider.observeTeams(.init(filter: nil, ordering: .byRecentlyUsed)).makeAsyncIterator()
			let result = try await observations.next()

			XCTAssertEqual(result, [team3, team2, team1])
		}
	}

	func testObserveTeams_ByName_SortsByName() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let date = Date(timeIntervalSince1970: 1672519204)

		let team1: Team = .mock(id: id0, name: "first")
		let team2: Team = .mock(id: id1, name: "second")
		let team3: Team = .mock(id: id2, name: "third")

		let (teams, teamsContinuation) = AsyncThrowingStream<[Team], Error>.streamWithContinuation()
		let (recents, recentsContinuation) = AsyncStream<[RecentlyUsedService.Entry]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.observeTeams = { request in
				XCTAssertEqual(request.ordering, .byName)
				return teams
			}
			$0.recentlyUsedService.observeRecentlyUsed = { category in
				XCTAssertEqual(category, .teams)
				return recents
			}
		} operation: {
			let dataProvider: TeamsDataProvider = .liveValue

			teamsContinuation.yield([team1, team2, team3])
			recentsContinuation.yield([.init(id: id2, lastUsedAt: date)])

			var observations = dataProvider.observeTeams(.init(filter: nil, ordering: .byName)).makeAsyncIterator()
			let result = try await observations.next()

			XCTAssertEqual(result, [team1, team2, team3])
		}
	}
}
