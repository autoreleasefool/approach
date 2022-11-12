import ComposableArchitecture
import SharedModelsLibrary
import XCTest
@testable import BowlersListFeature

@MainActor
final class BowlersListTests: XCTestCase {

	func testSubscribesToBowlers() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler = Bowler(id: id0, name: "Joseph")

		let (bowlers, continuation) = AsyncThrowingStream<[Bowler], Error>.streamWithContinuation()
		store.dependencies.persistenceService.fetchBowlers = { _ in bowlers }

		let task = await store.send(.subscribeToBowlers)

		continuation.yield([bowler])

		await store.receive(.bowlersResponse(.success([bowler]))) {
			$0.bowlers = [bowler]
		}

		await task.cancel()
	}

	func testSwipeToEditBowler() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler = Bowler(id: id0, name: "Joseph")

		await store.send(.swipeAction(bowler, .edit)) {
			$0.bowlerEditor = .init(mode: .edit(bowler))
		}
	}

	func testSwipeToDeleteBowler() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler = Bowler(id: id0, name: "Joseph")

		await store.send(.swipeAction(bowler, .delete)) {
			$0.alert = BowlersList.alert(toDelete: bowler)
		}
	}

	func testCreatesBowler() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		await store.send(.setFormSheet(isPresented: true)) {
			$0.bowlerEditor = .init(mode: .create)
		}

		await store.send(.setFormSheet(isPresented: false)) {
			$0.bowlerEditor = nil
		}
	}

	func testNavigatesToBowlerLeague() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler = Bowler(id: id0, name: "Joseph")

		await store.send(.bowlersResponse(.success([bowler]))) {
			$0.bowlers = .init(uniqueElements: [bowler])
		}

		await store.send(.setNavigation(selection: id0)) {
			$0.selection = Identified(.init(bowler: bowler), id: id0)
		}

		await store.send(.setNavigation(selection: nil)) {
			$0.selection = nil
		}
	}

	func testDoesNotNavigateToMissingBowler() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!

		await store.send(.setNavigation(selection: id0)).finish()
	}

	func testDeletesBowler() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler = Bowler(id: id0, name: "Joseph")

		let expectation = self.expectation(description: "deleted")
		store.dependencies.persistenceService.deleteBowler = { deleted in
			XCTAssertEqual(deleted, bowler)
			expectation.fulfill()
		}

		await store.send(.alert(.deleteButtonTapped(bowler)))

		await store.receive(.deleteBowlerResponse(.success(true)))

		waitForExpectations(timeout: 1)
	}

	func testHandlesEditorResults() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler = Bowler(id: id0, name: "Joseph")

		await store.send(.swipeAction(bowler, .edit)) {
			$0.bowlerEditor = .init(mode: .edit(bowler))
		}

		await store.send(.bowlerEditor(.form(.saveResult(.success(bowler))))) {
			$0.bowlerEditor = nil
		}

		await store.send(.swipeAction(bowler, .edit)) {
			$0.bowlerEditor = .init(mode: .edit(bowler))
		}

		await store.send(.bowlerEditor(.form(.deleteResult(.success(bowler))))) {
			$0.bowlerEditor = nil
		}
	}
}
