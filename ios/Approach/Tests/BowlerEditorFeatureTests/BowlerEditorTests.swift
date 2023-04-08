@testable import BaseFormLibrary
@testable import BowlerEditorFeature
import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest

@MainActor
final class BowlerEditorTests: XCTestCase {
	func testChangesName() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
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
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
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

	func testDeleteBowler() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler: Bowler = .mock(id: id0)

		let store = TestStore(
			initialState: BowlerEditor.State(mode: .edit(bowler)),
			reducer: BowlerEditor()
		)

		let expectation = self.expectation(description: "deleted")
		store.dependencies.persistenceService.deleteBowler = { deleted in
			XCTAssertEqual(deleted, bowler)
			expectation.fulfill()
		}

		await store.send(.form(.deleteButtonTapped)) {
			$0.base.alert = $0.base.deleteAlert
		}

		let task = await store.send(.form(.alert(.deleteButtonTapped))) {
			$0.base.alert = nil
			$0.base.isLoading = true
		}

		await store.receive(.form(.deleteResult(.success(bowler))))

		await task.finish()

		waitForExpectations(timeout: 1)
	}

	func testBowlerRequiresName() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		store.dependencies.persistenceService.createBowler = { _ in
			XCTFail("Should not save")
		}

		await store.send(.form(.saveButtonTapped)).finish()
	}

	func testNewBowlerIsCreated() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler: Bowler = .mock(id: id0, name: "Joe")

		store.dependencies.uuid = .incrementing

		await store.send(.set(\.base.form.$name, "Joe")) {
			$0.base.form.name = "Joe"
		}

		let expectation = self.expectation(description: "created")
		store.dependencies.persistenceService.createBowler = { created in
			XCTAssertEqual(created, bowler)
			expectation.fulfill()
		}

		let task = await store.send(.form(.saveButtonTapped)) {
			$0.base.isLoading = true
		}
		await store.receive(.form(.saveModelResult(.success(bowler))))
		await task.finish()

		waitForExpectations(timeout: 1)
	}

	func testExistingBowlerIsUpdated() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler: Bowler = .mock(id: id0, name: "Joe")
		let bowler2: Bowler = .mock(id: id0, name: "Joseph")

		let store = TestStore(
			initialState: BowlerEditor.State(mode: .edit(bowler)),
			reducer: BowlerEditor()
		)

		await store.send(.set(\.base.form.$name, "Joseph")) {
			$0.base.form.name = "Joseph"
		}

		let expectation = self.expectation(description: "created")
		store.dependencies.persistenceService.updateBowler = { updated in
			XCTAssertEqual(updated, bowler2)
			expectation.fulfill()
		}

		let task = await store.send(.form(.saveButtonTapped)) {
			$0.base.isLoading = true
		}
		await store.receive(.form(.saveModelResult(.success(bowler2))))
		await task.finish()

		waitForExpectations(timeout: 1)
	}

	func testDeletedBowlerMustExist() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		await store.send(.form(.alert(.deleteButtonTapped)))
		await store.send(.form(.deleteButtonTapped))
	}

	func testErrorSavingUpdatesStates() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		store.dependencies.uuid = .incrementing
		store.dependencies.persistenceService.createBowler = { _ in
			throw MockError.mock
		}

		await store.send(.set(\.base.form.$name, "Joseph")) {
			$0.base.form.name = "Joseph"
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
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		XCTAssertFalse(store.state.base.hasChanges)

		await store.send(.set(\.base.form.$name, "Joseph")) {
			$0.base.form.name = "Joseph"
		}

		XCTAssertTrue(store.state.base.hasChanges)
	}

	func testCreateIsSaveable() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		XCTAssertFalse(store.state.base.isSaveable)

		await store.send(.set(\.base.form.$name, "Joseph")) {
			$0.base.form.name = "Joseph"
		}

		XCTAssertTrue(store.state.base.isSaveable)
	}

	func testEditHasChanges() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler: Bowler = .mock(id: id0)

		let store = TestStore(
			initialState: BowlerEditor.State(mode: .edit(bowler)),
			reducer: BowlerEditor()
		)

		XCTAssertFalse(store.state.base.hasChanges)

		await store.send(.set(\.base.form.$name, "Joe")) {
			$0.base.form.name = "Joe"
		}

		XCTAssertTrue(store.state.base.hasChanges)
	}

	func testEditIsSaveable() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler: Bowler = .mock(id: id0)

		let store = TestStore(
			initialState: BowlerEditor.State(mode: .edit(bowler)),
			reducer: BowlerEditor()
		)

		XCTAssertFalse(store.state.base.isSaveable)

		await store.send(.set(\.base.form.$name, "")) {
			$0.base.form.name = ""
		}
		XCTAssertFalse(store.state.base.isSaveable)
		await store.send(.set(\.base.form.$name, "Joe")) {
			$0.base.form.name = "Joe"
		}
		XCTAssertTrue(store.state.base.isSaveable)
	}
}

enum MockError: Error {
	case mock
}
