import Dependencies
import PreferenceServiceInterface
import RecentlyUsedServiceInterface
import XCTest
@testable import RecentlyUsedService

final class RecentlyUsedServiceTests: XCTestCase {
	func testUpdatesRecentlyUsedResource() {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!

		let expectation = self.expectation(description: "updated recently used")

		DependencyValues.withValues {
			$0.preferenceService.getStringArray = { key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				return nil
			}

			$0.preferenceService.setStringArray = { key, value in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				XCTAssertEqual([id0.uuidString], value)
				expectation.fulfill()
			}
		} operation: {
			let recentlyUsedService: RecentlyUsedService = .liveValue

			recentlyUsedService.didRecentlyUseResource(.bowlers, id0)
		}

		waitForExpectations(timeout: 1)
	}

	func testReplacesRecentlyUsedResourceIfExists() {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!

		let expectation = self.expectation(description: "updated recently used")

		DependencyValues.withValues {
			$0.preferenceService.getStringArray = { key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				return [id0.uuidString, id1.uuidString]
			}

			$0.preferenceService.setStringArray = { key, value in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				XCTAssertEqual([id1.uuidString, id0.uuidString], value)
				expectation.fulfill()
			}
		} operation: {
			let recentlyUsedService: RecentlyUsedService = .liveValue

			recentlyUsedService.didRecentlyUseResource(.bowlers, id1)
		}

		waitForExpectations(timeout: 1)
	}

	func testResetsResource() {
		let expectation = self.expectation(description: "reset recently used")

		DependencyValues.withValues {
			$0.preferenceService.removeKey = { key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				expectation.fulfill()
			}
		} operation: {
			let recentlyUsedService: RecentlyUsedService = .liveValue

			recentlyUsedService.resetRecentlyUsed(.bowlers)
		}

		waitForExpectations(timeout: 1)
	}

	func testObservesChanges() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!

		let getExpectation = self.expectation(description: "got recently used")
		getExpectation.expectedFulfillmentCount = 3
		let updateExpectation = self.expectation(description: "updated recently used")

		await DependencyValues.withValues {
			$0.preferenceService.getStringArray = { key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				getExpectation.fulfill()
				return [id0.uuidString]
			}

			$0.preferenceService.setStringArray = { key, value in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				XCTAssertEqual([id1.uuidString, id0.uuidString], value)
				updateExpectation.fulfill()
			}
		} operation: {
			let recentlyUsedService: RecentlyUsedService = .liveValue

			let recentlyUsed = recentlyUsedService.observeRecentlyUsed(.bowlers)
			var recentlyUsedIterator = recentlyUsed.makeAsyncIterator()

			let firstValue = await recentlyUsedIterator.next()
			XCTAssertEqual([id0], firstValue)

			recentlyUsedService.didRecentlyUseResource(.bowlers, id1)

			let secondValue = await recentlyUsedIterator.next()
			XCTAssertEqual([id0], secondValue)
		}

		await self.waitForExpectations(timeout: 1)
	}

	func testDoesNotObserveUnrelatedChanges() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let recentlyUsedService: RecentlyUsedService = .liveValue
		var recentlyUsed: AsyncStream<[UUID]>?
		var recentlyUsedIterator: AsyncStream<[UUID]>.Iterator?

		await DependencyValues.withValues {
			$0.preferenceService.getStringArray = { key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				return [id0.uuidString]
			}
		} operation: {
			recentlyUsed = recentlyUsedService.observeRecentlyUsed(.bowlers)
			recentlyUsedIterator = recentlyUsed!.makeAsyncIterator()

			let firstValue = await recentlyUsedIterator!.next()
			XCTAssertEqual([id0], firstValue)
		}

		DependencyValues.withValues {
			$0.preferenceService.getStringArray = { key in
				XCTAssertEqual("RecentlyUsed.alleys", key)
				return [id1.uuidString]
			}

			$0.preferenceService.setStringArray = { key, value in
				XCTAssertEqual("RecentlyUsed.alleys", key)
				XCTAssertEqual([id1.uuidString], value)
			}
		} operation: {
			recentlyUsedService.didRecentlyUseResource(.alleys, id1)
		}

		await DependencyValues.withValues {
			$0.preferenceService.getStringArray = { key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				return [id2.uuidString]
			}

			$0.preferenceService.setStringArray = { key, value in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				XCTAssertEqual([id2.uuidString], value)
			}
		} operation: {
			recentlyUsedService.didRecentlyUseResource(.bowlers, id2)

			// We shouldn't see id1 ever surfaced here
			let secondValue = await recentlyUsedIterator!.next()
			XCTAssertEqual([id2], secondValue)
		}
	}
}
