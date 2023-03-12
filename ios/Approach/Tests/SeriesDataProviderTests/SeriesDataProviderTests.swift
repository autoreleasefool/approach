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
		let bowlerId = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let leagueId = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000004")!

		let league: League = .mock(bowler: bowlerId, id: leagueId)
		let series1: Series = .mock(league: league.id, id: id1, date: Date())
		let series2: Series = .mock(league: league.id, id: id2, date: Date())

		try await withDependencies {
			$0.persistenceService.fetchSeries = { _ in
				return [series1, series2]
			}
		} operation: {
			let dataProvider: SeriesDataProvider = .liveValue

			let result = try await dataProvider.fetchSeries(.init(filter: .league(league), ordering: .byDate))

			XCTAssertEqual(result, [series1, series2])
		}
	}
}
