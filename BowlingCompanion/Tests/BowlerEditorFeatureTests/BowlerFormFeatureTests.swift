import BowlerEditorFeature
import ComposableArchitecture
import SharedModelsLibrary
import XCTest

@MainActor
final class BowlerEditorFeatureTests: XCTestCase {
	func testChangesName() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		XCTAssertEqual(store.state.name, "")

		await store.send(.nameChange("J")) {
			$0.name = "J"
		}.finish()

		await store.send(.nameChange("Jo")) {
			$0.name = "Jo"
		}.finish()

		await store.send(.nameChange("Joe")) {
			$0.name = "Joe"
		}.finish()
	}

	func testEditBowlerSetsName() async {
		let mockBowler = Bowler(id: UUID(), name: "Joseph")
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .edit(mockBowler)),
			reducer: BowlerEditor()
		)

		XCTAssertEqual(store.state.name, "Joseph")
	}

	func testSaveButtonStartsTask() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		store.dependencies.uuid = .incrementing
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!

		let expectation = self.expectation(description: "saved")
		store.dependencies.bowlersDataProvider.save = { bowler in
			XCTAssertEqual(bowler.name, "Joe")
			expectation.fulfill()
		}

		await store.send(.nameChange("Joe")) {
			$0.name = "Joe"
		}.finish()

		_ = await store.send(.saveButtonTapped) {
			$0.isSaving = true
		}

		await store.receive(.saveBowlerResult(.success(Bowler(id: id0, name: "Joe")))) {
			$0.isSaving = false
		}

		wait(for: [expectation], timeout: 1)
	}

	func testEmptyNameDoesNotSave() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		await store.send(.nameChange("")).finish()

		await store.send(.saveButtonTapped).finish()
	}

	func testErrorSavingUpdatesState() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		store.dependencies.uuid = .incrementing

		store.dependencies.bowlersDataProvider.save = { _ in
			throw MockError.mock
		}

		await store.send(.nameChange("Joe")) {
			$0.name = "Joe"
		}.finish()

		_ = await store.send(.saveButtonTapped) {
			$0.isSaving = true
		}

		await store.receive(.saveBowlerResult(.failure(MockError.mock))) {
			$0.isSaving = false
		}
	}

	func testCreateHasChanges() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		XCTAssertFalse(store.state.hasChanges)

		await store.send(.nameChange("Joe")) {
			$0.name = "Joe"
		}.finish()

		XCTAssertTrue(store.state.hasChanges)
	}

	func testCreateCanSave() async {
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .create),
			reducer: BowlerEditor()
		)

		XCTAssertFalse(store.state.canSave)

		await store.send(.nameChange("Joe")) {
			$0.name = "Joe"
		}.finish()

		XCTAssertTrue(store.state.canSave)
	}

	func testEditHasChanges() async {
		let mockBowler = Bowler(id: UUID(), name: "Joseph")
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .edit(mockBowler)),
			reducer: BowlerEditor()
		)

		XCTAssertFalse(store.state.name.isEmpty)
		XCTAssertFalse(store.state.hasChanges)

		await store.send(.nameChange("Joe")) {
			$0.name = "Joe"
		}.finish()

		XCTAssertTrue(store.state.hasChanges)
	}

	func testEditCanSave() async {
		let mockBowler = Bowler(id: UUID(), name: "Joseph")
		let store = TestStore(
			initialState: BowlerEditor.State(mode: .edit(mockBowler)),
			reducer: BowlerEditor()
		)

		XCTAssertFalse(store.state.name.isEmpty)
		XCTAssertFalse(store.state.canSave)

		await store.send(.nameChange("Joe")) {
			$0.name = "Joe"
		}.finish()

		XCTAssertTrue(store.state.canSave)
	}
}

private enum MockError: Error {
	case mock
}
