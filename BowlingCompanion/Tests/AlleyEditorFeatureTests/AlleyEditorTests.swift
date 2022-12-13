import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest
@testable import BaseFormFeature
@testable import AlleyEditorFeature

@MainActor
final class AlleyEditorTests: XCTestCase {
	func testChangesName() async {
		let store = TestStore(
			initialState: AlleyEditor.State(mode: .create),
			reducer: AlleyEditor()
		)

		XCTAssertEqual(store.state.base.form.name, "")

		await store.send(.set(\.base.form.$name, "J")) {
			$0.base.form.name = "J"
		}
		await store.send(.set(\.base.form.$name, "Jo")) {
			$0.base.form.name = "Jo"
		}
	}

	func testDiscardChanges() async {
		let store = TestStore(
			initialState: AlleyEditor.State(mode: .create),
			reducer: AlleyEditor()
		)

		XCTAssertEqual(store.state.base.form.name, "")

		await store.send(.set(\.base.form.$name, "J")) {
			$0.base.form.name = "J"
		}
		await store.send(.form(.discardButtonTapped)) {
			$0.base.alert = $0.base.discardAlert
		}
		await store.send(.form(.alert(.discardButtonTapped))) {
			$0.base.alert = nil
			$0.base.form.name = ""
		}
	}

	func testDeleteAlley() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let alley: Alley = .mock(id: id0)

		let store = TestStore(
			initialState: AlleyEditor.State(mode: .edit(alley)),
			reducer: AlleyEditor()
		)

		let expectation = self.expectation(description: "deleted")
		store.dependencies.persistenceService.deleteAlley = { deleted in
			XCTAssertEqual(deleted, alley)
			expectation.fulfill()
		}

		await store.send(.form(.deleteButtonTapped)) {
			$0.base.alert = $0.base.deleteAlert
		}

		let task = await store.send(.form(.alert(.deleteButtonTapped))) {
			$0.base.alert = nil
			$0.base.isLoading = true
		}

		await store.receive(.form(.deleteResult(.success(alley))))

		await task.finish()

		waitForExpectations(timeout: 1)
	}

	func testAlleyRequiresName() async {
		let store = TestStore(
			initialState: AlleyEditor.State(mode: .create),
			reducer: AlleyEditor()
		)

		store.dependencies.persistenceService.createAlley = { _ in
			XCTFail("Should not save")
		}

		await store.send(.form(.saveButtonTapped)).finish()
	}

	func testNewAlleyIsCreated() async {
		let store = TestStore(
			initialState: AlleyEditor.State(mode: .create),
			reducer: AlleyEditor()
		)

		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let alley: Alley = .mock(id: id0)

		store.dependencies.uuid = .incrementing

		await store.send(.set(\.base.form.$name, "Skyview")) {
			$0.base.form.name = "Skyview"
		}

		let expectation = self.expectation(description: "created")
		store.dependencies.persistenceService.createAlley = { created in
			XCTAssertEqual(created, alley)
			expectation.fulfill()
		}

		let task = await store.send(.form(.saveButtonTapped)) {
			$0.base.isLoading = true
		}
		await store.receive(.form(.saveModelResult(.success(alley))))
		await task.finish()

		waitForExpectations(timeout: 1)
	}

	func testExistingAlleyIsUpdated() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let alley: Alley = .mock(id: id0)
		let alley2: Alley = .mock(id: id0, name: "Sky")

		let store = TestStore(
			initialState: AlleyEditor.State(mode: .edit(alley)),
			reducer: AlleyEditor()
		)

		await store.send(.set(\.base.form.$name, "Sky")) {
			$0.base.form.name = "Sky"
		}

		let expectation = self.expectation(description: "created")
		store.dependencies.persistenceService.updateAlley = { updated in
			XCTAssertEqual(updated, alley2)
			expectation.fulfill()
		}

		let task = await store.send(.form(.saveButtonTapped)) {
			$0.base.isLoading = true
		}
		await store.receive(.form(.saveModelResult(.success(alley2))))
		await task.finish()

		waitForExpectations(timeout: 1)
	}

	func testDeletedAlleyMustExist() async {
		let store = TestStore(
			initialState: AlleyEditor.State(mode: .create),
			reducer: AlleyEditor()
		)

		await store.send(.form(.alert(.deleteButtonTapped)))
		await store.send(.form(.deleteButtonTapped))
	}

	func testErrorSavingUpdatesStates() async {
		let store = TestStore(
			initialState: AlleyEditor.State(mode: .create),
			reducer: AlleyEditor()
		)

		store.dependencies.uuid = .incrementing
		store.dependencies.persistenceService.createAlley = { _ in
			throw MockError.mock
		}

		await store.send(.set(\.base.form.$name, "Skyview")) {
			$0.base.form.name = "Skyview"
		}

		await store.send(.form(.saveButtonTapped)) {
			$0.base.isLoading = true
		}
		await store.receive(.form(.saveModelResult(.failure(MockError.mock)))) {
			$0.base.isLoading = false
		}
	}

	func testCreateHasChanges() async {
		let store = TestStore(
			initialState: AlleyEditor.State(mode: .create),
			reducer: AlleyEditor()
		)

		XCTAssertFalse(store.state.base.hasChanges)

		await store.send(.set(\.base.form.$name, "Skyview")) {
			$0.base.form.name = "Skyview"
		}

		XCTAssertTrue(store.state.base.hasChanges)
	}

	func testCreateIsSaveable() async {
		let store = TestStore(
			initialState: AlleyEditor.State(mode: .create),
			reducer: AlleyEditor()
		)

		XCTAssertFalse(store.state.base.isSaveable)

		await store.send(.set(\.base.form.$name, "Skyview")) {
			$0.base.form.name = "Skyview"
		}

		XCTAssertTrue(store.state.base.isSaveable)
	}

	func testEditHasChanges() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let alley: Alley = .mock(id: id0)

		let store = TestStore(
			initialState: AlleyEditor.State(mode: .edit(alley)),
			reducer: AlleyEditor()
		)

		XCTAssertFalse(store.state.base.hasChanges)

		await store.send(.set(\.base.form.$name, "Skyview Lanes")) {
			$0.base.form.name = "Skyview Lanes"
		}

		XCTAssertTrue(store.state.base.hasChanges)
	}

	func testEditIsSaveable() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let alley: Alley = .mock(id: id0)

		let store = TestStore(
			initialState: AlleyEditor.State(mode: .edit(alley)),
			reducer: AlleyEditor()
		)

		XCTAssertFalse(store.state.base.isSaveable)

		await store.send(.set(\.base.form.$name, "")) {
			$0.base.form.name = ""
		}
		XCTAssertFalse(store.state.base.isSaveable)
		await store.send(.set(\.base.form.$name, "Skyview Lanes")) {
			$0.base.form.name = "Skyview Lanes"
		}
		XCTAssertTrue(store.state.base.isSaveable)
	}
}

enum MockError: Error {
	case mock
}
