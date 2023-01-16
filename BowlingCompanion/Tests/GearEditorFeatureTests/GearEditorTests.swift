import BowlersDataProviderInterface
import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest
@testable import BaseFormLibrary
@testable import GearEditorFeature

@MainActor
final class GearEditorTests: XCTestCase {
	func testLoadsNothingWhenNoInitialData() async {
		let store = TestStore(
			initialState: GearEditor.State(mode: .create),
			reducer: GearEditor()
		)

		await store.send(.loadInitialData).finish()
	}

	func testLoadsInitialData() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let gear: Gear = .mock(bowler: id1, id: id0)
		let bowler: Bowler = .mock(id: id1)

		await DependencyValues.withValues {
			$0.bowlersDataProvider.fetchBowlers = { request in
				XCTAssertEqual(request, .init(filter: [.id(id1)], ordering: .byName))
				return [.mock(id: id1)]
			}
		} operation: {
			let store = TestStore(
				initialState: GearEditor.State(mode: .edit(gear)),
				reducer: GearEditor()
			)

			await store.send(.loadInitialData).finish()

			await store.receive(.bowlerResponse(.success(bowler))) {
				$0.initialBowler = bowler
			}
		}
	}

	func testChangesName() async {
		let store = TestStore(
			initialState: GearEditor.State(mode: .create),
			reducer: GearEditor()
		)

		XCTAssertEqual(store.state.base.form.name, "")

		await store.send(.set(\.base.form.$name, "J")) {
			$0.base.form.name = "J"
		}
		await store.send(.set(\.base.form.$name, "Jo")) {
			$0.base.form.name = "Jo"
		}
	}

	func testChangesKind() async {
		let store = TestStore(
			initialState: GearEditor.State(mode: .create),
			reducer: GearEditor()
		)

		XCTAssertEqual(store.state.base.form.kind, .bowlingBall)

		await store.send(.set(\.base.form.$kind, .towel)) {
			$0.base.form.kind = .towel
		}
		await store.send(.set(\.base.form.$kind, .shoes)) {
			$0.base.form.kind = .shoes
		}
	}

	func testChangesBowler() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!

		let store = TestStore(
			initialState: GearEditor.State(mode: .create),
			reducer: GearEditor()
		)

		XCTAssertEqual(store.state.base.form.bowlerPicker.selected, [])

		await store.send(.bowlerPicker(.resourceTapped(.mock(id: id0)))) {
			$0.base.form.bowlerPicker.selected = [id0]
		}

		await store.receive(.bowlerPicker(.saveButtonTapped)) {
			$0.base.form.bowlerPicker.initialSelection = [id0]
		}
	}

	func testDiscardChanges() async {
		let store = TestStore(
			initialState: GearEditor.State(mode: .create),
			reducer: GearEditor()
		)

		XCTAssertEqual(store.state.base.form.name, "")
		XCTAssertEqual(store.state.base.form.kind, .bowlingBall)

		await store.send(.set(\.base.form.$name, "J")) {
			$0.base.form.name = "J"
		}
		await store.send(.set(\.base.form.$kind, .towel)) {
			$0.base.form.kind = .towel
		}
		await store.send(.form(.discardButtonTapped)) {
			$0.base.alert = $0.base.discardAlert
		}
		await store.send(.form(.alert(.discardButtonTapped))) {
			$0.base.alert = nil
			$0.base.form.name = ""
			$0.base.form.kind = .bowlingBall
		}
	}

	func testDeleteGear() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let gear: Gear = .mock(id: id0)

		let store = TestStore(
			initialState: GearEditor.State(mode: .edit(gear)),
			reducer: GearEditor()
		)

		let expectation = self.expectation(description: "deleted")
		store.dependencies.persistenceService.deleteGear = { deleted in
			XCTAssertEqual(deleted, gear)
			expectation.fulfill()
		}

		await store.send(.form(.deleteButtonTapped)) {
			$0.base.alert = $0.base.deleteAlert
		}

		let task = await store.send(.form(.alert(.deleteButtonTapped))) {
			$0.base.alert = nil
			$0.base.isLoading = true
		}

		await store.receive(.form(.deleteResult(.success(gear))))

		await task.finish()

		waitForExpectations(timeout: 1)
	}

	func testGearRequiresName() async {
		let store = TestStore(
			initialState: GearEditor.State(mode: .create),
			reducer: GearEditor()
		)

		store.dependencies.persistenceService.createGear = { _ in
			XCTFail("Should not save")
		}

		await store.send(.form(.saveButtonTapped)).finish()
	}

	func testNewGearIsCreated() async {
		let store = TestStore(
			initialState: GearEditor.State(mode: .create),
			reducer: GearEditor()
		)

		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let id1 = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let gear: Gear = .mock(bowler: id1, id: id0, name: "Ball", kind: .towel)

		store.dependencies.uuid = .incrementing

		await store.send(.set(\.base.form.$name, "Ball")) {
			$0.base.form.name = "Ball"
		}
		await store.send(.set(\.base.form.$kind, .towel)) {
			$0.base.form.kind = .towel
		}
		await store.send(.bowlerPicker(.resourceTapped(.mock(id: id1)))) {
			$0.base.form.bowlerPicker.selected = [id1]
		}
		await store.receive(.bowlerPicker(.saveButtonTapped)) {
			$0.base.form.bowlerPicker.initialSelection = [id1]
		}

		let expectation = self.expectation(description: "created")
		store.dependencies.persistenceService.createGear = { created in
			XCTAssertEqual(created, gear)
			expectation.fulfill()
		}

		let task = await store.send(.form(.saveButtonTapped)) {
			$0.base.isLoading = true
		}
		await store.receive(.form(.saveModelResult(.success(gear))))
		await task.finish()

		waitForExpectations(timeout: 1)
	}

	func testExistingGearIsUpdated() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let gear: Gear = .mock(id: id0, name: "Red")
		let gear2: Gear = .mock(id: id0, name: "Blue")

		let store = TestStore(
			initialState: GearEditor.State(mode: .edit(gear)),
			reducer: GearEditor()
		)

		await store.send(.set(\.base.form.$name, "Blue")) {
			$0.base.form.name = "Blue"
		}

		let expectation = self.expectation(description: "created")
		store.dependencies.persistenceService.updateGear = { updated in
			XCTAssertEqual(updated, gear2)
			expectation.fulfill()
		}

		let task = await store.send(.form(.saveButtonTapped)) {
			$0.base.isLoading = true
		}
		await store.receive(.form(.saveModelResult(.success(gear2))))
		await task.finish()

		waitForExpectations(timeout: 1)
	}

	func testDeletedGearMustExist() async {
		let store = TestStore(
			initialState: GearEditor.State(mode: .create),
			reducer: GearEditor()
		)

		await store.send(.form(.alert(.deleteButtonTapped)))
		await store.send(.form(.deleteButtonTapped))
	}

	func testErrorSavingUpdatesStates() async {
		let store = TestStore(
			initialState: GearEditor.State(mode: .create),
			reducer: GearEditor()
		)

		store.dependencies.uuid = .incrementing
		store.dependencies.persistenceService.createGear = { _ in
			throw MockError.mock
		}

		await store.send(.set(\.base.form.$name, "Red")) {
			$0.base.form.name = "Red"
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
			initialState: GearEditor.State(mode: .create),
			reducer: GearEditor()
		)

		XCTAssertFalse(store.state.base.hasChanges)

		await store.send(.set(\.base.form.$name, "Blue")) {
			$0.base.form.name = "Blue"
		}

		XCTAssertTrue(store.state.base.hasChanges)
	}

	func testCreateIsSaveable() async {
		let store = TestStore(
			initialState: GearEditor.State(mode: .create),
			reducer: GearEditor()
		)

		XCTAssertFalse(store.state.base.isSaveable)

		await store.send(.set(\.base.form.$name, "Yellow")) {
			$0.base.form.name = "Yellow"
		}

		XCTAssertTrue(store.state.base.isSaveable)
	}

	func testEditHasChanges() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let gear: Gear = .mock(id: id0)

		let store = TestStore(
			initialState: GearEditor.State(mode: .edit(gear)),
			reducer: GearEditor()
		)

		XCTAssertFalse(store.state.base.hasChanges)

		await store.send(.set(\.base.form.$name, "Red")) {
			$0.base.form.name = "Red"
		}

		XCTAssertTrue(store.state.base.hasChanges)
	}

	func testEditIsSaveable() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let gear: Gear = .mock(id: id0)

		let store = TestStore(
			initialState: GearEditor.State(mode: .edit(gear)),
			reducer: GearEditor()
		)

		XCTAssertFalse(store.state.base.isSaveable)

		await store.send(.set(\.base.form.$name, "")) {
			$0.base.form.name = ""
		}
		XCTAssertFalse(store.state.base.isSaveable)
		await store.send(.set(\.base.form.$name, "Red")) {
			$0.base.form.name = "Red"
		}
		XCTAssertTrue(store.state.base.isSaveable)
	}
}

enum MockError: Error {
	case mock
}
