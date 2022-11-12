import AlleysDataProvider
import AlleysDataProviderInterface
import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import XCTest

final class AlleysDataProviderTests: XCTestCase {
	func testFetchAlleys_ByRecentlyUsed_SortsByNameWhenNoRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let firstAlley = Alley(id: id0, name: "first")
		let secondAlley = Alley(id: id1, name: "second")
		let thirdAlley = Alley(id: id2, name: "third")

		let (alleys, alleysContinuation) = AsyncThrowingStream<[Alley], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchAlleys = { request in
				XCTAssertEqual(request.ordering, .byName)
				return alleys
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: AlleysDataProvider = .liveValue

			var iterator = dataProvider.fetchAlleys(.init(ordering: .byRecentlyUsed)).makeAsyncIterator()

			alleysContinuation.yield([firstAlley, secondAlley, thirdAlley])
			idsContinuation.yield([])

			let result = try await iterator.next()

			XCTAssertEqual(result, [firstAlley, secondAlley, thirdAlley])
		}
	}

	func testFetchAlleys_ByRecentlyUsed_SortsByRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let firstAlley = Alley(id: id0, name: "first")
		let secondAlley = Alley(id: id1, name: "second")
		let thirdAlley = Alley(id: id2, name: "third")

		let (alleys, alleysContinuation) = AsyncThrowingStream<[Alley], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchAlleys = { request in
				XCTAssertEqual(request.ordering, .byName)
				return alleys
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: AlleysDataProvider = .liveValue

			var iterator = dataProvider.fetchAlleys(.init(ordering: .byRecentlyUsed)).makeAsyncIterator()

			alleysContinuation.yield([firstAlley, secondAlley, thirdAlley])
			idsContinuation.yield([id2, id1])

			let result = try await iterator.next()

			XCTAssertEqual(result, [thirdAlley, secondAlley, firstAlley])
		}
	}

	func testFetchAlleys_ByName_SortsByName() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let firstAlley = Alley(id: id0, name: "first")
		let secondAlley = Alley(id: id1, name: "second")
		let thirdAlley = Alley(id: id2, name: "third")

		let (alleys, alleysContinuation) = AsyncThrowingStream<[Alley], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchAlleys = { request in
				XCTAssertEqual(request.ordering, .byName)
				return alleys
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: AlleysDataProvider = .liveValue

			var iterator = dataProvider.fetchAlleys(.init(ordering: .byName)).makeAsyncIterator()

			alleysContinuation.yield([firstAlley, secondAlley, thirdAlley])
			idsContinuation.yield([id2, id1])

			let result = try await iterator.next()

			XCTAssertEqual(result, [firstAlley, secondAlley, thirdAlley])
		}
	}
}
