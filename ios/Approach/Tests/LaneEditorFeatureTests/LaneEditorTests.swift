import ComposableArchitecture
import Foundation
@testable import LaneEditorFeature
import XCTest

@MainActor
final class LaneEditorTests: XCTestCase {
	func testTogglesAgainstWall() async {
		let id0 = UUID(0)
		let store = TestStore(
			initialState: LaneEditor.State(id: id0, label: "1", position: .noWall),
			reducer: LaneEditor()
		)

		XCTAssertEqual(store.state.position, .noWall)

		await store.send(.set(\.$position, .leftWall)) {
			$0.position = .leftWall
		}
	}
}
