import Dependencies
@testable import RecentlyUsedService
import RecentlyUsedServiceInterface
import UserDefaultsPackageServiceInterface
import XCTest

final class RecentlyUsedServiceTests: XCTestCase {
	@Dependency(RecentlyUsedService.self) var recentlyUsed

	static let encoder: JSONEncoder = {
		let encoder = JSONEncoder()
		encoder.outputFormatting = [.sortedKeys]
		return encoder
	}()

	func testDidRecentlyUseResource_UpdatesEntries() {
		let cache = LockIsolated<String?>(nil)
		let now = Date(timeIntervalSince1970: 1672519204)

		withDependencies {
			$0[RecentlyUsedService.self] = .liveValue
			$0.date = .constant(now)
			$0[JSONEncoderService.self] = .init(Self.encoder)

			$0.userDefaults.string = { @Sendable key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				return cache.value
			}

			$0.userDefaults.setString = { @Sendable key, value in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				cache.setValue(value)
			}
		} operation: {
			self.recentlyUsed.didRecentlyUseResource(.bowlers, UUID(0))
		}

		XCTAssertEqual(Self.entriesString(ids: [UUID(0)]), cache.value)
	}

	func testDidRecentlyUseResource_RemovesOldEntries() {
		let cache = LockIsolated<String?>(nil)
		let now = Date(timeIntervalSince1970: 1672519204)

		withDependencies {
			$0[RecentlyUsedService.self] = .liveValue
			$0.date = .constant(now)
			$0[JSONEncoderService.self] = .init(Self.encoder)

			$0.userDefaults.string = { @Sendable _ in
				return cache.value
			}

			$0.userDefaults.setString = { @Sendable _, value in
				cache.setValue(value)
			}

		} operation: {
			for i in 0...RecentlyUsedService.maximumEntries * 2 {
				self.recentlyUsed.didRecentlyUseResource(.bowlers, UUID(i))
			}
		}

		XCTAssertEqual(
			Self.entriesString(
				ids: (RecentlyUsedService.maximumEntries + 1...RecentlyUsedService.maximumEntries * 2)
					.reversed()
					.map(UUID.init)
			),
			cache.value
		)
	}

	func testReplacesRecentlyUsedResourceIfExists() {
		let cache = LockIsolated<String?>(Self.entriesString(ids: [UUID(0), UUID(1)]))
		let now = Date(timeIntervalSince1970: 1672519204)

		withDependencies {
			$0[RecentlyUsedService.self] = .liveValue
			$0.date = .constant(now)
			$0[JSONEncoderService.self] = .init(Self.encoder)

			$0.userDefaults.string = { @Sendable key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				return cache.value
			}

			$0.userDefaults.setString = { @Sendable key, value in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				cache.setValue(value)
			}
		} operation: {
			self.recentlyUsed.didRecentlyUseResource(.bowlers, UUID(1))
		}

		XCTAssertEqual(Self.entriesString(ids: [UUID(1), UUID(0)]), cache.value)
	}

	func testResetRecentlyUsed_ResetsEntries() {
		let cache = LockIsolated<String?>(Self.entriesString(ids: [UUID(0), UUID(1)]))

		withDependencies {
			$0[RecentlyUsedService.self] = .liveValue
			$0.userDefaults.remove = { @Sendable key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				cache.setValue(nil)
			}
		} operation: {
			self.recentlyUsed.resetRecentlyUsed(.bowlers)
		}

		XCTAssertNil(cache.value)
	}

	func testObserve_ReceivesChanges() async {
		let cache = LockIsolated<String?>(Self.entriesString(ids: [UUID(0)]))
		let now = Date(timeIntervalSince1970: 1672519204)

		await withDependencies {
			$0[RecentlyUsedService.self] = .liveValue
			$0[JSONEncoderService.self] = .init(Self.encoder)

			$0.userDefaults.string = { @Sendable key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				return cache.value
			}

			$0.userDefaults.setString = { @Sendable key, value in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				cache.setValue(value)
			}

			$0.date = .constant(now)
		} operation: {
			let recentlyUsed = self.recentlyUsed.observeRecentlyUsed(.bowlers)
			var recentlyUsedIterator = recentlyUsed.makeAsyncIterator()

			let firstValue = await recentlyUsedIterator.next()
			XCTAssertEqual([RecentlyUsedService.Entry(id: UUID(0), lastUsedAt: now)], firstValue)

			self.recentlyUsed.didRecentlyUseResource(.bowlers, UUID(1))

			let secondValue = await recentlyUsedIterator.next()
			XCTAssertEqual([
				RecentlyUsedService.Entry(id: UUID(1), lastUsedAt: now),
				RecentlyUsedService.Entry(id: UUID(0), lastUsedAt: now),
			], secondValue)
		}
	}

	func testObserve_DoesNotReceiveUnrelatedChanges() async {
		let cache = LockIsolated<String?>(Self.entriesString(ids: [UUID(0)]))
		let now = Date(timeIntervalSince1970: 1672519204)

		var recentlyUsed: AsyncStream<[RecentlyUsedService.Entry]>?
		var recentlyUsedIterator: AsyncStream<[RecentlyUsedService.Entry]>.Iterator?

		await withDependencies {
			$0[RecentlyUsedService.self] = .liveValue
			$0[JSONEncoderService.self] = .init(Self.encoder)

			$0.userDefaults.string = { @Sendable key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				return cache.value
			}
		} operation: {
			recentlyUsed = self.recentlyUsed.observeRecentlyUsed(.bowlers)
			recentlyUsedIterator = recentlyUsed!.makeAsyncIterator()

			let firstValue = await recentlyUsedIterator!.next()
			XCTAssertEqual([RecentlyUsedService.Entry(id: UUID(0), lastUsedAt: now)], firstValue)
		}

		cache.setValue(Self.entriesString(ids: [UUID(1)]))

		withDependencies {
			$0[RecentlyUsedService.self] = .liveValue
			$0.date = .constant(now)
			$0[JSONEncoderService.self] = .init(Self.encoder)

			$0.userDefaults.string = { @Sendable key in
				XCTAssertEqual("RecentlyUsed.alleys", key)
				return cache.value
			}

			$0.userDefaults.setString = { @Sendable key, value in
				XCTAssertEqual("RecentlyUsed.alleys", key)
				cache.setValue(value)
			}
		} operation: {
			self.recentlyUsed.didRecentlyUseResource(.alleys, UUID(1))
		}

		XCTAssertEqual(Self.entriesString(ids: [UUID(1)]), cache.value)

		cache.setValue(Self.entriesString(ids: [UUID(2)]))

		await withDependencies {
			$0[RecentlyUsedService.self] = .liveValue
			$0.date = .constant(now)
			$0[JSONEncoderService.self] = .init(Self.encoder)

			$0.userDefaults.string = { @Sendable key in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				return cache.value
			}

			$0.userDefaults.setString = { @Sendable key, value in
				XCTAssertEqual("RecentlyUsed.bowlers", key)
				cache.setValue(value)
			}
		} operation: {
			self.recentlyUsed.didRecentlyUseResource(.bowlers, UUID(2))

			// We shouldn't see UUID(1) ever surfaced here
			let secondValue = await recentlyUsedIterator!.next()
			XCTAssertEqual([RecentlyUsedService.Entry(id: UUID(2), lastUsedAt: now)], secondValue)
		}

		XCTAssertEqual(Self.entriesString(ids: [UUID(2)]), cache.value)
	}

	static func entriesString(ids: [UUID], date: Date = Date(timeIntervalSince1970: 1672519204)) -> String {
		guard let entries = try? encoder.encode(ids.map { RecentlyUsedService.Entry(id: $0, lastUsedAt: date) }) else {
			XCTFail("Failed to encode entries")
			return ""
		}

		return String(data: entries, encoding: .utf8)!
	}
}
