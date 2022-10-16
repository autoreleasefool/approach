import BowlersListFeature
import ComposableArchitecture
import SharedModelsLibrary
import XCTest

@MainActor
final class BowlersListFeatureTests: XCTestCase {
	func testLoadsBowlers() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		let mockBowler = Bowler(id: UUID(), name: "Bowler")
		let (bowlers, continuation) = AsyncStream<[Bowler]>.streamWithContinuation()
		store.dependencies.bowlersDataProvider.fetchAll = { bowlers }

		await _ = store.send(.onAppear)

		continuation.yield([mockBowler])

		await store.receive(.bowlersResponse(.success([mockBowler])), timeout: 1) {
			$0.bowlers = [mockBowler]
		}

		await store.send(.onDisappear).finish()
	}

	func testNavigatesToLeague() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		let mockBowler = Bowler(id: UUID(), name: "Bowler")

		await store.send(.bowlersResponse(.success([mockBowler]))) {
			$0.bowlers = .init(uniqueElements: [mockBowler])
		}.finish()

		await store.send(.setNavigation(selection: mockBowler.id)) {
			$0.selection = Identified(.init(bowler: mockBowler), id: mockBowler.id)
		}.finish()
	}

	func testDismissesNavigationToLeague() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		let mockBowler = Bowler(id: UUID(), name: "Bowler")

		await store.send(.bowlersResponse(.success([mockBowler]))) {
			$0.bowlers = .init(uniqueElements: [mockBowler])
		}.finish()

		await store.send(.setNavigation(selection: mockBowler.id)) {
			$0.selection = Identified(.init(bowler: mockBowler), id: mockBowler.id)
		}.finish()

		await store.send(.setNavigation(selection: nil)) {
			$0.selection = nil
		}.finish()
	}

	func testShowsBowlerForm() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		await store.send(.setFormSheet(isPresented: true)) {
			$0.bowlerForm = .init(mode: .create)
		}.finish()
	}

	func testDismissesBowlerForm() async {
		let store = TestStore(
			initialState: BowlersList.State(),
			reducer: BowlersList()
		)

		await store.send(.setFormSheet(isPresented: true)) {
			$0.bowlerForm = .init(mode: .create)
		}.finish()

		await store.send(.setFormSheet(isPresented: false)) {
			$0.bowlerForm = nil
		}.finish()
	}
}
