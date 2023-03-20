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
		let bowlerId = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

		let league1: League = .mock(bowler: bowlerId, id: id1, name: "first")
		let league2: League = .mock(bowler: bowlerId, id: id2, name: "second")
		let league3: League = .mock(bowler: bowlerId, id: id3, name: "third")

		try await withDependencies {
			$0.persistenceService.fetchLeagues = { _ in
				return [league1, league2, league3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .leagues)
				return []
			}
		} operation: {
			let dataProvider: LeaguesDataProvider = .liveValue

			let result = try await dataProvider.fetchLeagues(.init(filter: nil, ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [league1, league2, league3])
		}
	}

	func testFetchLeagues_ByRecentlyUsed_SortsByRecentlyUsed() async throws {
		let bowlerId = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

		let league1: League = .mock(bowler: bowlerId, id: id1, name: "first")
		let league2: League = .mock(bowler: bowlerId, id: id2, name: "second")
		let league3: League = .mock(bowler: bowlerId, id: id3, name: "third")

		try await withDependencies {
			$0.persistenceService.fetchLeagues = { _ in
				return [league1, league2, league3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .leagues)
				return [
					.init(id: id3, lastUsedAt: Date(timeIntervalSince1970: 0)),
					.init(id: id2, lastUsedAt: Date(timeIntervalSince1970: 1)),
				]
			}
		} operation: {
			let dataProvider: LeaguesDataProvider = .liveValue

			let result = try await dataProvider.fetchLeagues(.init(filter: nil, ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [league3, league2, league1])
		}
	}

	func testFetchLeagues_ByName_SortsByName() async throws {
		let bowlerId = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

		let league1: League = .mock(bowler: bowlerId, id: id1, name: "first")
		let league2: League = .mock(bowler: bowlerId, id: id2, name: "second")
		let league3: League = .mock(bowler: bowlerId, id: id3, name: "third")

		try await withDependencies {
			$0.persistenceService.fetchLeagues = { _ in
				return [league1, league2, league3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .leagues)
				return [
					.init(id: id2, lastUsedAt: Date(timeIntervalSince1970: 0)),
					.init(id: id1, lastUsedAt: Date(timeIntervalSince1970: 1)),
				]
			}
		} operation: {
			let dataProvider: LeaguesDataProvider = .liveValue

			let result = try await dataProvider.fetchLeagues(.init(filter: nil, ordering: .byName))

			XCTAssertEqual(result, [league1, league2, league3])
		}
	}
}
