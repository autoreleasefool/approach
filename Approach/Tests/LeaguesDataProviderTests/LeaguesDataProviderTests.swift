import ComposableArchitecture
import Dependencies
import LeaguesDataProvider
import LeaguesDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest

final class LeaguesDataProviderTests: XCTestCase {
	func testFetchLeagues_ByRecentlyUsed_SortsByNameWhenNoRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

		let league1: League = .mock(bowler: id0, id: id1, name: "first")
		let league2: League = .mock(bowler: id0, id: id2, name: "second")
		let league3: League = .mock(bowler: id0, id: id3, name: "third")

		try await DependencyValues.withValues {
			$0.persistenceService.fetchLeagues = { request in
				XCTAssertEqual(request.ordering, .byName)
				return [league1, league2, league3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .leagues)
				return []
			}
		} operation: {
			let dataProvider: LeaguesDataProvider = .liveValue

			let result = try await dataProvider.fetchLeagues(.init(bowler: id0, ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [league1, league2, league3])
		}
	}

	func testFetchLeagues_ByRecentlyUsed_SortsByRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

		let league1: League = .mock(bowler: id0, id: id1, name: "first")
		let league2: League = .mock(bowler: id0, id: id2, name: "second")
		let league3: League = .mock(bowler: id0, id: id3, name: "third")

		try await DependencyValues.withValues {
			$0.persistenceService.fetchLeagues = { request in
				XCTAssertEqual(request.ordering, .byName)
				return [league1, league2, league3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .leagues)
				return [id3, id2]
			}
		} operation: {
			let dataProvider: LeaguesDataProvider = .liveValue

			let result = try await dataProvider.fetchLeagues(.init(bowler: id0, ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [league3, league2, league1])
		}
	}

	func testFetchLeagues_ByName_SortsByName() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

		let league1: League = .mock(bowler: id0, id: id1, name: "first")
		let league2: League = .mock(bowler: id0, id: id2, name: "second")
		let league3: League = .mock(bowler: id0, id: id3, name: "third")

		try await DependencyValues.withValues {
			$0.persistenceService.fetchLeagues = { request in
				XCTAssertEqual(request.ordering, .byName)
				return [league1, league2, league3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .leagues)
				return [id2, id1]
			}
		} operation: {
			let dataProvider: LeaguesDataProvider = .liveValue

			let result = try await dataProvider.fetchLeagues(.init(bowler: id0, ordering: .byName))

			XCTAssertEqual(result, [league1, league2, league3])
		}
	}
}
