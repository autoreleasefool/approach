import ComposableArchitecture
import Foundation
import LanesDataProviderInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest
@testable import LaneEditorFeature

@MainActor
final class AlleyLanesEditorTests: XCTestCase {
	func testLoadsInitialData() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let lane0: Lane = .init(id: id1, label: "", isAgainstWall: true, alley: id0)

		let store = TestStore(
			initialState: AlleyLanesEditor.State(alley: id0),
			reducer: AlleyLanesEditor()
		)

		store.dependencies.lanesDataProvider.fetchLanes = { _ in [lane0] }

		await store.send(.loadInitialData)

		await store.receive(.lanesResponse(.success([lane0]))) {
			$0.isLoadingInitialData = false
			$0.lanes = .init(uniqueElements: [.init(
				id: id1,
				label: "",
				isAgainstWall: true,
				isShowingAgainstWallNotice: true
			)])
		}
	}

	func testLoadsInitialDataCreatesLaneIfNecessary() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!

		let store = TestStore(
			initialState: AlleyLanesEditor.State(alley: id0),
			reducer: AlleyLanesEditor()
		)

		store.dependencies.lanesDataProvider.fetchLanes = { _ in [] }
		store.dependencies.uuid = .incrementing

		await store.send(.loadInitialData)

		await store.receive(.lanesResponse(.success([]))) {
			$0.isLoadingInitialData = false
			$0.lanes = .init(uniqueElements: [.init(
				id: id0,
				label: "1",
				isAgainstWall: true,
				isShowingAgainstWallNotice: true
			)])
		}
	}

	func testAddLaneWithPreviousNumericLane() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let lane0: Lane = .init(id: id1, label: "10", isAgainstWall: true, alley: id0)

		let store = TestStore(
			initialState: AlleyLanesEditor.State(alley: id0),
			reducer: AlleyLanesEditor()
		)

		store.dependencies.lanesDataProvider.fetchLanes = { _ in [lane0] }
		store.dependencies.uuid = .incrementing

		await store.send(.loadInitialData)

		await store.receive(.lanesResponse(.success([lane0]))) {
			$0.isLoadingInitialData = false
			$0.lanes = .init(uniqueElements: [.init(
				id: id1,
				label: "10",
				isAgainstWall: true,
				isShowingAgainstWallNotice: true
			)])
		}

		await store.send(.addLaneButtonTapped) {
			$0.lanes.append(.init(id: id0, label: "11", isAgainstWall: false, isShowingAgainstWallNotice: false))
		}
	}

	func testAddLaneWithoutPreviousNumericLane() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let lane0: Lane = .init(id: id1, label: "A", isAgainstWall: true, alley: id0)

		let store = TestStore(
			initialState: AlleyLanesEditor.State(alley: id0),
			reducer: AlleyLanesEditor()
		)

		store.dependencies.lanesDataProvider.fetchLanes = { _ in [lane0] }
		store.dependencies.uuid = .incrementing

		await store.send(.loadInitialData)

		await store.receive(.lanesResponse(.success([lane0]))) {
			$0.isLoadingInitialData = false
			$0.lanes = .init(uniqueElements: [.init(
				id: id1,
				label: "A",
				isAgainstWall: true,
				isShowingAgainstWallNotice: true
			)])
		}

		await store.send(.addLaneButtonTapped) {
			$0.lanes.append(.init(id: id0, label: "", isAgainstWall: false, isShowingAgainstWallNotice: false))
		}
	}
}
