import ComposableArchitecture
import Dependencies
import GearDataProvider
import GearDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import XCTest

final class GearDataProviderTests: XCTestCase {
	func testFetchGear_ByRecentlyUsed_SortsByNameWhenNoRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let firstGear = Gear(bowler: nil, id: id0, name: "first", kind: .bowlingBall)
		let secondGear = Gear(bowler: nil, id: id1, name: "second", kind: .bowlingBall)
		let thirdGear = Gear(bowler: nil, id: id2, name: "third", kind: .bowlingBall)

		let (gear, gearContinuation) = AsyncThrowingStream<[Gear], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchGear = { request in
				XCTAssertEqual(request.ordering, .byName)
				return gear
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: GearDataProvider = .liveValue

			var iterator = dataProvider.fetchGear(.init(ordering: .byRecentlyUsed)).makeAsyncIterator()

			gearContinuation.yield([firstGear, secondGear, thirdGear])
			idsContinuation.yield([])

			let result = try await iterator.next()

			XCTAssertEqual(result, [firstGear, secondGear, thirdGear])
		}
	}

	func testFetchGear_ByRecentlyUsed_SortsByRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let firstGear = Gear(bowler: nil, id: id0, name: "first", kind: .bowlingBall)
		let secondGear = Gear(bowler: nil, id: id1, name: "second", kind: .bowlingBall)
		let thirdGear = Gear(bowler: nil, id: id2, name: "third", kind: .bowlingBall)

		let (gear, gearContinuation) = AsyncThrowingStream<[Gear], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchGear = { request in
				XCTAssertEqual(request.ordering, .byName)
				return gear
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: GearDataProvider = .liveValue

			var iterator = dataProvider.fetchGear(.init(ordering: .byRecentlyUsed)).makeAsyncIterator()

			gearContinuation.yield([firstGear, secondGear, thirdGear])
			idsContinuation.yield([id2, id1])

			let result = try await iterator.next()

			XCTAssertEqual(result, [thirdGear, secondGear, firstGear])
		}
	}

	func testFetchGear_ByName_SortsByName() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let firstGear = Gear(bowler: nil, id: id0, name: "first", kind: .bowlingBall)
		let secondGear = Gear(bowler: nil, id: id1, name: "second", kind: .bowlingBall)
		let thirdGear = Gear(bowler: nil, id: id2, name: "third", kind: .bowlingBall)

		let (gear, gearContinuation) = AsyncThrowingStream<[Gear], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchGear = { request in
				XCTAssertEqual(request.ordering, .byName)
				return gear
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: GearDataProvider = .liveValue

			var iterator = dataProvider.fetchGear(.init(ordering: .byName)).makeAsyncIterator()

			gearContinuation.yield([firstGear, secondGear, thirdGear])
			idsContinuation.yield([id2, id1])

			let result = try await iterator.next()

			XCTAssertEqual(result, [firstGear, secondGear, thirdGear])
		}
	}
}
