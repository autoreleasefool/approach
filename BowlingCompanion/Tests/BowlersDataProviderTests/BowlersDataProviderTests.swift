import BowlersDataProvider
import BowlersDataProviderInterface
import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import XCTest

final class BowlersDataProviderTests: XCTestCase {
	func testFetchBowlers_ByRecentlyUsed_SortsByNameWhenNoRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let firstBowler = Bowler(id: id0, name: "first")
		let secondBowler = Bowler(id: id1, name: "second")
		let thirdBowler = Bowler(id: id2, name: "third")

		let (bowlers, bowlersContinuation) = AsyncThrowingStream<[Bowler], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchBowlers = { request in
				XCTAssertEqual(request.ordering, .byName)
				return bowlers
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: BowlersDataProvider = .liveValue

			var iterator = dataProvider.fetchBowlers(.init(ordering: .byRecentlyUsed)).makeAsyncIterator()

			bowlersContinuation.yield([firstBowler, secondBowler, thirdBowler])
			idsContinuation.yield([])

			let result = try await iterator.next()

			XCTAssertEqual(result, [firstBowler, secondBowler, thirdBowler])
		}
	}

	func testFetchBowlers_ByRecentlyUsed_SortsByRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let firstBowler = Bowler(id: id0, name: "first")
		let secondBowler = Bowler(id: id1, name: "second")
		let thirdBowler = Bowler(id: id2, name: "third")

		let (bowlers, bowlersContinuation) = AsyncThrowingStream<[Bowler], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchBowlers = { request in
				XCTAssertEqual(request.ordering, .byName)
				return bowlers
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: BowlersDataProvider = .liveValue

			var iterator = dataProvider.fetchBowlers(.init(ordering: .byRecentlyUsed)).makeAsyncIterator()

			bowlersContinuation.yield([firstBowler, secondBowler, thirdBowler])
			idsContinuation.yield([id2, id1])

			let result = try await iterator.next()

			XCTAssertEqual(result, [thirdBowler, secondBowler, firstBowler])
		}
	}

	func testFetchBowlers_ByName_SortsByName() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let firstBowler = Bowler(id: id0, name: "first")
		let secondBowler = Bowler(id: id1, name: "second")
		let thirdBowler = Bowler(id: id2, name: "third")

		let (bowlers, bowlersContinuation) = AsyncThrowingStream<[Bowler], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchBowlers = { request in
				XCTAssertEqual(request.ordering, .byName)
				return bowlers
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: BowlersDataProvider = .liveValue

			var iterator = dataProvider.fetchBowlers(.init(ordering: .byName)).makeAsyncIterator()

			bowlersContinuation.yield([firstBowler, secondBowler, thirdBowler])
			idsContinuation.yield([id2, id1])

			let result = try await iterator.next()

			XCTAssertEqual(result, [firstBowler, secondBowler, thirdBowler])
		}
	}
}
