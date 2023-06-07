import ComposableArchitecture
@testable import SortOrderLibrary
import XCTest

enum MockOrderable: CaseIterable, Hashable, CustomStringConvertible {
	case first
	case second

	var description: String {
		switch self {
		case .first: return "First"
		case .second: return "Second"
		}
	}
}

@MainActor
final class SortOrderTests: XCTestCase {
	func testSetsInitialValue() {
		let state = SortOrder.State(initialValue: MockOrderable.second)
		XCTAssertEqual(state.ordering, .second)
	}

	func testDidTapOption_SetsOrdering_AndDismisses() async {
		let dismissed = self.expectation(description: "dismissed")

		let store = TestStore(initialState: SortOrder.State(initialValue: MockOrderable.first)) {
			SortOrder()
		} withDependencies: {
			$0.dismiss = .init { dismissed.fulfill() }
		}

		await store.send(.view(.didTapOption(.second))) {
			$0.ordering = .second
		}

		await store.receive(.delegate(.didTapOption(.second)))

		await fulfillment(of: [dismissed])
	}
}
