import ComposableArchitecture
import Foundation
@testable import LaneEditorFeature
import LanesDataProviderInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest

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
			$0.existingLanes = [lane0]
			$0.lanes = .init(uniqueElements: [
				.init(
					id: id1,
					label: "",
					isAgainstWall: true
				),
			])
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
			$0.lanes = .init(uniqueElements: [
				.init(
					id: id0,
					label: "1",
					isAgainstWall: true
				),
			])
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
			$0.existingLanes = [lane0]
			$0.lanes = .init(uniqueElements: [
				.init(
					id: id1,
					label: "10",
					isAgainstWall: true
				),
			])
		}

		await store.send(.addLaneButtonTapped) {
			$0.lanes.append(.init(id: id0, label: "11", isAgainstWall: false))
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
			$0.existingLanes = [lane0]
			$0.lanes = .init(uniqueElements: [
				.init(
					id: id1,
					label: "A",
					isAgainstWall: true
				),
			])
		}

		await store.send(.addLaneButtonTapped) {
			$0.lanes.append(.init(id: id0, label: "", isAgainstWall: false))
		}
	}

	func testPromptsToDeleteExistingLane() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let lane0: Lane = .init(id: id1, label: "1", isAgainstWall: true, alley: id0)

		let store = TestStore(
			initialState: AlleyLanesEditor.State(alley: id0),
			reducer: AlleyLanesEditor()
		)

		store.dependencies.lanesDataProvider.fetchLanes = { _ in [lane0] }

		await store.send(.loadInitialData)

		await store.receive(.lanesResponse(.success([lane0]))) {
			$0.isLoadingInitialData = false
			$0.existingLanes = [lane0]
			$0.lanes = .init(uniqueElements: [
				.init(
					id: id1,
					label: "1",
					isAgainstWall: true
				),
			])
		}

		await store.send(.laneEditor(id: id1, action: .swipeAction(.delete))) {
			$0.alert = AlleyLanesEditor.alert(toDelete: lane0)
		}
	}

	func testDeletesExistingLane() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let lane0: Lane = .init(id: id1, label: "1", isAgainstWall: true, alley: id0)

		let store = TestStore(
			initialState: AlleyLanesEditor.State(alley: id0),
			reducer: AlleyLanesEditor()
		)

		store.dependencies.lanesDataProvider.fetchLanes = { _ in [lane0] }
		store.dependencies.persistenceService.deleteLane = { deleted in
			XCTAssertEqual(deleted, lane0)
		}

		await store.send(.loadInitialData)

		await store.receive(.lanesResponse(.success([lane0]))) {
			$0.isLoadingInitialData = false
			$0.existingLanes = [lane0]
			$0.lanes = .init(uniqueElements: [
				.init(
					id: id1,
					label: "1",
					isAgainstWall: true
				),
			])
		}

		await store.send(.alert(.deleteButtonTapped(lane0)))

		await store.receive(.laneDeleteResponse(.success(id1))) {
			$0.existingLanes = []
			$0.lanes = []
		}
	}

	func testDeletesNewLaneWithoutPrompt() async {
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
			$0.existingLanes = [lane0]
			$0.lanes = .init(uniqueElements: [
				.init(
					id: id1,
					label: "10",
					isAgainstWall: true
				),
			])
		}

		await store.send(.addLaneButtonTapped) {
			$0.lanes.append(.init(id: id0, label: "11", isAgainstWall: false))
		}

		await store.send(.laneEditor(id: id0, action: .swipeAction(.delete))) {
			$0.lanes = .init(uniqueElements: $0.lanes.dropLast())
		}
	}

	func testDismissesPrompt() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let lane0: Lane = .init(id: id1, label: "1", isAgainstWall: true, alley: id0)

		let store = TestStore(
			initialState: AlleyLanesEditor.State(alley: id0),
			reducer: AlleyLanesEditor()
		)

		store.dependencies.lanesDataProvider.fetchLanes = { _ in [lane0] }

		await store.send(.loadInitialData)

		await store.receive(.lanesResponse(.success([lane0]))) {
			$0.isLoadingInitialData = false
			$0.existingLanes = [lane0]
			$0.lanes = .init(uniqueElements: [
				.init(
					id: id1,
					label: "1",
					isAgainstWall: true
				),
			])
		}

		await store.send(.laneEditor(id: id1, action: .swipeAction(.delete))) {
			$0.alert = AlleyLanesEditor.alert(toDelete: lane0)
		}

		await store.send(.alert(.dismissed)) {
			$0.alert = nil
		}
	}
}
