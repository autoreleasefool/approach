import ComposableArchitecture
import Foundation
import XCTest
@testable import LaneEditorFeature

@MainActor
final class LaneEditorTests: XCTestCase {
	func testTogglesAgainstWall() async {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
		let store = TestStore(
			initialState: LaneEditor.State(id: id0),
			reducer: LaneEditor()
		)

		XCTAssertFalse(store.state.isAgainstWall)

		await store.send(.set(\.$isAgainstWall, true)) {
			$0.isAgainstWall = true
		}
	}
}
