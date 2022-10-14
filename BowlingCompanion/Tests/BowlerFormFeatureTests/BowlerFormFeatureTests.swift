import BowlerFormFeature
import ComposableArchitecture
import SharedModelsLibrary
import XCTest

@MainActor
final class BowlerFormFeatureTests: XCTestCase {
	func testChangesName() async {
		let store = TestStore(
			initialState: BowlerForm.State(mode: .create),
			reducer: BowlerForm()
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
			initialState: BowlerForm.State(mode: .edit(mockBowler)),
			reducer: BowlerForm()
		)

		XCTAssertEqual(store.state.name, "Joseph")
	}

	func testSaveButtonStartsTask() async {
		let store = TestStore(
			initialState: BowlerForm.State(mode: .create),
			reducer: BowlerForm()
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
}
