import GamesDataProvider
import GamesDataProviderInterface
import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import SharedModelsLibrary
import XCTest

final class GamesDataProviderTests: XCTestCase {
	func testFetchGames() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!

		let firstGame = Game(seriesId: id0, id: id1, ordinal: 1, locked: .locked, manualScore: nil)
		let secondGame = Game(seriesId: id0, id: id2, ordinal: 2, locked: .locked, manualScore: nil)
		let thirdGame = Game(seriesId: id0, id: id3, ordinal: 3, locked: .locked, manualScore: nil)

		let (games, gamesContinuation) = AsyncThrowingStream<[Game], Error>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchGames = { request in
				XCTAssertEqual(request.series, id0)
				return games
			}
		} operation: {
			let dataProvider: GamesDataProvider = .liveValue

			var iterator = dataProvider.fetchGames(.init(series: id0, ordering: .byOrdinal)).makeAsyncIterator()

			gamesContinuation.yield([firstGame, secondGame, thirdGame])

			let result = try await iterator.next()

			XCTAssertEqual(result, [firstGame, secondGame, thirdGame])
		}
	}
}
