import BowlersDataProvider
import BowlersDataProviderInterface
import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest

final class BowlersDataProviderTests: XCTestCase {
	func testFetchBowlers_ByRecentlyUsed_SortsByNameWhenNoRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let bowler1: Bowler = .mock(id: id0, name: "first")
		let bowler2: Bowler = .mock(id: id1, name: "second")
		let bowler3: Bowler = .mock(id: id2, name: "third")

		try await withDependencies {
			$0.persistenceService.fetchBowlers = { request in
				return [bowler1, bowler2, bowler3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .bowlers)
				return []
			}
		} operation: {
			let dataProvider: BowlersDataProvider = .liveValue

			let result = try await dataProvider.fetchBowlers(.init(filter: nil, ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [bowler1, bowler2, bowler3])
		}
	}

	func testFetchBowlers_ByRecentlyUsed_SortsByRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let bowler1: Bowler = .mock(id: id0, name: "first")
		let bowler2: Bowler = .mock(id: id1, name: "second")
		let bowler3: Bowler = .mock(id: id2, name: "third")

		try await withDependencies {
			$0.persistenceService.fetchBowlers = { request in
				return [bowler1, bowler2, bowler3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .bowlers)
				return [
					.init(id: id2, lastUsedAt: Date(timeIntervalSince1970: 0)),
					.init(id: id1, lastUsedAt: Date(timeIntervalSince1970: 1)),
				]
			}
		} operation: {
			let dataProvider: BowlersDataProvider = .liveValue

			let result = try await dataProvider.fetchBowlers(.init(filter: nil, ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [bowler3, bowler2, bowler1])
		}
	}

	func testFetchBowlers_ByName_SortsByName() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let bowler1: Bowler = .mock(id: id0, name: "first")
		let bowler2: Bowler = .mock(id: id1, name: "second")
		let bowler3: Bowler = .mock(id: id2, name: "third")

		try await withDependencies {
			$0.persistenceService.fetchBowlers = { request in
				return [bowler1, bowler2, bowler3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .bowlers)
				return [
					.init(id: id2, lastUsedAt: Date(timeIntervalSince1970: 0)),
					.init(id: id1, lastUsedAt: Date(timeIntervalSince1970: 1)),
				]
			}
		} operation: {
			let dataProvider: BowlersDataProvider = .liveValue

			let result = try await dataProvider.fetchBowlers(.init(filter: nil, ordering: .byName))

			XCTAssertEqual(result, [bowler1, bowler2, bowler3])
		}
	}
}
