import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsMocksLibrary
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
		let bowler: Bowler = .mock(id: id0)

		store.dependencies.persistenceService.fetchBowlers = { _ in [bowler] }
		store.dependencies.recentlyUsedService.didRecentlyUseResource = { _, _ in }

		let task = await store.send(.refreshList)

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
		let bowler: Bowler = .mock(id: id0)

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
		let bowler: Bowler = .mock(id: id0)

		await store.send(.swipeAction(bowler, .delete)) {
			$0.alert = BowlersList.alert(toDelete: bowler)
		}
	}

	func testCreatesBowler() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		await store.send(.setEditorFormSheet(isPresented: true)) {
			$0.bowlerEditor = .init(mode: .create)
		}

		await store.send(.setEditorFormSheet(isPresented: false)) {
			$0.bowlerEditor = nil
		}
	}

	func testNavigatesToBowlerLeague() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		store.dependencies.recentlyUsedService.didRecentlyUseResource = { _, _ in }

		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let bowler: Bowler = .mock(id: id0)

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
		let bowler: Bowler = .mock(id: id0)

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
		let bowler: Bowler = .mock(id: id0)

		await store.send(.swipeAction(bowler, .edit)) {
			$0.bowlerEditor = .init(mode: .edit(bowler))
		}

		await store.send(.bowlerEditor(.form(.saveModelResult(.success(bowler))))) {
			$0.bowlerEditor = nil
		}

		await store.send(.swipeAction(bowler, .edit)) {
			$0.bowlerEditor = .init(mode: .edit(bowler))
		}

		await store.send(.bowlerEditor(.form(.deleteModelResult(.success(bowler))))) {
			$0.bowlerEditor = nil
		}
	}
}
