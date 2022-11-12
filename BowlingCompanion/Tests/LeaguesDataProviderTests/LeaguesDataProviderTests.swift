import ComposableArchitecture
import Dependencies
import LeaguesDataProvider
import LeaguesDataProviderInterface
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SharedModelsLibrary
import XCTest

final class LeaguesDataProviderTests: XCTestCase {
	func testFetchLeagues_ByRecentlyUsed_SortsByNameWhenNoRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

		let firstLeague = League(
			bowlerId: id0,
			id: id1,
			name: "first",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil
		)
		let secondLeague = League(
			bowlerId: id0,
			id: id2,
			name: "second",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil
		)
		let thirdLeague = League(
			bowlerId: id0,
			id: id3,
			name: "third",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil
		)

		let (leagues, leaguesContinuation) = AsyncThrowingStream<[League], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchLeagues = { request in
				XCTAssertEqual(request.ordering, .byName)
				return leagues
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: LeaguesDataProvider = .liveValue

			var iterator = dataProvider.fetchLeagues(.init(bowler: id0, ordering: .byRecentlyUsed)).makeAsyncIterator()

			leaguesContinuation.yield([firstLeague, secondLeague, thirdLeague])
			idsContinuation.yield([])

			let result = try await iterator.next()

			XCTAssertEqual(result, [firstLeague, secondLeague, thirdLeague])
		}
	}

	func testFetchLeagues_ByRecentlyUsed_SortsByRecentlyUsed() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

		let firstLeague = League(
			bowlerId: id0,
			id: id1,
			name: "first",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil
		)
		let secondLeague = League(
			bowlerId: id0,
			id: id2,
			name: "second",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil
		)
		let thirdLeague = League(
			bowlerId: id0,
			id: id3,
			name: "third",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil
		)

		let (leagues, leaguesContinuation) = AsyncThrowingStream<[League], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchLeagues = { request in
				XCTAssertEqual(request.ordering, .byName)
				return leagues
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: LeaguesDataProvider = .liveValue

			var iterator = dataProvider.fetchLeagues(.init(bowler: id0, ordering: .byRecentlyUsed)).makeAsyncIterator()

			leaguesContinuation.yield([firstLeague, secondLeague, thirdLeague])
			idsContinuation.yield([id3, id2])

			let result = try await iterator.next()

			XCTAssertEqual(result, [thirdLeague, secondLeague, firstLeague])
		}
	}

	func testFetchLeagues_ByName_SortsByName() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

		let firstLeague = League(
			bowlerId: id0,
			id: id1,
			name: "first",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil
		)
		let secondLeague = League(
			bowlerId: id0,
			id: id2,
			name: "second",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil
		)
		let thirdLeague = League(
			bowlerId: id0,
			id: id3,
			name: "third",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil
		)

		let (leagues, leaguesContinuation) = AsyncThrowingStream<[League], Error>.streamWithContinuation()
		let (ids, idsContinuation) = AsyncStream<[UUID]>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchLeagues = { request in
				XCTAssertEqual(request.ordering, .byName)
				return leagues
			}
			$0.recentlyUsedService.observeRecentlyUsed = { _ in ids }
		} operation: {
			let dataProvider: LeaguesDataProvider = .liveValue

			var iterator = dataProvider.fetchLeagues(.init(bowler: id0, ordering: .byName)).makeAsyncIterator()

			leaguesContinuation.yield([firstLeague, secondLeague, thirdLeague])
			idsContinuation.yield([id2, id1])

			let result = try await iterator.next()

			XCTAssertEqual(result, [firstLeague, secondLeague, thirdLeague])
		}
	}
}
