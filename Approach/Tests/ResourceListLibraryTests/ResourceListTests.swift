import ComposableArchitecture
import XCTest
@testable import ResourceListLibrary

@MainActor
final class ResourceListTests: XCTestCase {
	private func buildState(withFeatures: [ResourceList<Model, Query>.Feature]) -> ResourceList<Model, Query>.State {
		.init(
			features: withFeatures,
			query: .init(),
			listTitle: "listTitle",
			emptyContent: .init(image: .emptyBowlers, title: "title", action: "action")
		)
	}

	func testHasDeleteFeature() {
		func onDelete(model: Model) async throws -> Void {}
		let state = buildState(withFeatures: [.swipeToDelete(onDelete: .init(onDelete(model:)))])

		XCTAssertNotNil(state.onDelete)
		XCTAssertTrue(state.hasDeleteFeature)
	}

	func testHasNoDeleteFeautre() {
		let state = buildState(
			withFeatures: [
				.swipeToEdit,
				.add,
			]
		)

		XCTAssertNil(state.onDelete)
		XCTAssertFalse(state.hasDeleteFeature)
	}

	func testObservesData() async {
		let store = TestStore(
			initialState: buildState(withFeatures: []),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.didObserveData))

		await store.send(.internal(.cancelObservation))
	}

	func testErrorButtonReloadsData() async {
		let store = TestStore(
			initialState: buildState(withFeatures: []),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.internal(.resourcesResponse(.failure(MockError())))) {
			$0.error = .failedToLoad
		}

		await store.send(.view(.empty(.delegate(.didTapButton)))) {
			$0.error = nil
		}

		await store.send(.internal(.cancelObservation))
	}

	func testSwipeToDeleteDisplaysAlert() async {
		func onDelete(model: Model) async throws -> Void {}
		let store = TestStore(
			initialState: buildState(withFeatures: [.swipeToDelete(onDelete: .init(onDelete(model:)))]),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.didSwipeToDelete(.mock))) {
			$0.alert = ResourceList<Model, Query>.alert(toDelete: .mock)
		}
	}

	func testAddButtonCallsDelegate() async {
		let store = TestStore(
			initialState: buildState(withFeatures: [.add]),
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
			initialState: buildState(withFeatures: [.swipeToDelete(onDelete: .init(onDelete(model:)))]),
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
			initialState: buildState(withFeatures: [.swipeToDelete(onDelete: .init(onDelete(model:)))]),
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
			initialState: buildState(withFeatures: [.swipeToDelete(onDelete: .init(onDelete(model:)))]),
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
			initialState: buildState(withFeatures: [.swipeToEdit]),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.didSwipeToEdit(.mock)))

		await store.receive(.delegate(.didEdit(.mock)))
	}


	func testCancelObservation() async {
		let store = TestStore(
			initialState: buildState(withFeatures: []),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		await store.send(.view(.didObserveData))

		await store.send(.internal(.cancelObservation))
	}

	func testActionButtonWhenNoErrorIsEmptyStateButton() async {
		let store = TestStore(
			initialState: buildState(withFeatures: []),
			reducer: ResourceList<Model, Query> { _ in .never }
		)

		XCTAssertNil(store.state.error)

		await store.send(.view(.empty(.delegate(.didTapButton))))

		await store.receive(.delegate(.didTapEmptyStateButton))
	}

	func testActionButtonWhenErrorIsHandled() async {
		let store = TestStore(
			initialState: buildState(withFeatures: []),
			reducer: ResourceList<Model, Query> { _ in .finished(throwing: MockError()) }
		)

		XCTAssertNil(store.state.error)

		await store.send(.internal(.observeData))

		await store.receive(.internal(.resourcesResponse(.failure(MockError())))) {
			$0.error = .failedToLoad
		}

		await store.send(.view(.empty(.delegate(.didTapButton)))) {
			$0.error = nil
		}

		await store.receive(.internal(.resourcesResponse(.failure(MockError())))) {
			$0.error = .failedToLoad
		}
	}

	func testTappingRowSendsAction() async {
		let (models, modelsContinuation) = AsyncThrowingStream<[Model], Error>.streamWithContinuation()

		let store = TestStore(
			initialState: buildState(withFeatures: [.tappable]),
			reducer: ResourceList<Model, Query> { _ in models }
		)

		await store.send(.internal(.observeData))

		modelsContinuation.yield([.mock])

		await store.receive(.internal(.resourcesResponse(.success([.mock])))) {
			$0.resources = .init(uniqueElements: [.mock])
		}

		await store.send(.view(.didTap(.mock)))

		await store.receive(.delegate(.didTap(.mock)))

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

	func _testTappingRowWhenNoTappableFeatureThrowsError() async {
		// TODO: enable _testTappingRowWhenNoTappableFeatureThrowsError
	}
}

private struct Model: ResourceListItem {
	let name: String
	var id: String { name }

	static var mock: Self { .init(name: "mock") }
}

private struct Query: Equatable {}

private struct MockError: Equatable, Error {}
