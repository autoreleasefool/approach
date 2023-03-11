import GamesDataProvider
import GamesDataProviderInterface
import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest

final class GamesDataProviderTests: XCTestCase {
	func testFetchGames() async throws {
		let seriesId = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000004")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000005")!

		let game1: Game = .mock(series: seriesId, id: id1, ordinal: 1)
		let game2: Game = .mock(series: seriesId, id: id2, ordinal: 2)
		let game3: Game = .mock(series: seriesId, id: id3, ordinal: 3)

		try await withDependencies {
			$0.persistenceService.fetchGames = { request in
				return [game1, game2, game3]
			}
		} operation: {
			let dataProvider: GamesDataProvider = .liveValue

			let result = try await dataProvider.fetchGames(.init(filter: nil, ordering: .byOrdinal))

			XCTAssertEqual(result, [game1, game2, game3])
		}
	}
}
