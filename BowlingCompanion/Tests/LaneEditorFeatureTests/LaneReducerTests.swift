import ComposableArchitecture
import Foundation
import XCTest
@testable import LaneEditorFeature

@MainActor
final class LaneReducerTests: XCTestCase {
	func testEmptyLaneIsValid() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let store = TestStore(
			initialState: Lane.State(id: id0, isShowingAgainstWallNotice: true),
			reducer: Lane()
		)

		await store.send(.set(\.$label, ""))

		XCTAssertTrue(store.state.isValid)
		XCTAssertNil(store.state.laneLabel)
	}

	func testNonNumericLaneIsNotValid() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let store = TestStore(
			initialState: Lane.State(id: id0, isShowingAgainstWallNotice: true),
			reducer: Lane()
		)

		await store.send(.set(\.$label, "test")) {
			$0.label = "test"
		}

		XCTAssertFalse(store.state.isValid)
		XCTAssertNil(store.state.laneLabel)
	}

	func testNumericLaneIsValid() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let store = TestStore(
			initialState: Lane.State(id: id0, isShowingAgainstWallNotice: true),
			reducer: Lane()
		)

		await store.send(.set(\.$label, "1")) {
			$0.label = "1"
		}

		XCTAssertTrue(store.state.isValid)
		XCTAssertEqual(store.state.laneLabel, 1)
	}

	func testTogglesAgainstWall() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let store = TestStore(
			initialState: Lane.State(id: id0, isShowingAgainstWallNotice: true),
			reducer: Lane()
		)

		XCTAssertFalse(store.state.isAgainstWall)

		await store.send(.set(\.$isAgainstWall, true)) {
			$0.isAgainstWall = true
		}
	}
}
