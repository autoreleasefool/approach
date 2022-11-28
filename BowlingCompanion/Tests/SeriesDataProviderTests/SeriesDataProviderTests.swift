import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import SeriesDataProvider
import SeriesDataProviderInterface
import SharedModelsLibrary
import XCTest

final class SeriesDataProviderTests: XCTestCase {
	func testFetchSeries() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let firstSeries = Series(league: id0, id: id1, date: Date(), numberOfGames: 4)
		let secondSeries = Series(league: id0, id: id2, date: Date(), numberOfGames: 4)

		let (series, seriesContinuation) = AsyncThrowingStream<[Series], Error>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchSeries = { request in
				XCTAssertEqual(request.league, id0)
				return series
			}
		} operation: {
			let dataProvider: SeriesDataProvider = .liveValue

			var iterator = dataProvider.fetchSeries(.init(league: id0, ordering: .byDate)).makeAsyncIterator()

			seriesContinuation.yield([firstSeries, secondSeries])

			let result = try await iterator.next()

			XCTAssertEqual(result, [firstSeries, secondSeries])
		}
	}
}
