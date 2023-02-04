import ComposableArchitecture
import XCTest
@testable import SortOrderLibrary

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

	func testSetSheetPresented() async {
		let store = TestStore(
			initialState: SortOrder.State(initialValue: MockOrderable.first),
			reducer: SortOrder()
		)

		await store.send(.view(.setSheetPresented(isPresented: true))) {
			$0.isSheetPresented = true
		}

		await store.send(.view(.setSheetPresented(isPresented: false))) {
			$0.isSheetPresented = false
		}
	}

	func testDidTapOptionSetsOrdering() async {
		let store = TestStore(
			initialState: SortOrder.State(initialValue: MockOrderable.first),
			reducer: SortOrder()
		)

		await store.send(.view(.didTapOption(.second))) {
			$0.ordering = .second
			$0.isSheetPresented = false
		}

		await store.receive(.delegate(.didTapOption(.second)))
	}

	func testOptionTappedHidesSheet() async {
		let store = TestStore(
			initialState: SortOrder.State(initialValue: MockOrderable.first),
			reducer: SortOrder()
		)

		await store.send(.view(.setSheetPresented(isPresented: true))) {
			$0.isSheetPresented = true
		}

		await store.send(.view(.didTapOption(.second))) {
			$0.ordering = .second
			$0.isSheetPresented = false
		}

		await store.receive(.delegate(.didTapOption(.second)))
	}
}
