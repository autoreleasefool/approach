import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import SeriesDataProvider
import SeriesDataProviderInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest

final class SeriesDataProviderTests: XCTestCase {
	func testFetchSeries() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!

		let series1: Series = .mock(league: id0, id: id1, date: Date())
		let series2: Series = .mock(league: id0, id: id2, date: Date())

		try await DependencyValues.withValues {
			$0.persistenceService.fetchSeries = { request in
				XCTAssertEqual(request.league, id0)
				return [series1, series2]
			}
		} operation: {
			let dataProvider: SeriesDataProvider = .liveValue

			let result = try await dataProvider.fetchSeries(.init(league: id0, ordering: .byDate))

			XCTAssertEqual(result, [series1, series2])
		}
	}
}
