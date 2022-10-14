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
}
