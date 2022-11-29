import AlleysDataProvider
import AlleysDataProviderInterface
import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest

final class AlleysDataProviderTests: XCTestCase {
	func testFetchAlleys_ByRecentlyUsed_SortsByNameWhenNoRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let alley1: Alley = .mock(id: id0, name: "first")
		let alley2: Alley = .mock(id: id1, name: "second")
		let alley3: Alley = .mock(id: id2, name: "third")

		try await DependencyValues.withValues {
			$0.persistenceService.fetchAlleys = { request in
				XCTAssertEqual(request.ordering, .byName)
				return [alley1, alley2, alley3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .alleys)
				return []
			}
		} operation: {
			let dataProvider: AlleysDataProvider = .liveValue

			let result = try await dataProvider.fetchAlleys(.init(ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [alley1, alley2, alley3])
		}
	}

	func testFetchAlleys_ByRecentlyUsed_SortsByRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let alley1: Alley = .mock(id: id0, name: "first")
		let alley2: Alley = .mock(id: id1, name: "second")
		let alley3: Alley = .mock(id: id2, name: "third")

		try await DependencyValues.withValues {
			$0.persistenceService.fetchAlleys = { request in
				XCTAssertEqual(request.ordering, .byName)
				return [alley1, alley2, alley3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .alleys)
				return [id2, id1]
			}
		} operation: {
			let dataProvider: AlleysDataProvider = .liveValue

			let result = try await dataProvider.fetchAlleys(.init(ordering: .byRecentlyUsed))

			XCTAssertEqual(result, [alley3, alley2, alley1])
		}
	}

	func testFetchAlleys_ByName_SortsByName() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let alley1: Alley = .mock(id: id0, name: "first")
		let alley2: Alley = .mock(id: id1, name: "second")
		let alley3: Alley = .mock(id: id2, name: "third")

		try await DependencyValues.withValues {
			$0.persistenceService.fetchAlleys = { request in
				XCTAssertEqual(request.ordering, .byName)
				return [alley1, alley2, alley3]
			}
			$0.recentlyUsedService.getRecentlyUsed = { category in
				XCTAssertEqual(category, .alleys)
				return [id2, id1]
			}
		} operation: {
			let dataProvider: AlleysDataProvider = .liveValue

			let result = try await dataProvider.fetchAlleys(.init(ordering: .byName))

			XCTAssertEqual(result, [alley1, alley2, alley3])
		}
	}
}
