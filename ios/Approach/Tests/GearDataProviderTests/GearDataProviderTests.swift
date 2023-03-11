import ComposableArchitecture
import Dependencies
import GearDataProvider
import GearDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest

final class GearDataProviderTests: XCTestCase {
	func testFetchGear_ByRecentlyUsed_SortsByNameWhenNoRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let gear1: Gear = .mock(id: id0, name: "first")
		let gear2: Gear = .mock(id: id1, name: "second")
		let gear3: Gear = .mock(id: id2, name: "third")

		try await withDependencies {
			$0.persistenceService.fetchGear = { request in
				return [gear1, gear2, gear3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .gear)
				return []
			}
		} operation: {
			let dataProvider: GearDataProvider = .liveValue

			let result = try await dataProvider.fetchGear(.init(filter: nil, ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [gear1, gear2, gear3])
		}
	}

	func testFetchGear_ByRecentlyUsed_SortsByRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let gear1: Gear = .mock(id: id0, name: "first")
		let gear2: Gear = .mock(id: id1, name: "second")
		let gear3: Gear = .mock(id: id2, name: "third")

		try await withDependencies {
			$0.persistenceService.fetchGear = { request in
				return [gear1, gear2, gear3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .gear)
				return [
					.init(id: id2, lastUsedAt: Date(timeIntervalSince1970: 0)),
					.init(id: id1, lastUsedAt: Date(timeIntervalSince1970: 1)),
				]
			}
		} operation: {
			let dataProvider: GearDataProvider = .liveValue

			let result = try await dataProvider.fetchGear(.init(filter: nil, ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [gear3, gear2, gear1])
		}
	}

	func testFetchGear_ByName_SortsByName() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let gear1: Gear = .mock(id: id0, name: "first")
		let gear2: Gear = .mock(id: id1, name: "second")
		let gear3: Gear = .mock(id: id2, name: "third")

		try await withDependencies {
			$0.persistenceService.fetchGear = { request in
				return [gear1, gear2, gear3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .gear)
				return [
					.init(id: id2, lastUsedAt: Date(timeIntervalSince1970: 0)),
					.init(id: id1, lastUsedAt: Date(timeIntervalSince1970: 1)),
				]
			}
		} operation: {
			let dataProvider: GearDataProvider = .liveValue

			let result = try await dataProvider.fetchGear(.init(filter: nil, ordering: .byName))

			XCTAssertEqual(result, [gear1, gear2, gear3])
		}
	}
}
