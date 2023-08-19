import ComposableArchitecture
@testable import FormFeature
import XCTest

typealias TestForm = Form<Createable, Editable>

@MainActor
final class FormFeatureTests: XCTestCase {

	// MARK: Saving

	static let id = UUID(uuidString: "00000000-0000-0000-0000-000000000001")

	func test_WhenHasNoChanges_SaveButtonIsDisabled() {
		let initialValue: TestForm.Value = .create(.init())

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: initialValue),
			reducer: TestForm()
		)

		XCTAssertFalse(store.state.hasChanges)
		XCTAssertFalse(store.state.isSaveable)
	}

	func test_WhenNotIsSaveable_SaveButtonIsDisabled() {
		let initialValue: TestForm.Value = .create(.init(isSaveable: false, name: "A"))
		let value: TestForm.Value = .create(.init(isSaveable: false, name: "B"))

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: value),
			reducer: TestForm()
		)

		XCTAssertTrue(store.state.hasChanges)
		XCTAssertFalse(store.state.isSaveable)
	}

	func test_WhenHasChanges_AndIsSaveable_SaveButtonIsEnabled() {
		let initialValue: TestForm.Value = .create(.init(isSaveable: false, name: "A"))
		let value: TestForm.Value = .create(.init(isSaveable: true, name: "B"))

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: value),
			reducer: TestForm()
		)

		XCTAssertTrue(store.state.hasChanges)
		XCTAssertTrue(store.state.isSaveable)
	}

	func test_WhenNotIsSaveable_AndSaveButtonTapped_DoesNothing() async {
		let initialValue: TestForm.Value = .create(.init(isSaveable: false, name: "A"))
		let value: TestForm.Value = .create(.init(isSaveable: false, name: "B"))

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: value),
			reducer: TestForm()
		)

		await store.send(.view(.didTapSaveButton))
	}

	func test_WhenCreating_WhenSaveButtonTapped_CreatesRecord() async throws {
		let initialValue: TestForm.Value = .create(.init(isSaveable: false, name: "A"))
		let value: TestForm.Value = .create(.init(isSaveable: true, name: "B"))

		let expectation = self.expectation(description: "record created")
		let store = withDependencies {
			$0.records.create = {
				try XCTAssertEqual(XCTUnwrap(value.record as? Createable), XCTUnwrap($0 as? Createable))
				expectation.fulfill()
			}
		} operation: {
			TestStore(
				initialState: TestForm.State(initialValue: initialValue, currentValue: value),
				reducer: TestForm()
			)
		}

		await store.send(.view(.didTapSaveButton)) {
			$0.isLoading = true
		}

		await fulfillment(of: [expectation])

		try await store.receive(.delegate(.didCreate(.success(XCTUnwrap(value.record as? Createable)))))
	}

	func test_WhenEditing_WhenSaveButtonTapped_UpdatesRecord() async throws {
		let initialValue: TestForm.Value = .edit(.init(isSaveable: false, name: "A"))
		let value: TestForm.Value = .edit(.init(isSaveable: true, name: "B"))

		let expectation = self.expectation(description: "record updated")
		let store = withDependencies {
			$0.records.update = {
				try XCTAssertEqual(XCTUnwrap(value.record as? Editable), XCTUnwrap($0 as? Editable))
				expectation.fulfill()
			}
		} operation: {
			TestStore(
				initialState: TestForm.State(initialValue: initialValue, currentValue: value),
				reducer: TestForm()
			)
		}

		await store.send(.view(.didTapSaveButton)) {
			$0.isLoading = true
		}

		await fulfillment(of: [expectation])

		try await store.receive(.delegate(.didUpdate(.success(XCTUnwrap(value.record as? Editable)))))
	}

	// MARK: Deleting

	func test_WhenCreating_WhenDeleteButtonIsTapped_DoesNothing() async {
		let initialValue: TestForm.Value = .create(.init())

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: initialValue),
			reducer: TestForm()
		)

		await store.send(.view(.didTapDeleteButton))
	}

	func test_WhenEditing_WhenDeleteIsDisabled_WhenDeleteButtonIsTapped_DoesNothing() async {
		let initialValue: TestForm.Value = .edit(.init())
		let value: TestForm.Value = .edit(.init(isDeleteable: false))

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: value),
			reducer: TestForm()
		)

		await store.send(.view(.didTapDeleteButton))
	}

	func test_WhenEditing_WhenDeleteButtonIsTapped_ShowsPrompt() async {
		let initialValue: TestForm.Value = .edit(.init())
		let value: TestForm.Value = .edit(.init(isDeleteable: true))

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: value),
			reducer: TestForm()
		)

		await store.send(.view(.didTapDeleteButton)) {
			$0.alert = AlertState {
				TextState("Are you sure you want to delete Existing?")
			} actions: {
				ButtonState(role: .destructive, action: .didTapDeleteButton) { TextState("Delete") }
				ButtonState(role: .cancel, action: .didTapCancelButton) { TextState("Cancel") }
			}
		}
	}

	func test_WhenDeletePromptDismissed_DoesNothing() async {
		let initialValue: TestForm.Value = .edit(.init())
		let value: TestForm.Value = .edit(.init(isDeleteable: true))

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: value),
			reducer: TestForm()
		)

		await store.send(.view(.didTapDeleteButton)) {
			$0.alert = AlertState {
				TextState("Are you sure you want to delete Existing?")
			} actions: {
				ButtonState(role: .destructive, action: .didTapDeleteButton) { TextState("Delete") }
				ButtonState(role: .cancel, action: .didTapCancelButton) { TextState("Cancel") }
			}
		}

		await store.send(.view(.alert(.presented(.didTapCancelButton)))) {
			$0.alert = nil
		}
	}

	func test_WhenDeletePromptConfirmed_DeletesRecord() async throws {
		let initialValue: TestForm.Value = .edit(.init())
		let value: TestForm.Value = .edit(.init(isDeleteable: true))

		let expectation = self.expectation(description: "record deleted")
		let store = withDependencies {
			$0.records.delete = {
				try XCTAssertEqual(XCTUnwrap(value.record?.id as? UUID), XCTUnwrap($0 as? UUID))
				expectation.fulfill()
			}
		} operation: {
			TestStore(
				initialState: TestForm.State(initialValue: initialValue, currentValue: value),
				reducer: TestForm()
			)
		}

		await store.send(.view(.didTapDeleteButton)) {
			$0.alert = AlertState {
				TextState("Are you sure you want to delete Existing?")
			} actions: {
				ButtonState(role: .destructive, action: .didTapDeleteButton) { TextState("Delete") }
				ButtonState(role: .cancel, action: .didTapCancelButton) { TextState("Cancel") }
			}
		}

		await store.send(.view(.alert(.presented(.didTapDeleteButton)))) {
			$0.alert = nil
			$0.isLoading = true
		}

		await fulfillment(of: [expectation])

		try await store.receive(.delegate(.didDelete(.success(XCTUnwrap(initialValue.record as? Editable)))))
	}

	// MARK: Discarding

	func test_WhenHasNoChanges_WhenDiscardButtonTapped_DoesNothing() async {
		let initialValue: TestForm.Value = .edit(.init())
		let value: TestForm.Value = .edit(.init())

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: value),
			reducer: TestForm()
		)

		XCTAssertFalse(store.state.hasChanges)

		await store.send(.view(.didTapDiscardButton))
	}

	func test_WhenDiscardButtonTapped_ShowsPrompt() async {
		let initialValue: TestForm.Value = .edit(.init(name: "A"))
		let value: TestForm.Value = .edit(.init(name: "B"))

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: value),
			reducer: TestForm()
		)

		XCTAssertTrue(store.state.hasChanges)

		await store.send(.view(.didTapDiscardButton)) {
			$0.alert = AlertState {
				TextState("Discard your changes?")
				} actions: {
					ButtonState(role: .destructive, action: .didTapDiscardButton) { TextState("Discard") }
					ButtonState(role: .cancel, action: .didTapCancelButton) { TextState("Cancel") }
				}
		}
	}

	func test_WhenDiscardPromptDismissed_DoesNothing() async {
		let initialValue: TestForm.Value = .edit(.init(name: "A"))
		let value: TestForm.Value = .edit(.init(name: "B"))

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: value),
			reducer: TestForm()
		)

		XCTAssertTrue(store.state.hasChanges)

		await store.send(.view(.didTapDiscardButton)) {
			$0.alert = AlertState {
				TextState("Discard your changes?")
				} actions: {
					ButtonState(role: .destructive, action: .didTapDiscardButton) { TextState("Discard") }
					ButtonState(role: .cancel, action: .didTapCancelButton) { TextState("Cancel") }
				}
		}

		await store.send(.view(.alert(.presented(.didTapCancelButton)))) {
			$0.alert = nil
		}
	}

	func test_WhenDiscardPromptConfirmed_ResetsState() async {
		let initialValue: TestForm.Value = .edit(.init(name: "A"))
		let value: TestForm.Value = .edit(.init(name: "B"))

		let store = TestStore(
			initialState: TestForm.State(initialValue: initialValue, currentValue: value),
			reducer: TestForm()
		)

		XCTAssertTrue(store.state.hasChanges)

		await store.send(.view(.didTapDiscardButton)) {
			$0.alert = AlertState {
				TextState("Discard your changes?")
				} actions: {
					ButtonState(role: .destructive, action: .didTapDiscardButton) { TextState("Discard") }
					ButtonState(role: .cancel, action: .didTapCancelButton) { TextState("Cancel") }
				}
		}

		await store.send(.view(.alert(.presented(.didTapDiscardButton)))) {
			$0.alert = nil
			$0.value = initialValue
		}

		await store.receive(.delegate(.didDiscard))
	}
}

struct Createable: CreateableRecord {
	static var modelName = "Create"
	var id = FormLibraryTests.id
	var isSaveable = false
	var name = ""
}

struct Editable: EditableRecord {
	var id = FormLibraryTests.id
	var isDeleteable = false
	var isSaveable = false
	var name = "Existing"
}
