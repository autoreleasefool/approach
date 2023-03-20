import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest
@testable import LanesDataProvider
@testable import LanesDataProviderInterface

final class LanesDataProviderTests: XCTestCase {
	func testFetchLanesByLabel() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let id2 = UUID(uuidString: "00000000-0000-0000-0000-000000000002")!
		let id3 = UUID(uuidString: "00000000-0000-0000-0000-000000000003")!

		let lane1: Lane = .mock(id: id1, label: "first", alley: id0)
		let lane2: Lane = .mock(id: id2, label: "second", alley: id0)
		let lane3: Lane = .mock(id: id3, label: "third", alley: id0)

		try await withDependencies {
			$0.persistenceService.fetchLanes = { request in
				XCTAssertEqual(request.ordering, .byLabel)
				return [lane1, lane2, lane3]
			}
		} operation: {
			let dataProvider: LanesDataProvider = .liveValue

			let result = try await dataProvider.fetchLanes(.init(filter: nil, ordering: .byLabel))

			XCTAssertEqual(result, [lane1, lane2, lane3])
		}
	}

	func testFetchLanesById() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!

		try await withDependencies {
			$0.persistenceService.fetchLanes = { _ in
				return []
			}
		} operation: {
			let dataProvider: LanesDataProvider = .liveValue

			let result = try await dataProvider.fetchLanes(.init(filter: .id(id0), ordering: .byLabel))

			XCTAssertEqual(result, [])
		}
	}

	func testFetchLanesByAlleyId() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let alley: Alley = .mock(id: id0)

		try await withDependencies {
			$0.persistenceService.fetchLanes = { _ in
				return []
			}
		} operation: {
			let dataProvider: LanesDataProvider = .liveValue

			let result = try await dataProvider.fetchLanes(.init(filter: .alley(alley), ordering: .byLabel))

			XCTAssertEqual(result, [])
		}
	}
}
