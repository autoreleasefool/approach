import ComposableArchitecture
import XCTest
@testable import ResourceListLibrary

@MainActor
final class ResourceListTests: XCTestCase {
	func testHasDeleteFeature() {
		func onDelete(model: Model) async throws -> Void {}
		let state = ResourceList<Model, Query>.State(
			features: [
				.swipeToDelete(onDelete: .init(onDelete(model:))),
			],
			query: .init()
		)

		XCTAssertNotNil(state.onDelete)
		XCTAssertTrue(state.hasDeleteFeature)
	}

	func testHasNoDeleteFeautre() {
		let state = ResourceList<Model, Query>.State(
			features: [
				.swipeToEdit,
				.add
			],
			query: .init()
		)

		XCTAssertNil(state.onDelete)
		XCTAssertFalse(state.hasDeleteFeature)
	}

	func testObservesData() async {
		let store = TestStore(
			initialState: ResourceList<Model, Query>.State(
				features: [],
				query: .init()
			),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.didObserveData))

		await store.receive(.internal(.observeData))

		await store.send(.internal(.cancelObservation))
	}

	func testErrorButtonReloadsData() async {
		let store = TestStore(
			initialState: ResourceList<Model, Query>.State(
				features: [],
				query: .init()
			),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.internal(.resourcesResponse(.failure(MockError())))) {
			$0.error = .failedToLoad
		}

		await store.send(.view(.didTapErrorButton))

		await store.receive(.internal(.observeData)) {
			$0.error = nil
		}

		await store.send(.internal(.cancelObservation))
	}

	func testSwipeToDeleteDisplaysAlert() async {
		func onDelete(model: Model) async throws -> Void {}
		let store = TestStore(
			initialState: ResourceList<Model, Query>.State(
				features: [
					.swipeToDelete(onDelete: .init(onDelete(model:))),
				],
				query: .init()
			),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.didSwipeToDelete(.mock))) {
			$0.alert = ResourceList<Model, Query>.alert(toDelete: .mock)
		}
	}

	func testAddButtonCallsDelegate() async {
		let store = TestStore(
			initialState: ResourceList<Model, Query>.State(
				features: [.add],
				query: .init()
			),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.didTapAddButton))

		await store.receive(.delegate(.didAddNew))
	}

	func testDeleteButtonDeletesModel() async {
		let expectation = self.expectation(description: "Model deleted")
		func onDelete(model: Model) async throws -> Void {
			XCTAssertEqual(model, .mock)
			expectation.fulfill()
		}

		let store = TestStore(
			initialState: ResourceList<Model, Query>.State(
				features: [
					.swipeToDelete(onDelete: .init(onDelete(model:))),
				],
				query: .init()
			),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.alert(.deleteButtonTapped(.mock))))

		await store.receive(.internal(.deleteResponse(.success(.mock))))

		await store.receive(.delegate(.didDelete(.mock)))

		waitForExpectations(timeout: 1)
	}

	func testDeleteButtonWhenErrorThrownShowsError() async {
		func onDelete(model: Model) async throws -> Void {
			XCTAssertEqual(model, .mock)
			throw MockError()
		}

		let store = TestStore(
			initialState: ResourceList<Model, Query>.State(
				features: [
					.swipeToDelete(onDelete: .init(onDelete(model:))),
				],
				query: .init()
			),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.alert(.deleteButtonTapped(.mock))))

		await store.receive(.internal(.deleteResponse(.failure(MockError())))) {
			$0.error = .failedToDelete
		}
	}

	func testDismissDeleteAlert() async {
		func onDelete(model: Model) async throws -> Void {}
		let store = TestStore(
			initialState: ResourceList<Model, Query>.State(
				features: [
					.swipeToDelete(onDelete: .init(onDelete(model:))),
				],
				query: .init()
			),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.didSwipeToDelete(.mock))) {
			$0.alert = ResourceList<Model, Query>.alert(toDelete: .mock)
		}

		await store.send(.view(.alert(.dismissed))) {
			$0.alert = nil
		}
	}

	func testSwipeToEditCallsDelegate() async {
		let store = TestStore(
			initialState: ResourceList<Model, Query>.State(
				features: [.swipeToEdit],
				query: .init()
			),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.didSwipeToEdit(.mock)))

		await store.receive(.delegate(.didEdit(.mock)))
	}


	func testCancelObservation() async {
		let store = TestStore(
			initialState: ResourceList<Model, Query>.State(
				features: [],
				query: .init()
			),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.didObserveData))

		await store.receive(.internal(.observeData))

		await store.send(.internal(.cancelObservation))
	}

	func _testSwipeToDeleteWhenNoDeleteFeatureThrowsError() async {
		// TODO: enable _testSwipeToDeleteWhenNoDeleteFeatureThrowsError
	}

	func _testDeleteButtonWhenNoDeleteFeatureThrowsError() async {
		// TODO: enable testDeleteButtonWhenNoDeleteFeatureThrowsError
	}

	func _testSwipeToEditWhenNoEditFeatureThrowsError() async {
		// TODO: enable _testSwipeToEditWhenNoEditFeatureThrowsError
	}

	func _testAddButtonWhenNoAddFeatureThrowsError() async {
		// TODO: enable _testAddButtonWhenNoAddFeatureThrowsError
	}
}

private struct Model: ResourceListItem {
	let name: String
	var id: String { name }

	static var mock: Self { .init(name: "mock") }
}

private struct Query: Equatable {}

private struct MockError: Equatable, Error {}
